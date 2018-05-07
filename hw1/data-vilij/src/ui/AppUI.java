package ui;

import Algorithms.DataSet;
import actions.AppActions;
import Algorithms.Algorithm;
import dataprocessors.AppData;
import Algorithms.classification.RandomClassifier;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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
    private Label metaData;
    private Button Clustering;
    private Button Classification;
    private GridPane algButtons;
    private ToggleGroup buttons;
    private Button run;
    private Algorithm algorithm;
    private ArrayList<Algorithm> algorithms;
    private Thread thread;
    private boolean classOrCluster; /// true == class
    private String key;
    private Button nextButton;
    private HashMap<String,ClusterConfig> clusterConfigHashMap = new HashMap<>();
    private HashMap<String,ClassificationConfig> classificationConfigHashMap = new HashMap<>();

    public ToggleGroup getButtons() {
        return buttons;
    }

    public GridPane getAlgButtons() {
        return algButtons;
    }

    public Button getNextButton(){
        return nextButton;
    }

    public Button getRun() {
        return run;
    }

    public Button getScrnshotButton() {
        return scrnshotButton;
    }

    public LineChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    public Thread getThread() {
        return thread;
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

    public Label getMetaData() {
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
        algButtons.getChildren().clear();
        run.setVisible(false);
        nextButton.setVisible(false);
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

        thread = new Thread();

        appPane.getScene().getStylesheets().add("properties/yeet.css");
        PropertyManager manager = applicationTemplate.manager;
        NumberAxis      xAxis   = new NumberAxis();
        NumberAxis      yAxis   = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        yAxis.setForceZeroInRange(false);
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

        metaData = new Label();
        metaData.setMinHeight(150);
        metaData.setPrefWidth(300);
        metaData.setWrapText(true);
        metaData.setVisible(false);

        buttons = new ToggleGroup();

        algButtons = new GridPane();

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

        run = new Button("Run");
        run.setVisible(false);
        leftPanel.getChildren().add(run);

        nextButton = new Button("Next");
        nextButton.setVisible(false);
        leftPanel.getChildren().add(nextButton);

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
                disableAll();
                run.setVisible(false);
                nextButton.setVisible(false);
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
                if((((AppData) applicationTemplate.getDataComponent()).getLabels().size()==2))
                    Classification.setVisible(true);
                Clustering.setVisible(true);
            } catch (Exception e) {
            }

        });
        Classification.setOnAction(event -> {
            try{
                run.setDisable(false);
                Class [] classes = getClasses("Algorithms.classification");
                int i = 0;
                for(Class c : classes){
                    String [] strings = c.getName().split("\\.");
                    String name = strings[strings.length -1];
                    Algorithm algorithm = (Algorithm) c.getConstructor().newInstance();
                    classificationConfigHashMap.put(name, new ClassificationConfig());
                    RadioButton radioButton = new RadioButton(name);
                    radioButton.setUserData(algorithm);
                    radioButton.setToggleGroup(buttons);
                    algButtons.add(radioButton, 0,i);
                    Button button = new Button("Options");
                    algButtons.add(button, 1, i);
                    radioButton.setOnAction(event1 -> {
                        if(classificationConfigHashMap.get(name).isConfigSet()) {
                            run.setVisible(true);

                        }
                        else {
                            run.setVisible(false);
                        }
                        classOrCluster = true;
                        key = name;
                    });
                    button.setOnAction(event1 -> {
                        Text text = new Text("Max Iteration");
                        Text text1 = new Text("Update Interval");
                        Text text2 = new Text("Continuous Run?");
                        Button save = new Button("Save");
                        ClassificationConfig a = classificationConfigHashMap.get(name);
                        Stage stage = new Stage();
                        GridPane gridPane = new GridPane();
                        Scene scene = new Scene(gridPane);
                        TextField textField = new TextField();
                        TextField textField1 = new TextField();
                        CheckBox checkBox = new CheckBox();
                        textField.setText(String.valueOf(a.getMaxIntegers()));
                        textField1.setText(String.valueOf(a.getUpdateInterval()));
                        checkBox.setSelected(a.isContinuousRun());
                        gridPane.add(text, 0, 0);
                        gridPane.add(text1, 0, 2);
                        gridPane.add(text2, 0, 4);
                        gridPane.add(save, 0, 5);
                        gridPane.add(textField, 1, 0);
                        gridPane.add(textField1, 1, 2);
                        gridPane.add(checkBox, 1, 4);
                        stage.setScene(scene);
                        stage.show();
                        save.setOnAction(event2 -> {
                            while(true) {
                                String error = "";
                                boolean errorBoolean = false;
                                try {
                                    try {
                                        a.setMaxIntegers(Integer.parseInt(textField.getText()));
                                        if (Integer.parseInt(textField.getText()) <= 0) {
                                            throw new Exception();
                                        }
                                    }catch (Exception e){
                                        error += "Invalid max Integers, reset to 1\n";
                                        errorBoolean = true;
                                        a.setMaxIntegers(1);
                                    }
                                    try {
                                        a.setUpdateInterval(Integer.parseInt(textField1.getText()));
                                        if (Integer.parseInt(textField1.getText()) <= 0) {
                                            throw new Exception();
                                        }
                                    }catch (Exception e){
                                        error += "Invalid Update Interval, reset to 1\n";
                                        errorBoolean = true;
                                        a.setUpdateInterval(1);
                                    }
                                    a.setContinuousRun(checkBox.isSelected());
                                    a.setConfigSet(true);
                                    if(radioButton.isSelected())
                                        run.setVisible(true);
                                    else
                                        run.setVisible(false);
                                    if(checkBox.isSelected())
                                        nextButton.setVisible(false);
                                    if(errorBoolean)
                                        throw new Exception(error);
                                    stage.close();
                                    break;
                                }catch (Exception e){
                                    ErrorDialog.getDialog().show("Error", e.getMessage());
                                }
                                stage.close();
                                break;
                            }
                        });
                    });
                    i++;
                }
            }catch (Exception e){
            }
            Clustering.setVisible(false);
            Classification.setVisible(false);
        });
        Clustering.setOnAction(event -> {
            try {
                run.setDisable(false);
                Class [] classes = getClasses("Algorithms.clustering");
                int i = 0;
                for(Class c : classes){
                    String [] strings = c.getName().split("\\.");
                    String name = strings[strings.length -1];
                    Algorithm algorithm = (Algorithm) c.getConstructor().newInstance();
                    clusterConfigHashMap.put(name, new ClusterConfig());
                    RadioButton radioButton = new RadioButton(name);
                    radioButton.setToggleGroup(buttons);
                    radioButton.setUserData(algorithm);
                    algButtons.add(radioButton, 0,i);
                    Button button = new Button("Options");
                    algButtons.add(button, 1, i);
                    radioButton.setOnAction(event1 -> {
                        if(clusterConfigHashMap.get(name).isConfigSet()) {
                            run.setVisible(true);
                        }
                        else {
                            run.setVisible(false);
                        }
                        classOrCluster = false;
                        key = name;
                    });
                    button.setOnAction(event1 -> {
                        Text text = new Text("Max Iteration");
                        Text text1 = new Text("Update Interval");
                        Text text2 = new Text("Number of Clusters");
                        Text text3 = new Text("Continuous Run?");
                        Button save = new Button("Save");
                        ClusterConfig a = clusterConfigHashMap.get(name);
                        Stage stage = new Stage();
                        GridPane gridPane = new GridPane();
                        Scene scene = new Scene(gridPane);
                        TextField textField = new TextField();
                        TextField textField1 = new TextField();
                        TextField textField2 = new TextField();
                        CheckBox checkBox = new CheckBox();
                        textField.setText(String.valueOf(a.getMaxIntegers()));
                        textField1.setText(String.valueOf(a.getUpdateInterval()));
                        textField2.setText(String.valueOf(a.getNumClusters()));
                        checkBox.setSelected(a.isContinuousRun());
                        gridPane.add(text, 0, 0);
                        gridPane.add(text1, 0, 2);
                        gridPane.add(text2, 0, 4);
                        gridPane.add(text3, 0, 6);
                        gridPane.add(save, 0, 8);
                        gridPane.add(textField, 1, 0);
                        gridPane.add(textField1, 1, 2);
                        gridPane.add(textField2, 1, 4);
                        gridPane.add(checkBox, 1, 6);
                        stage.setScene(scene);
                        stage.show();
                        save.setOnAction(event2 -> {
                            while(true) {
                                try {
                                    String error = "";
                                    boolean errorBoolean = false;
                                    try {
                                        a.setMaxIntegers(Integer.parseInt(textField.getText()));
                                        if (Integer.parseInt(textField.getText()) <= 0) {
                                            throw new Exception();
                                        }
                                    }catch (Exception e){
                                        error += "Invalid max Integers, reset to 1\n";
                                        errorBoolean = true;
                                        a.setMaxIntegers(1);
                                    }
                                    try {
                                        a.setUpdateInterval(Integer.parseInt(textField1.getText()));
                                        if (Integer.parseInt(textField1.getText()) <= 0) {
                                            throw new Exception();
                                        }
                                    }catch (Exception e){
                                        error += "Inavlid Update Interval, reset to 1\n";
                                        errorBoolean = true;
                                        a.setUpdateInterval(1);
                                    }
                                    a.setContinuousRun(checkBox.isSelected());
                                    try {
                                        a.setNumClusters(Integer.parseInt(textField2.getText()));
                                        if (Integer.parseInt(textField2.getText()) < 2) {
                                            a.setNumClusters(2);
                                            throw new Exception("Invalid Number of clusters cannot be less than 2, set to 2\n");
                                        }
                                        if(Integer.parseInt(textField2.getText())>4){
                                            a.setNumClusters(4);
                                            throw new Exception("Invalid number of clusters, cannot be greater than 4, set to 4\n");
                                        }
                                    }catch (Exception e){
                                        error += e.getMessage();
                                        errorBoolean = true;
                                    }
                                    if(radioButton.isSelected())
                                        run.setVisible(true);
                                    else
                                        run.setVisible(false);
                                    if(checkBox.isSelected())
                                        nextButton.setVisible(false);
                                    a.setConfigSet(true);
                                    if(errorBoolean)
                                        throw new Exception(error);
                                    stage.close();
                                    break;
                                }catch (Exception e){
                                    ErrorDialog.getDialog().show("Error", e.getMessage());
                                }
                                stage.close();
                                break;
                            }
                        });
                    });
                    i++;
                }
            }catch (Exception e){
            }
            Clustering.setVisible(false);
            Classification.setVisible(false);
        });

        run.setOnAction(event -> {
            try {
                Class c = buttons.getSelectedToggle().getUserData().getClass();
                String[] strings = c.getName().split("\\.");
                if (strings[strings.length - 2].equals("classification")) {
                    ClassificationConfig config = classificationConfigHashMap.get(strings[strings.length - 1]);
                    thread = new Thread((Algorithm) c.getConstructor(DataSet.class, int.class, int.class, boolean.class, ApplicationTemplate.class)
                            .newInstance(new DataSet(((AppData) applicationTemplate.getDataComponent()).getDataLabels(),
                                            ((AppData) applicationTemplate.getDataComponent()).getDataPoints()), config.getMaxIntegers(),
                                    config.getUpdateInterval(), config.isContinuousRun(), applicationTemplate));
                    nextButton.setVisible(false);
//                    chart.getData().clear();
//                    ((AppData) applicationTemplate.getDataComponent()).clear();
//                    ((AppData) applicationTemplate.getDataComponent()).loadData(((AppData)applicationTemplate.getDataComponent()).getP());
//                    ((AppData) applicationTemplate.getDataComponent()).displayData();
                    thread.start();
                }
                if (strings[strings.length - 2].equals("clustering")) {
                    ClusterConfig config = clusterConfigHashMap.get(strings[strings.length - 1]);
                    thread = new Thread((Algorithm) c.getConstructor(int.class, DataSet.class, int.class, int.class, boolean.class, ApplicationTemplate.class)
                            .newInstance(config.getNumClusters(), new DataSet(((AppData) applicationTemplate.getDataComponent()).getDataLabels(),
                                            ((AppData) applicationTemplate.getDataComponent()).getDataPoints()), config.getMaxIntegers(),
                                    config.getUpdateInterval(), config.isContinuousRun(), applicationTemplate));
//                    chart.getData().clear();
//                    ((AppData) applicationTemplate.getDataComponent()).clear();
//                    ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
//                    ((AppData) applicationTemplate.getDataComponent()).displayData();
                    thread.start();
                }
            }catch (Exception e){
            }
        });
        nextButton.setOnAction(event -> {
            synchronized (((AppUI) applicationTemplate.getUIComponent()).getThread()) {
                try {
                    thread.notify();
                }
                catch (Exception e){
                }
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
                    ((AppData) applicationTemplate.getDataComponent()).clear();
                    ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
                    scrnshotButton.setDisable(false);
                } catch (Exception e) {
                }

            }
        });
    }
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

}
