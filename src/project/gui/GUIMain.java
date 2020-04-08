package project.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import project.db.DBCommunicator;
import project.gui.utils.MainSceneSwapper;

import java.util.HashMap;
import java.util.Map;

public class GUIMain extends Application {

//    private Map<String,Object> SHARED_VALUES = new HashMap<>();

    private MainSceneSwapper mainSceneSwapper;

    private DBCommunicator databaseCommunicator;

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
        // setup the scene swapper
        mainSceneSwapper = new MainSceneSwapper(scene);
        GUIMainController mainCtrlr = ((GUIMainController) mainLoader.getController());
        mainCtrlr.setMainSceneSwapper(mainSceneSwapper); // give the main controller access to the scene swapper
        // setup the database communicator
        databaseCommunicator = new DBCommunicator();
        mainCtrlr.setDatabaseCommunicator(databaseCommunicator);
        // setup the cleanup actions when the window is closing
        stage.setOnCloseRequest(windowEvent -> {
            System.out.println("performing stage's closing actions");
            this.databaseCommunicator.close();
            Platform.exit();  // from what I've read, this will close down the program 'the proper way'
        });
        // load up the actual window!
        stage.show();
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
