package project.gui.utils.form;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FormFieldController {

    @FXML
    private Label lblName;
    @FXML
    private TextField fieldValue;

    private final String displayName;
    private final String initialValue;

    public FormFieldController(String name, String defValue) {
        this.displayName = name;
        this.initialValue = defValue;
    }

    @FXML
    private void initialize() {
        lblName.setText(displayName);
        fieldValue.setText(initialValue);
    }

    public String getFieldValue() {
        return fieldValue.getText();
    }

}
