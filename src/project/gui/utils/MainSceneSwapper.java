package project.gui.utils;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Objects;

public class MainSceneSwapper {

    private final Scene mainScene;
    private Pane mainPane = null;

    public MainSceneSwapper(Scene mainScene/*, Pane mainPane*/) {
        this.mainScene = mainScene;
//        this.mainPane = mainPane;
        // instead of passing as param, just get it from the scene itself
        this.mainPane = (Pane) mainScene.getRoot();
//        toMainPane();
    }

    public void toMainPane() {
        Objects.requireNonNull(mainPane, "mainPane was null! Did you not call setMainPane yet?");
        this.mainScene.setRoot(mainPane);
    }

//    public void toDBViewingScreen(...) {
//        //TODO
//    }

    public void toAlternatePane(Pane alt) {
        this.mainScene.setRoot(alt);
    }


    public void setMainPane(Pane mainPane) {
        this.mainPane = mainPane;
    }

    public void exitProgram() {
//        Platform.exit();  // from what I've read, this will close down the program 'the proper way'
        // never mind, that needs to happen in the stage's onCloseRequest handler.
        // We need to get the window and send it a window close request,
        //   so that the stage will act like the user clicked the window's close button
        //   (and will fire its onCloseRequest handler)
        this.mainScene.getWindow().fireEvent(new WindowEvent(
                this.mainScene.getWindow(),WindowEvent.WINDOW_CLOSE_REQUEST
        ));
    }

}
