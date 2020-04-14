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
    private List<String> defaults = new ArrayList<>();
    private String title;
    private String yesBtnText;

    // if exists, executed as the form loads to populate it with data (ex. when preparing to edit)
    private Supplier<Map<String,String>> provideFormDataOnLoad;
    // executed as the form closes to apply the data to the database (ex. when saving or adding)
    private Function<Map<String, String>, FormPaneController.FormClosingAction> useFormDataOnExit;

    public FormPaneBuilder(MainSceneSwapper mainSceneSwapper, String title, String yesBtnText,
                           Function<Map<String, String>, FormPaneController.FormClosingAction> useFormDataOnExit) {
        this(mainSceneSwapper, title, yesBtnText, null, useFormDataOnExit);
    }
    public FormPaneBuilder(MainSceneSwapper mainSceneSwapper, String title, String yesBtnText,
                           Supplier<Map<String,String>> provideFormDataOnLoad,
                           Function<Map<String, String>, FormPaneController.FormClosingAction> useFormDataOnExit) {
        Objects.requireNonNull(useFormDataOnExit,"useFormDataOnExit must not be null!" +
                " Otherwise, when the form exits, nothing will be done with the form's edited data!");
        this.provideFormDataOnLoad = provideFormDataOnLoad;
        this.useFormDataOnExit = useFormDataOnExit;
        this.mainSceneSwapper = mainSceneSwapper;
        this.title = title;
        this.yesBtnText = yesBtnText;
    }


    public FormPaneBuilder addColumnLabelForField(String label, String displayText) {
        return this.addColumnLabelForField(label, displayText, null);
    }
    // TODO: add some way to add a mask to these fields?
    //  So while the user edits the form, they can't enter invalid values?
    public FormPaneBuilder addColumnLabelForField(String label, String displayText, String defaultValue) {
        labels.add(label);
        displayTexts.add(displayText);
        defaults.add(defaultValue);
        return this;
    }

    public Pane create() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("formpane.fxml"));
        FormPaneController ctrlr = new FormPaneController(mainSceneSwapper, useFormDataOnExit);
        loader.setController(ctrlr);
        Pane formPane = loader.load();
        // add the forms to the loaded pane via the controller
        Map<String, String> prePopulatingData = null;
        if(provideFormDataOnLoad != null) {
            prePopulatingData = provideFormDataOnLoad.get();
        }
        Iterator<String> iterLabels = labels.iterator();
        Iterator<String> iterDspTxts = displayTexts.iterator();
        Iterator<String> iterDefaults = defaults.iterator();
        while(iterLabels.hasNext()) {
            String label = iterLabels.next();
            String displayText = iterDspTxts.next();
            String defaultVal = iterDefaults.next();
            if(prePopulatingData != null) {
                // if possible, try to get the default value from the prepopulation data instead (using the label as the key);
                //  if that fails (ex. if no prepopulation data is given), revert back to the builder-specified default value.
                defaultVal = prePopulatingData.getOrDefault(label, defaultVal);
            }
            ctrlr.addField(label, displayText, defaultVal);
        }
        // return the prepared form pane
        return formPane;
    }

}
