package dataprocessors;

import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;
    private Boolean error;
    private Path p;

    public Boolean getError() {
        return error;
    }

    public AtomicInteger getCounter(){
        return processor.getCounter();
    }

    public ArrayList<String> getLabels(){
        return processor.getLabels();
    }

    public AtomicInteger getNullLabel(){
        return processor.getNullLabel();
    }

    public String getMeta(){
        return "\t" + ((AppData)applicationTemplate.getDataComponent()).getCounter() + " number of " +
                "instances \n \t " + ((AppData)applicationTemplate.getDataComponent()).getLabels().size() + " number of" +
                "labels loaded \n\t,  " + "\n\t and labels " +
                ((AppData)applicationTemplate.getDataComponent()).getLabels()
                        .toString() + "\n";
    }
    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        p = dataFilePath;
       String dataString = "";
       Scanner scanner;
       try {
           scanner = new Scanner(dataFilePath);
           while (scanner.hasNext()){
               dataString +=scanner.nextLine() + "\n";
           }
           loadData(dataString);
       }catch (IOException e){
       }
    }

    public void loadData(String dataString) {
        try {
            processor.processString(dataString);
        } catch (Exception e) {
            ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager  = applicationTemplate.manager;
            String          errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String          errMsg   = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String          errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
            dialog.show(errTitle, errMsg + errInput + e.getMessage());
            processor.clear();
            error = true;
            return;
        }
        int numLines = 0;
        String show = "";
        String hide = "";

        String [] data2 = dataString.split("\n");
        for(int i = 0; (i< 10 && i < data2.length); i ++){
            show += data2[i] + "\n";
            numLines++;
        }
        for(int i = 10; i < data2.length; i++){
            hide += data2[i] + "\n";
            numLines++;
        }
        if(numLines >10){
            ErrorDialog errorDialog =  ErrorDialog.getDialog();
            errorDialog.show("Error", "Loaded data consists of " + numLines + " . Showing first 10");
        }
        ((AppUI)applicationTemplate.getUIComponent()).setCurrentText(show);
        displayData();
        error = false;
    }

    @Override
    public void saveData(Path dataFilePath) {
        // NOTE: completing this method was not a part of HW 1. You may have implemented file saving from the
        // confirmation dialog elsewhere in a different way.
        try (PrintWriter writer = new PrintWriter(Files.newOutputStream(dataFilePath))) {
            writer.write(((AppUI) applicationTemplate.getUIComponent()).getCurrentText());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
