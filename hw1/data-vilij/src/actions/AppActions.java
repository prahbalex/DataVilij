package actions;

import dataprocessors.AppData;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import javax.imageio.ImageIO;
import javax.xml.soap.Text;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import static vilij.settings.PropertyTypes.SAVE_WORK_TITLE;
import static vilij.templates.UITemplate.SEPARATOR;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    Path dataFilePath;

    /** The boolean property marking whether or not there are any unsaved changes. */
    SimpleBooleanProperty isUnsaved;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        this.isUnsaved = new SimpleBooleanProperty(false);
    }

    public void setIsUnsavedProperty(boolean property) { isUnsaved.set(property); }

    public boolean getIsUnsvaedProperty() { return isUnsaved.getValue();}

    @Override
    public void handleNewRequest() {
        ((AppUI)applicationTemplate.getUIComponent()).disableAll();
        ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setVisible(true);
        ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setDisable(false);
        ((AppUI) applicationTemplate.getUIComponent()).getChart().setVisible(true);
        ((AppUI) applicationTemplate.getUIComponent()).getLeftPanelTitle().setVisible(true);
        ((AppUI)applicationTemplate.getUIComponent()).getDisplayButton().setVisible(true);
        ((AppUI)applicationTemplate.getUIComponent()).getCheckBox().setVisible(true);
        (applicationTemplate.getUIComponent()).clear();
        ((AppUI)applicationTemplate.getUIComponent()).setMetaData("");
        ((AppUI)applicationTemplate.getUIComponent()).getClassification().setVisible(false);
        ((AppUI)applicationTemplate.getUIComponent()).getClustering().setVisible(false);
        dataFilePath = null;
    }

//    @Override
//    public void handleNewRequest() {
//        try {
//            if (!isUnsaved.get() || promptToSave()) {
//
//                applicationTemplate.getDataComponent().clear();
//                applicationTemplate.getUIComponent().clear();
//                isUnsaved.set(false);
//                dataFilePath = null;
//            }
//        } catch (IOException e) { errorHandlingHelper(); }
//    }

    @Override
    public void handleSaveRequest() {
        try {
            ((AppData)applicationTemplate.getDataComponent()).loadData(((AppUI)applicationTemplate.getUIComponent()).getCurrentText());
            if(((AppData) applicationTemplate.getDataComponent()).getError())
                return;
            if (!isUnsaved.get() || promptToSave()) {
                isUnsaved.set(false);
            }}
        catch (IOException e){
            errorHandlingHelper();
        }
    }

    @Override
    public void handleLoadRequest() {
        ((AppUI)applicationTemplate.getUIComponent()).clear();
        ((AppUI)applicationTemplate.getUIComponent()).disableAll();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        if(file == null)
            return;
        Path p = file.toPath();
        dataFilePath = p;
        (applicationTemplate.getDataComponent()).loadData(p);
        if(!((AppData)applicationTemplate.getDataComponent()).getError()) {
            ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setVisible(true);
            ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setDisable(true);
            ((AppUI) applicationTemplate.getUIComponent()).getChart().setVisible(true);
            ((AppUI) applicationTemplate.getUIComponent()).getLeftPanelTitle().setVisible(true);
            ((AppUI)applicationTemplate.getUIComponent()).getDisplayButton().setVisible(true);
            ((AppUI)applicationTemplate.getUIComponent()).getCheckBox().setVisible(true);
            String text = "\t" + ((AppData)applicationTemplate.getDataComponent()).getCounter() + " number of " +
                    "instances \n \t " + ((AppData)applicationTemplate.getDataComponent()).getLabels().size() + " number of" +
                    "labels loaded \n\t,  loaded from path \n\t " + p.toString() + "\n\t and labels " +
                    ((AppData)applicationTemplate.getDataComponent()).getLabels()
                    .toString() + "\n";
            ((AppUI)applicationTemplate.getUIComponent()).setMetaData(text);
            ((AppUI)applicationTemplate.getUIComponent()).getMetaData().setVisible(true);
            if((((AppData) applicationTemplate.getDataComponent()).getNullLabel().get()==2))
                ((AppUI) applicationTemplate.getUIComponent()).getClassification().setVisible(true);
            ((AppUI)applicationTemplate.getUIComponent()).getClustering().setVisible(true);
            ((AppUI)applicationTemplate.getUIComponent()).getSave().setDisable(true);
            ((AppData) applicationTemplate.getDataComponent()).displayData();
        }
    }

    @Override
    public void handleExitRequest() {
        try {
            if (!isUnsaved.get() || promptToSave())
                System.exit(0);
        } catch (IOException e) { errorHandlingHelper(); }
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        WritableImage screenshot = ((AppUI)applicationTemplate.
                getUIComponent()).getChart().snapshot(new SnapshotParameters(), null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("*.png", "*.png"));
        File file = fileChooser.showSaveDialog(new Stage());
        if(file != null)
            ImageIO.write(SwingFXUtils.fromFXImage(screenshot, null), "png", file);
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        PropertyManager    manager = applicationTemplate.manager;
        ConfirmationDialog dialog  = ConfirmationDialog.getDialog();
        dialog.show(manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE.name()),
                    manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK.name()));

        if (dialog.getSelectedOption() == null) return false; // if user closes dialog using the window's close button

        if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)) {
            if (dataFilePath == null) {
                FileChooser fileChooser = new FileChooser();
                String      dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
                URL         dataDirURL  = getClass().getResource(dataDirPath);

                if (dataDirURL == null)
                    throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));

                fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
                fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

                String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
                String extension   = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
                ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                                                                String.format("*.%s", extension));

                fileChooser.getExtensionFilters().add(extFilter);
                File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                if (selected != null) {
                    dataFilePath = selected.toPath();
                    save();
                } else return false; // if user presses escape after initially selecting 'yes'
            } else
                save();
        }

        return !dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL);
    }

    private void save() throws IOException {
        applicationTemplate.getDataComponent().saveData(dataFilePath);
        isUnsaved.set(false);
    }

    private void errorHandlingHelper() {
        ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager  = applicationTemplate.manager;
        String          errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
        String          errMsg   = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
        String          errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
        dialog.show(errTitle, errMsg + errInput);
    }
}
