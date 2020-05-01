package project.gui.utils.form.fields;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.util.List;

public class DropdownFieldController implements CtrlrWithFormFieldValue {

    @FXML
    private Label lblName;
    @FXML
    private ChoiceBox<String> dropdown;

    private final String displayName;
    private final List<String> dropdownValues;

    public DropdownFieldController(String name, List<String> dropdownValues) {
        this.displayName = name;
        this.dropdownValues = dropdownValues;
    }

    @FXML
    private void initialize() {
        lblName.setText(displayName);
        dropdown.setItems(FXCollections.observableList(dropdownValues));
    }

    @Override
    public String getFieldValue() {
        return dropdown.getValue();
    }

}
