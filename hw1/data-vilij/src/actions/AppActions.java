package actions;

import javafx.stage.FileChooser;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
        try{
            promptToSave();
        }catch (IOException e){
            return;
        }
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1
        System.exit(0);
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
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
        // TODO for homework 1
        // TODO remove the placeholder line below after you have implemented this method

        ConfirmationDialog confirmationDialog = ConfirmationDialog.getDialog();
        confirmationDialog.show
                (applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE.name()),
                        applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK.name()));
        String option = confirmationDialog.getSelectedOption().name();
        if(option.equals(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.YES.name()))){
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                    (applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name()),
                            applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name())));
            Path p1 = Paths.get(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.HW1.name()) +
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SEPARATOR.name()) +
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DATA_VILIJ_PATH.name()) +
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SEPARATOR.name()) +
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.RESOURCES.name()) +
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SEPARATOR.name()) +
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name()));
            Path p2 = Paths.get("").toAbsolutePath();
            Path relpath = p2.resolve(p1);
            fileChooser.setInitialDirectory(relpath.toFile());
            File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
            if(file == null)
                return true;
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(((AppUI)applicationTemplate.getUIComponent()).getTextArea());
            fileWriter.close();
            dataFilePath = file.toPath();
            return true;
        }
        else if(option.equals(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.NO.name()))){
            applicationTemplate.getUIComponent().clear();
            return true;
        }
        else
            return false;
    }
}
