package Algorithms.classification;



import Algorithms.Classifier;
import Algorithms.DataSet;
import actions.AppActions;
import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    private ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;

    // currently, this value does not change after instantiation
    private AtomicBoolean tocontinue;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClassifier() {
        this.dataset = new DataSet();
        this.maxIterations = 0;
        this.updateInterval = 0;
        this.tocontinue = new AtomicBoolean(true);
    }

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue,
                            ApplicationTemplate applicationTemplate) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.applicationTemplate = applicationTemplate;
    }

    public void setTocontinue(boolean tocontinue) {
        this.tocontinue.set(tocontinue);
    }

    @Override
    public void run() {
        for (int i = 1; i <= maxIterations; i++) {
            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
            ((AppUI)applicationTemplate.getUIComponent()).getCheckBox().setVisible(false);
            if(tocontinue())
                ((AppUI)applicationTemplate.getUIComponent()).getRun().setDisable(true);
            for(Node r :((AppUI)applicationTemplate.getUIComponent()).getAlgButtons().getChildren()){
                if(r instanceof RadioButton){
                    r.setDisable(true);
                }
                if(r instanceof Button){
                    if(((Button) r).getText().equals("Options"))
                        r.setDisable(true);
                }
            }
            int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
            int yCoefficient = 10;
            int constant     = RAND.nextInt(11);

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i); //
                flush();
                Platform.runLater(() ->addNewLine(xCoefficient,yCoefficient,constant));
                try {
                    Thread.sleep(2000);
                }
                catch (Exception ex){

                }
                if(!tocontinue()){
                    ((AppUI)applicationTemplate.getUIComponent()).getRun().setDisable(true);
                    ((AppUI)applicationTemplate.getUIComponent()).getNextButton().setVisible(true);
                    synchronized (((AppUI) applicationTemplate.getUIComponent()).getThread()) {
                        try {
                            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
                            ((AppUI) applicationTemplate.getUIComponent()).getThread().wait();
                            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
                            Thread.sleep(500);
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }

            }

        }
        ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
        ((AppUI)applicationTemplate.getUIComponent()).getRun().setDisable(false);
        if (!((AppActions)applicationTemplate.getActionComponent()).isRunOrLoad())
            ((AppUI)applicationTemplate.getUIComponent()).getCheckBox().setVisible(true);
        ((AppUI)applicationTemplate.getUIComponent()).getNextButton().setVisible(false);
        for(Node r :((AppUI)applicationTemplate.getUIComponent()).getAlgButtons().getChildren()){
            if(r instanceof RadioButton){
                r.setDisable(false);
            }
            if(r instanceof Button){
                if(((Button) r).getText().equals("Options"))
                    r.setDisable(false);
            }
        }
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

//    /** A placeholder main method to just make sure this code runs smoothly */
//    public static void main(String... args) throws IOException {
//        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
//        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true,
//                );
//        classifier.run(); // no multithreading yet
//    }

    public void setApplicationTemplate(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    public void addNewLine(int xCoefficient, int yCoefficient, int constant){
        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().remove
                (((AppData) applicationTemplate.getDataComponent()).getProcessor().getAverageLine());



        HashSet<Point2D> data = new HashSet<>(((AppData) applicationTemplate.getDataComponent()).
                getProcessor().getDataPoints().values());

        XYChart.Series<Number, Number> random = new XYChart.Series<>();

        double minimumx = 100000000;
        double maximumx = 0;


        for (Point2D x: data) {
            if(x.getX() < minimumx)
                minimumx = x.getX();
            if(x.getX() > maximumx)
                maximumx = x.getX();
        }
        double firstY = ((-1 * constant) - (xCoefficient * minimumx))/yCoefficient;
        double secondY = ((-1 * constant) - (xCoefficient * maximumx))/yCoefficient;

        random.getData().add(new XYChart.Data<>(minimumx, firstY));
        random.getData().add(new XYChart.Data<>(maximumx, secondY));
        random.setName("Random Line");

        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().add(random);
        (((AppData) applicationTemplate.getDataComponent()).getProcessor()).setAverageLine(random);

    }
}

