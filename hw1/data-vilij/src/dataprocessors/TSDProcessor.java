package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    public static class DuplicateDataNameException extends Exception{
        private static final String DUPLICATE_ERROR_MSG = "Duplicate data";

        public DuplicateDataNameException(String name){
            super(String.format("Invalid data name '%s'." + DUPLICATE_ERROR_MSG));
        }
    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
    }

    public String getKey(XYChart.Data<Number, Number> i){
        Point2D x = new Point2D(i.getXValue().doubleValue(), i.getYValue().doubleValue());
        for(String s: dataPoints.keySet()){
            if(dataPoints.get(s).equals(x))
                return s;
        }
        return "";
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        AtomicInteger counter = new AtomicInteger(1);
        ArrayList<String> strings = new ArrayList<>();
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      String   name  = checkedname(list.get(0));
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                      for(String s:strings){
                          if(s.equals(name)){
                              throw new DuplicateDataNameException(name);
                          }
                      }
                      strings.add(name);
                      counter.getAndIncrement();
                  } catch (InvalidDataNameException e) {
                      errorMessage.setLength(0);
                      errorMessage.append("Invalid Data Name at line " + counter.toString() + " \n");
                      hadAnError.set(true);
                  }
                  catch (Exception e){
                      errorMessage.setLength(0);
                      errorMessage.append("Duplicate Data Name at line" + counter.toString() + " \n");
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void toChartData(XYChart<Number, Number> chart) {

        HashSet<Point2D> data = new HashSet<>(dataPoints.values());

        double minimum = 100000000;
        double maximum = 0;
        double sumOfY = 0;

        for (Point2D x: data) {
            if(x.getX() < minimum)
                minimum = x.getX();
            if(x.getY() > maximum)
                maximum = x.getY();
            sumOfY += x.getY();
        }

        double average = (sumOfY)/data.size();

        XYChart.Series<Number, Number> averageLine = new XYChart.Series<>();

        averageLine.getData().add(new XYChart.Data<>(minimum, average));
        averageLine.getData().add(new XYChart.Data<>(maximum, average));
        averageLine.setName("Average Line");

        ArrayList<String> names = new ArrayList<>();

        boolean added = false;
        Set<String> labels = new HashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            });
            chart.getData().add(series);
            if(!added){
                chart.getData().add(averageLine);
                added = true;
            }
        }
        chart.getData().forEach((x) ->{
            if(!x.getName().equals("Average Line")){
                x.getNode().setId("css1");
                x.getNode().setStyle("-fx-stroke: transparent");
                x.getData().forEach((y) ->{
                    Tooltip.install(y.getNode(), new Tooltip(getKey(y)));
                    y.getNode().setCursor(Cursor.HAND);
                });
            }
            else{
                x.getData().forEach((y) ->{
                    y.getNode().setId("css2");
                });
            }
        });
    }

    void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException, DuplicateDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }


}
