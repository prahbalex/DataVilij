package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
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
    private RadioButton class1;
    private Button classRun1;
    private RadioButton class2;
    private Button classRun2;
    private RadioButton class3;
    private Button classRun3;
    private RadioButton clust1;
    private Button clustRun1;
    private RadioButton clust2;
    private Button clustRun2;
    private RadioButton clust3;
    private Button clustRun3;
    private GridPane algButtons;
    private ToggleGroup buttons;

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

    public void disableAll(){
        metaData.setVisible(false);
        textArea.setDisable(false);
        Classification.setVisible(false);
        Clustering.setVisible(false);
        class1.setVisible(false);
        class2.setVisible(false);
        class3.setVisible(false);
        classRun1.setVisible(false);
        classRun2.setVisible(false);
        classRun3.setVisible(false);
        clust1.setVisible(false);
        clust2.setVisible(false);
        clust3.setVisible(false);
        clustRun1.setVisible(false);
        clustRun2.setVisible(false);
        clustRun3.setVisible(false);
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

        buttons = new ToggleGroup();

        algButtons = new GridPane();
        class1 = new RadioButton("Classification 1");
        class1.setToggleGroup(buttons);
        class1.setVisible(false);
        algButtons.add(class1, 0,0);

        classRun1 = new Button("Options");
        algButtons.add(classRun1, 1, 0);
        classRun1.setVisible(false);

        class2 = new RadioButton("Classification 2");
        algButtons.add(class2, 0,1);
        class2.setToggleGroup(buttons);
        class2.setVisible(false);

        classRun2 = new Button("Options");
        algButtons.add(classRun2, 1,1);
        classRun2.setVisible(false);

        class3 = new RadioButton("Classification 3");
        algButtons.add(class3, 0,2);
        class3.setToggleGroup(buttons);
        class3.setVisible(false);

        classRun3 = new Button("Options");
        algButtons.add(classRun3, 1,2);
        classRun3.setVisible(false);

        clust1 = new RadioButton("Clustering 1");
        algButtons.add(clust1, 0,3);
        clust1.setToggleGroup(buttons);
        clust1.setVisible(false);

        clustRun1 = new Button("Options");
        algButtons.add(clustRun1, 1, 3);
        clustRun1.setVisible(false);

        clust2 = new RadioButton("Clustering 2");
        algButtons.add(clust2, 0, 4);
        clust2.setToggleGroup(buttons);
        clust2.setVisible(false);

        clustRun2 = new Button("Options");
        algButtons.add(clustRun2, 1, 4);
        clustRun2.setVisible(false);

        clust3 = new RadioButton("Clustering 3");
        algButtons.add(clust3, 0, 5);
        clust3.setToggleGroup(buttons);
        clust3.setVisible(false);

        clustRun3 = new Button("Options");
        algButtons.add(clustRun3, 1, 5);
        clustRun3.setVisible(false);



        leftPanel.getChildren().addAll(leftPanelTitle, textArea, processButtonsBox, metaData,Clustering,Classification,algButtons);

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
        leftPanel.getChildren().add(checkBox);

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
                metaData.setVisible(false);
                textArea.setDisable(false);
                Classification.setVisible(false);
                Clustering.setVisible(false);
                class1.setVisible(false);
                class2.setVisible(false);
                class3.setVisible(false);
                classRun1.setVisible(false);
                classRun2.setVisible(false);
                classRun3.setVisible(false);
                clust1.setVisible(false);
                clust2.setVisible(false);
                clust3.setVisible(false);
                clustRun1.setVisible(false);
                clustRun2.setVisible(false);
                clustRun3.setVisible(false);
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
                if((((AppData) applicationTemplate.getDataComponent()).getNullLabel().get()==2))
                    Classification.setVisible(true);
                Clustering.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        Classification.setOnAction(event -> {
            class1.setVisible(true);
            class2.setVisible(true);
            class3.setVisible(true);
            classRun1.setVisible(true);
            classRun2.setVisible(true);
            classRun3.setVisible(true);
            Classification.setVisible(false);
        });
        Clustering.setOnAction(event -> {
            clust1.setVisible(true);
            clust2.setVisible(true);
            clust3.setVisible(true);
            clustRun1.setVisible(true);
            clustRun2.setVisible(true);
            clustRun3.setVisible(true);
            Clustering.setVisible(false);
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
