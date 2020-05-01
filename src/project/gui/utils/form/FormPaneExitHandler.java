package project.gui.utils.form;

import java.io.IOException;
import java.util.Map;

@FunctionalInterface
public interface FormPaneExitHandler {
    public abstract void afterFormExit(FormPaneController.FormClosingAction formClosingAction, Map<String, String> formData) throws Exception;
}
