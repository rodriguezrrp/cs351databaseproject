package project.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.gui.utils.MainSceneSwapper;

import java.util.HashMap;
import java.util.Map;

public class GUIMain extends Application {

//    private Map<String,Object> SHARED_VALUES = new HashMap<>();

    private MainSceneSwapper mainSceneSwapper;

    public static void guiMain(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // start with a login before loading the main program
        boolean loginSuccess = promptLogin();
        if(!loginSuccess) {
            // if login exited with failure, stop loading the program
            return;
        }
        System.out.println("Successfully logged in! Loading program");

        // load main screen
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainRoot = mainLoader.load();
        stage.setTitle("TAL Database Manager");
        Scene scene = new Scene(mainRoot, 720, 640);  // width, height
        stage.setScene(scene);
        mainSceneSwapper = new MainSceneSwapper(scene);
        GUIMainController mainCtrlr = ((GUIMainController) mainLoader.getController());
        mainCtrlr.setMainSceneSwapper(mainSceneSwapper); // give the controller access to the scene swapper
        stage.show();  // load up the actual window!
    }

    private boolean promptLogin() {
        // load a login screen which blocks execution until login resolves (ideally successfully)
        // cheap shortcut; TODO: make this actually prompt a login!
        return authLogin(null,null);
    }

    private boolean authLogin(String user, String pass) {
        return true;  // TODO: make this actually validate
    }

}
