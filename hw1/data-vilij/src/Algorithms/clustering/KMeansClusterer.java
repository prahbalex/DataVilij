package Algorithms.clustering;

import Algorithms.Clusterer;
import Algorithms.DataSet;
import actions.AppActions;
import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */
public class KMeansClusterer extends Clusterer {

    private DataSet dataset;
    private List<Point2D> centroids;

    private final int           maxIterations;
    private final int           updateInterval;
    private AtomicBoolean tocontinue;
    private ApplicationTemplate applicationTemplate;

    public KMeansClusterer() {
        super(0);
        this.maxIterations = 0;
        this.updateInterval = 0;
        this.tocontinue = new AtomicBoolean(false);
    }

    public KMeansClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(false);
    }

    public KMeansClusterer(int k, DataSet dataset, int maxIterations, int updateInterval, boolean tocontinue, ApplicationTemplate applicationTemplate) {
        super(k);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public int getMaxIterations() { return maxIterations; }

    @Override
    public int getUpdateInterval() { return updateInterval; }

    @Override
    public boolean tocontinue() { return tocontinue.get(); }

    @Override
    public void run() {
        Map<String, String> savedLabels =((AppData)applicationTemplate.getDataComponent()).getProcessor().getDataLabels();
        boolean nextButton = tocontinue.get();
        initializeCentroids();
        int iteration = 0;
        while (iteration++ < maxIterations & tocontinue.get()) {
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
            assignLabels();
            recomputeCentroids();
            if (iteration % updateInterval == 0) {
                System.out.printf("Iteration number %d: \n", iteration);
                Platform.runLater(() -> cluster());
                try {
                    Thread.sleep(2000);
                } catch (Exception ex) {

                }
                if(!nextButton){
                    ((AppUI)applicationTemplate.getUIComponent()).getRun().setDisable(true);
                    ((AppUI)applicationTemplate.getUIComponent()).getNextButton().setVisible(true);
                    synchronized (((AppUI) applicationTemplate.getUIComponent()).getThread()) {
                        try {
                            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
                            ((AppUI) applicationTemplate.getUIComponent()).getThread().wait();
                            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
                            Thread.sleep(500);
                        }
                        catch (Exception e){
                        }
                    }
                }
            }
        }
        Platform.runLater(()-> ((AppData)applicationTemplate.getDataComponent()).getProcessor().setDataLabels(savedLabels));
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

    private void initializeCentroids() {
        Set<String>  chosen        = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(dataset.getLabels().keySet());
        Random       r             = new Random();
        while (chosen.size() < numberOfClusters) {
            int i = r.nextInt(instanceNames.size());
            while (chosen.contains(instanceNames.get(i)))
                ++i;
            chosen.add(instanceNames.get(i));
        }
        centroids = chosen.stream().map(name -> dataset.getLocations().get(name)).collect(Collectors.toList());
        tocontinue.set(true);
    }

    private void assignLabels() {
        dataset.getLocations().forEach((instanceName, location) -> {
            double minDistance      = Double.MAX_VALUE;
            int    minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            dataset.getLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });
    }

    private void recomputeCentroids() {
        tocontinue.set(false);
        IntStream.range(0, numberOfClusters).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum = dataset.getLabels()
                    .entrySet()
                    .stream()
                    .filter(entry -> i == Integer.parseInt(entry.getValue()))
                    .map(entry -> dataset.getLocations().get(entry.getKey()))
                    .reduce(new Point2D(0, 0), (p, q) -> {
                        clusterSize.incrementAndGet();
                        return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                    });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))) {
                centroids.set(i, newCentroid);
                tocontinue.set(true);
            }
        });
    }

    private static double computeDistance(Point2D p, Point2D q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }

    public void cluster(){
        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
        ((AppData)applicationTemplate.getDataComponent()).getProcessor().setDataLabels(dataset.getLabels());
        ((AppData) applicationTemplate.getDataComponent()).displayData();
    }

}