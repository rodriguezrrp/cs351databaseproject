package project.gui.utils.form;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import project.gui.utils.MainSceneSwapper;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class FormPaneBuilder {

    private MainSceneSwapper mainSceneSwapper;
    private List<String> labels = new ArrayList<>();
    private List<String> displayTexts = new ArrayList<>();
    private List<Object> values = new ArrayList<>();
    private List<FormFieldTypes> types = new ArrayList<>();
    private String formHeader;
    private String yesBtnText;
    private FormPaneExitHandler afterExitHandler = null;

    // if exists, executed as the form loads to populate it with data (ex. when preparing to edit)
    private Supplier<Map<String,String>> provideFormDataOnLoad;
    // executed as the form closes to apply the data to the database (ex. when saving or adding)
    private Function<Map<String, String>, FormPaneController.FormClosingAction> useFormDataOnExit;

    public FormPaneBuilder(MainSceneSwapper mainSceneSwapper, String formHeader, String yesBtnText,
                           Function<Map<String, String>, FormPaneController.FormClosingAction> useFormDataOnExit) {
        this(mainSceneSwapper, formHeader, yesBtnText, null, useFormDataOnExit);
    }
    public FormPaneBuilder(MainSceneSwapper mainSceneSwapper, String formHeader, String yesBtnText,
                           Supplier<Map<String,String>> provideFormDataOnLoad,
                           Function<Map<String, String>, FormPaneController.FormClosingAction> useFormDataOnExit) {
        Objects.requireNonNull(useFormDataOnExit,"useFormDataOnExit must not be null!" +
                " Otherwise, when the form exits, nothing will be done with the form's edited data!");
        this.provideFormDataOnLoad = provideFormDataOnLoad;
        this.useFormDataOnExit = useFormDataOnExit;
        this.mainSceneSwapper = mainSceneSwapper;
        this.formHeader = formHeader;
        this.yesBtnText = yesBtnText;
    }


    public FormPaneBuilder addColumnLabelForField(String label, String displayText) {
        return this.addColumnLabelForField(label, displayText, null);
    }
    // TODO: add some way to add a mask to these fields?
    //  So while the user edits the form, they can't enter invalid values?
    public FormPaneBuilder addColumnLabelForField(String label, String displayText, String defaultValue) {
        types.add(FormFieldTypes.TEXT);
        labels.add(label);
        displayTexts.add(displayText);
        values.add(defaultValue);
        return this;
    }

    public FormPaneBuilder addColumnLabelForDropdown(Supplier<List<String>> dropdownValues, String label, String displayText) {
        types.add(FormFieldTypes.DROPDOWN);
        labels.add(label);
        displayTexts.add(displayText);
        values.add(dropdownValues);
        return this;
    }

    public FormPaneBuilder afterFormExits(FormPaneExitHandler afterExitHandler) {
        this.afterExitHandler = afterExitHandler;
        return this;
    }

    public Pane create() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("formpane.fxml"));
        FormPaneController ctrlr = new FormPaneController(mainSceneSwapper, useFormDataOnExit);
        loader.setController(ctrlr);
        Pane formPane = loader.load();
        // set some extra data that must happen post-load
        ctrlr.setFormHeader(formHeader);
        ctrlr.setYesBtnText(yesBtnText);
        ctrlr.setAfterExitHandler(afterExitHandler);
        // add the forms to the loaded pane via the controller
        Map<String, String> prePopulatingData = null;
//        System.out.println("provideFormDataOnLoad = " + provideFormDataOnLoad);
        if(provideFormDataOnLoad != null) {
            prePopulatingData = provideFormDataOnLoad.get();
        }
//        System.out.println("prePopulatingData = " + prePopulatingData);
        Iterator<FormFieldTypes> iterTypes = types.iterator();
        Iterator<String> iterLabels = labels.iterator();
        Iterator<String> iterDspTxts = displayTexts.iterator();
        Iterator<Object> iterValues = values.iterator();
        while(iterLabels.hasNext()) {
            FormFieldTypes type = iterTypes.next();
            String label = iterLabels.next();
            String displayText = iterDspTxts.next();
            Object value = iterValues.next();
            if(type.equals(FormFieldTypes.TEXT)) {
                String defaultValue = (String) value;
                if (prePopulatingData != null) {
                    // if possible, try to get the default value from the prepopulation data instead (using the label as the key);
                    //  if that fails (ex. if no prepopulation data is given), revert back to the builder-specified default value.
                    defaultValue = prePopulatingData.getOrDefault(label, defaultValue);
                }
                ctrlr.addTextField(label, displayText, defaultValue);
            } else if(type.equals(FormFieldTypes.DROPDOWN)) {
                // if it's a dropdown, interpret the 'value' as a List<String> supplier (which it should be),
                //  which should produce the list to use in the dropdown.
                List<String> dropdownList = ((Supplier<List<String>>) value).get();
                ctrlr.addDropdown(label, displayText, dropdownList);
            } else {
                System.out.println("form builder: ignoring type " + type);
            }
        }
        // return the prepared form pane
        return formPane;
    }

}
