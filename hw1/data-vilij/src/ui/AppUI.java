package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;



/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display

    public ScatterChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // TODO for homework 1
        super.setToolBar(applicationTemplate);
        String Separator = applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SEPARATOR.name());
        String iconsPath = Separator + String.join(Separator,
                applicationTemplate.manager.getPropertyValue(AppPropertyTypes.GUI_RESOURCE_PATH.name()),
                applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ICONS_RESOURCE_PATH.name()));

        String scrnshoticonPath =
                String.join(Separator, iconsPath, applicationTemplate.manager.getPropertyValue
                        (AppPropertyTypes.SCREENSHOT_ICON.name()));
        scrnshotButton = new
                Button(null, new ImageView(new Image(getClass().getResourceAsStream(scrnshoticonPath))));
        scrnshotButton.getStyleClass().add
                (applicationTemplate.manager.getPropertyValue(AppPropertyTypes.TOOLBAR.name()));
        scrnshotButton.setTooltip(new
                Tooltip(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_TOOLTIP.name())));
        scrnshotButton.setDisable(true);
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
        chart.getData().clear();
        applicationTemplate.getDataComponent().clear();
        textArea.clear();
    }

    private void layout() {
        // TODO for homework 1
        workspace = new VBox();
        Label dataFile = new Label(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name()));
        textArea = new TextArea();
        textArea.setMaxSize(300,250);
        displayButton = new Button(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DISPLAY.name()));
        workspace.getChildren().addAll(dataFile,textArea,displayButton);
        appPane.getChildren().add(workspace);

        Label dataVisualization = new Label
                (applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name()));
        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(true);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        chart = new ScatterChart<>(xAxis,yAxis);
        appPane.getChildren().addAll(dataVisualization,chart);

        hasNewText = false;
    }

    private void setWorkspaceActions() {
        // TODO for homework 1
        displayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(hasNewText = false) return;
                ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
                ((AppData) applicationTemplate.getDataComponent()).displayData();
                hasNewText = false;
                applicationTemplate.getDataComponent().clear();
            }
        });
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(textArea.getText().isEmpty()){
                    newButton.setDisable(true);
                    saveButton.setDisable(true);
                    hasNewText = false;
                }
                else {
                    newButton.setDisable(false);
                    saveButton.setDisable(false);
                    hasNewText = true;
                }
            }
        });
    }
    public String getTextArea(){
        return textArea.getText();
    }
}
