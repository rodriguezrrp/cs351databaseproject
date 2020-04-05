package project.gui.utils;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

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
}
