package project.gui.utils.dbviewer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import project.gui.utils.MainSceneSwapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DBViewerBuilder {

    public static enum ToolbarSide { LEFT, RIGHT }

//    private Pane buildMeADatabaseViewer(.......) {
//
//    }

    private final Supplier<EventHandler<ActionEvent>> HANDLER_SUPPLIER_BACK_BUTTON = () -> {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
//                DBViewerBuilder.this.mainSceneSwapper.toMainPane();
                DBViewerBuilder.this.mainSceneSwapper.back();
            }
        };
    };

    private MainSceneSwapper mainSceneSwapper;

    private final List<Supplier<Node>> leftItemsSuppliers;
    private final List<Supplier<Node>> rightItemsSuppliers;
    private final Supplier<ResultSet> dbResultSetSupplier;

    public DBViewerBuilder(MainSceneSwapper mainSceneSwapper, Supplier<ResultSet> dbResultSetGetter) {
        this.mainSceneSwapper = mainSceneSwapper;
        leftItemsSuppliers = new ArrayList<>();
        rightItemsSuppliers = new ArrayList<>();
        this.dbResultSetSupplier = dbResultSetGetter;
    }

    public DBViewerBuilder addLeftButton(String name, Supplier<EventHandler<ActionEvent>> handlerSupplier) {
        return this.addButton(name, handlerSupplier, ToolbarSide.LEFT);
    }
    public DBViewerBuilder addRightButton(String name, Supplier<EventHandler<ActionEvent>> handlerSupplier) {
        return this.addButton(name, handlerSupplier, ToolbarSide.RIGHT);
    }
    public DBViewerBuilder addButton(String name, Supplier<EventHandler<ActionEvent>> handlerSupplier,
                                        ToolbarSide side) {
        (side==ToolbarSide.LEFT ? this.leftItemsSuppliers : this.rightItemsSuppliers).add(() -> {
            Button btn = new Button(name);
            btn.setOnAction(handlerSupplier.get());
            return btn;
        });
        return this;
    }
    public DBViewerBuilder insertButton(String name, Supplier<EventHandler<ActionEvent>> handlerSupplier,
                                        int index, ToolbarSide side) {
        (side==ToolbarSide.LEFT ? this.leftItemsSuppliers : this.rightItemsSuppliers).add(index, () -> {
            Button btn = new Button(name);
            btn.setOnAction(handlerSupplier.get());
            return btn;
        });
        return this;
    }

    public DBViewerBuilder addBackButton() {
        this.insertButton("Back",
                this.HANDLER_SUPPLIER_BACK_BUTTON,
                0, // insert at beginning of the left toolbar items list
                ToolbarSide.LEFT);
        // put a separator node right after the button, just for looks :)
        this.leftItemsSuppliers.add(1, () -> new Separator(Orientation.VERTICAL));
        return this;
    }

    public DBViewerBuilder addExitButton(/*MainSceneSwapper mainSceneSwapper*/) {
        Objects.requireNonNull(mainSceneSwapper, "parameter mainSceneSwapper must not be null!");
        // put a separator node right before the button, just for looks :)
        this.rightItemsSuppliers.add(() -> new Separator(Orientation.VERTICAL));
        this.addButton("Exit",
                () -> { return actionEvent -> { mainSceneSwapper.exitProgram(); }; },
                ToolbarSide.RIGHT);
        return this;
    }

//    public DBViewerBuilder addAddButton      addReportButton

    public Pane create() throws IOException {
        // resolve all the suppliers for the left items
        List<Node> leftItems = null;
        if(leftItemsSuppliers.size() > 0) {
            leftItems = new ArrayList<>(leftItemsSuppliers.size());
            for(Supplier<Node> supplier : leftItemsSuppliers) {
                leftItems.add(supplier.get());
            }
        }
        // resolve all the suppliers for the right items
        List<Node> rightItems = null;
        if(rightItemsSuppliers.size() > 0) {
            rightItems = new ArrayList<>(rightItemsSuppliers.size());
            for(Supplier<Node> supplier : rightItemsSuppliers) {
                rightItems.add(supplier.get());
            }
        }
//        DBViewerController controller = new DBViewerController(leftItems, rightItems);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dbviewer.fxml"));
        Pane root = (Pane) loader.load();
        DBViewerController controller = (DBViewerController) loader.getController();
        controller.setupToolbar(leftItems, rightItems);
        // populate the viewer (TODO: make this asynchronously load maybe?)
        controller.fillTable(dbResultSetSupplier.get());
        return root;
    }

}
