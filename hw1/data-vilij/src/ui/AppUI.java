package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.io.IOException;

import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;

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
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private VBox leftPanel;
    private Text leftPanelTitle;
    private Button checkBox;
    private Text metaData;
    private Button Clustering;
    private Button Classification;

    public LineChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    public Button getClassification() {
        return Classification;
    }

    public Button getSave(){
        return saveButton;
    }

    public Button getClustering() {
        return Clustering;
    }

    public VBox getleftPanel(){
        return leftPanel;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public Text getLeftPanelTitle() {
        return leftPanelTitle;
    }

    public Button getDisplayButton() {
        return displayButton;
    }

    public Button getCheckBox() {
        return checkBox;
    }

    public Text getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData.setText(metaData);
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                                                   manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                                   manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String scrnshoticonPath = String.join(SEPARATOR,
                                              iconsPath,
                                              manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ICON.name()));
        scrnshotButton = setToolbarButton(scrnshoticonPath,
                                          manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_TOOLTIP.name()),
                                          true);
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                applicationTemplate.getActionComponent().handleSaveRequest();
                if(!((AppActions)(applicationTemplate.getActionComponent())).getIsUnsvaedProperty()){
                    saveButton.setDisable(true);
                }
            }
        });
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction((event) ->{
            try{
                ((AppActions)applicationTemplate.getActionComponent())
                        .handleScreenshotRequest();
            }catch (IOException e){}
        });

    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        textArea.clear();
        chart.getData().clear();
    }

    public String getCurrentText() { return textArea.getText(); }

    public void setCurrentText(String s) { textArea.setText(s);}

    private void layout() {

        appPane.getScene().getStylesheets().add("properties/yeet.css");
        PropertyManager manager = applicationTemplate.manager;
        NumberAxis      xAxis   = new NumberAxis();
        NumberAxis      yAxis   = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));

        leftPanel = new VBox(8);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(10));

        VBox.setVgrow(leftPanel, Priority.ALWAYS);
        leftPanel.setMaxSize(windowWidth * 0.29, windowHeight * 0.3);
        leftPanel.setMinSize(windowWidth * 0.29, windowHeight * 0.3);

        leftPanelTitle = new Text(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLE.name()));
        String fontname       = manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLEFONT.name());
        Double fontsize       = Double.parseDouble(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLESIZE.name()));
        leftPanelTitle.setFont(Font.font(fontname, fontsize));

        textArea = new TextArea();
        textArea.setMinHeight(180);

        Clustering = new Button("Clustering");
        Clustering.setVisible(false);
        Classification = new Button("Classification");
        Classification.setVisible(false);

        HBox processButtonsBox = new HBox();
        displayButton = new Button(manager.getPropertyValue(AppPropertyTypes.DISPLAY_BUTTON_TEXT.name()));
        HBox.setHgrow(processButtonsBox, Priority.ALWAYS);

        metaData = new Text();
        metaData.setVisible(false);



        leftPanel.getChildren().addAll(leftPanelTitle, textArea, processButtonsBox, metaData,Clustering,Classification);

        StackPane rightPanel = new StackPane(chart);
        rightPanel.setMaxSize(windowWidth * 0.69, windowHeight * 0.69);
        rightPanel.setMinSize(windowWidth * 0.69, windowHeight * 0.69);
        StackPane.setAlignment(rightPanel, Pos.CENTER);

        GridPane gridPane = new GridPane();
        gridPane.setTranslateX(-450);
        Text chartTitle = new Text("Plot");
        gridPane.getChildren().add(chartTitle);

        workspace = new HBox(leftPanel, rightPanel, gridPane);

        HBox.setHgrow(workspace, Priority.ALWAYS);

        appPane.getChildren().add(workspace);
        VBox.setVgrow(appPane, Priority.ALWAYS);

        checkBox = new Button("Done Editing");
        appPane.getChildren().add(checkBox);

        textArea.setVisible(false);
        displayButton.setVisible(false);
        checkBox.setVisible(false);
        chart.setVisible(false);
        leftPanelTitle.setVisible(false);
        newButton.setDisable(false);

    }

    private void setWorkspaceActions() {
        setTextAreaActions();
        setDisplayButtonActions();
        checkBox.setOnAction( event -> {
            if(textArea.isDisabled()){
                textArea.setDisable(false);
                return;
            }
            textArea.setDisable(true);
            try {
                chart.getData().clear();
                AppData dataComponent = (AppData) applicationTemplate.getDataComponent();
                dataComponent.clear();
                dataComponent.loadData(textArea.getText());
                if(((AppData) applicationTemplate.getDataComponent()).getError()){
                    return;
                }
                dataComponent.displayData();
                scrnshotButton.setDisable(false);
                this.metaData.setText(((AppData)applicationTemplate.getDataComponent()).getMeta());
                metaData.setVisible(true);
                Classification.setVisible(true);
                Clustering.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void setTextAreaActions() {
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.equals(oldValue)) {
                    if (!newValue.isEmpty()) {
                        ((AppActions) applicationTemplate.getActionComponent()).setIsUnsavedProperty(true);
                        if (newValue.charAt(newValue.length() - 1) == '\n')
                            hasNewText = true;
                        newButton.setDisable(false);
                        saveButton.setDisable(false);
                    } else {
                        hasNewText = true;
                        newButton.setDisable(true);
                        saveButton.setDisable(true);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                System.err.println(newValue);
            }
        });
    }

    private void setDisplayButtonActions() {
        displayButton.setOnAction(event -> {
            if (hasNewText) {
                try {
                    chart.getData().clear();
                    AppData dataComponent = (AppData) applicationTemplate.getDataComponent();
                    dataComponent.clear();
                    dataComponent.loadData(textArea.getText());
                    dataComponent.displayData();
                    scrnshotButton.setDisable(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}