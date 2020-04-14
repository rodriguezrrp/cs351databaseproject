package project.gui.utils;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Supplier;

public class MainSceneSwapper {

    private final Scene mainScene;
    private Pane mainPane = null;
    private Pane currentPane = null;

    // a stack of history "newer than" the main pane
    // when this stack is emptied, then mainPane is the only option
    private Deque<Supplier<Pane>> history = new ArrayDeque<>();

    public MainSceneSwapper(Scene mainScene/*, Pane mainPane*/) {
        this.mainScene = mainScene;
//        this.mainPane = mainPane;
        // instead of passing as param, just get it from the scene itself
        this.mainPane = (Pane) mainScene.getRoot();
        this.currentPane = this.mainPane;
//        toMainPane();
    }

    public void toMainPane() {
        Objects.requireNonNull(mainPane, "mainPane was null! Did you not call setMainPane yet?");
        history.clear();
        this.mainScene.setRoot(mainPane);
        this.currentPane = mainPane;
    }

//    public void toAlternatePane(Pane alt) {
//        this.mainScene.setRoot(alt);
//    }

    public void toPane(Pane pane) {
        this.toPane(() -> pane); // wrap the pane in a supplier that just gives that reference
    }
    public void toPane(Supplier<Pane> paneSupplier) {
        Pane p = paneSupplier.get();
        if(p != null) { // if the supplier doesn't return null
            history.push(paneSupplier); // stick it into the history
            changePaneTo(p); // use that pane
        }
    }

    public void back() {
        if(!history.isEmpty()) {
            // remove the current pane as the current head of the stack
            history.pop();
            // find the next pane back in the history stack
            // (or, if none, go to the main pane)
            Pane p;
            if(history.isEmpty()) {
                p = mainPane;
            } else {
                p = history.peek().get();
            }
            changePaneTo(p);
        }
        // note: if history is empty, then we must be at the main screen, so nothing need be done.
    }

    private void changePaneTo(Pane p) {
        Objects.requireNonNull(p, "parameter pane must not be null!");
        this.mainScene.setRoot(p);
        this.currentPane = p;
    }

    public void setMainPane(Pane mainPane) {
        this.mainPane = mainPane;
    }

    public Pane getCurrentPane() {
        return currentPane;
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
