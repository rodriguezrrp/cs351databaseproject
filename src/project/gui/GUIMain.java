package project.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIMain extends Application {

    public static void guiMain(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // load
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = mainLoader.load();
        stage.setTitle("TAL Database Manager");
        stage.setScene(new Scene(root, 640, 480));  // width, height
        stage.show();
    }
}
