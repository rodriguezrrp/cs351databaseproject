package project.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import project.db.DBCommunicator;
import project.gui.login.LoginController;
import project.gui.utils.MainSceneSwapper;

import java.io.IOException;
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
        databaseCommunicator = new DBCommunicator();
//        boolean loginSuccess = promptLogin();
//        if(!loginSuccess) {
        String loginName = promptLogin();
        if(loginName == null) {
            // if login exited with failure, stop loading the program
            System.out.println("Login aborted. Not loading program.");
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
        mainCtrlr.setDatabaseCommunicator(databaseCommunicator);
        // setup personalization
        mainCtrlr.personalizeName(loginName);
        // setup the cleanup actions when the window is closing
        stage.setOnCloseRequest(windowEvent -> {
            System.out.println("performing stage's closing actions");
            this.databaseCommunicator.close();
            Platform.exit();  // from what I've read, this will close down the program 'the proper way'
        });
        // load up the actual window!
        stage.show();
    }

    private String promptLogin() {
        try {
            // load a login screen which blocks execution until login resolves (ideally successfully)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login/login.fxml"));
            LoginController ctrlr = new LoginController(databaseCommunicator);
            loader.setController(ctrlr);
            Parent loginPaneRoot = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("TAL Database Manager Login");
            loginStage.setScene(new Scene(loginPaneRoot, 320, 240));
//            return databaseCommunicator.authLogin(null, null);
            loginStage.showAndWait();
            return ctrlr.getLoginName();
        } catch (IOException e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR,
                    "Failed to start the program's login!"
                            + "\n" + e.getClass().getCanonicalName()
                            + "\n" + e.getMessage(),
                    ButtonType.CLOSE);
            a.show();
            return null;
        }
    }

}
