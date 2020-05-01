package project.gui.utils.form;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import project.gui.utils.MainSceneSwapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FormPaneController {

    public static enum FormClosingAction { CLOSE_FORM_WITH_SUCCESS, CLOSE_FORM_WITH_FAILURE, KEEP_FORM_OPEN }

    private Map<String,FormFieldController> dbLabelControllerMap = new HashMap<>();
    private MainSceneSwapper mainSceneSwapper;
    private Function<Map<String, String>, FormClosingAction> useFormDataOnExit;
    private FormPaneExitHandler exitHandler = null;

    @FXML
    private Label lblHeader;
    @FXML
    private VBox vboxFormFields;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnYes;
    @FXML
    private Region spacerRegion;

    public FormPaneController(//String formTitle, String formYesBtnText,
                              MainSceneSwapper mainSceneSwapper,
                              Function<Map<String, String>, FormClosingAction> useFormDataOnExit) {
        this.mainSceneSwapper = mainSceneSwapper;
        this.useFormDataOnExit = useFormDataOnExit;
    }

    @FXML
    private void initialize() {
        // doing this here because the FXMLLoader yells at me if I try to put this in the fxml
        HBox.setHgrow(spacerRegion, Priority.ALWAYS);
    }

    @FXML
    private void onActionYes() {
        // gather up the data from the form's fields
        Map<String, String> formData = new HashMap<>(dbLabelControllerMap.size());
        for (Map.Entry<String, FormFieldController> entry : dbLabelControllerMap.entrySet()) {
            String fieldColumnKey = entry.getKey();
            FormFieldController fieldCtrlr = entry.getValue();
            formData.put(fieldColumnKey, fieldCtrlr.getFieldValue());
        }
        // call the function which will use this data (ex. submitting it to the database)
//        useFormDataOnExit.accept(formData);
        FormClosingAction success = useFormDataOnExit.apply(formData);
        // exit the form (unless requested otherwise)
        if(!success.equals(FormClosingAction.KEEP_FORM_OPEN)) {
            exitForm(success, formData);
        }
    }

    @FXML
    private void onActionCancel() {
        // exit the form
        exitForm(FormClosingAction.CLOSE_FORM_WITH_FAILURE, null);
    }

    private void exitForm(FormClosingAction formClosingAction, Map<String, String> formData) {
        // do the things that make the form exit
        mainSceneSwapper.back();
        // call the handler if it is set
        if(exitHandler != null) {
            try {
                exitHandler.afterFormExit(formClosingAction, formData);
            } catch (Exception e) {
                System.err.println("Error occurred when executing FormPaneExitHandler.");
                e.printStackTrace();
            }
        }
    }


    public void setFormHeader(String formHeader) {
        this.lblHeader.setText(formHeader);
    }

    public void setYesBtnText(String yesBtnText) {
        this.btnYes.setText(yesBtnText);
    }

    public void setAfterExitHandler(FormPaneExitHandler exitHandler) {
        this.exitHandler = exitHandler;
    }

    public void addField(String label, String displayLabel, String defaultVal) {
        // prep the form field
        FXMLLoader loader = new FXMLLoader(getClass().getResource("formfield.fxml"));
        FormFieldController fieldCtrlr = new FormFieldController(displayLabel, defaultVal);
        loader.setController(fieldCtrlr);
        // load the form field and add it to the form
        Parent formField = null;
        try {
            formField = loader.load();
        } catch (IOException e) {
            System.err.println("Failed to load a form field (label="+label
                    +", displayLabel="+displayLabel+", defaultVal="+defaultVal+")!");
            e.printStackTrace();
            return; // abort out of the function because the loading failed
        }
        // add the label and the controller into the map
        //   so they can be retrieved as label-and-value paired data for SQL later
        dbLabelControllerMap.put(label, fieldCtrlr);
        this.vboxFormFields.getChildren().add(formField);
    }

}
