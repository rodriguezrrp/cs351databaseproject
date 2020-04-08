package project.gui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import project.db.DBCommunicator;
import project.gui.utils.DBViewerBuilder;
import project.gui.utils.MainSceneSwapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

public class GUIMainController {

    @FXML
    private Button btnCust;
    @FXML
    private Button btnOrders;
    @FXML
    private Button btnInv;

    private MainSceneSwapper mainSceneSwapper;
    private DBCommunicator databaseCommunicator;

    private Supplier<ResultSet> customerDataSupplier = () -> {
        try {
            return databaseCommunicator.getCustomerData();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    };
    private DBViewerBuilder customerViewerBuilder;


    public GUIMainController() { }


    @FXML
    private void onActionCust() {
        System.out.println("cust");
        try {
            Pane root = this.customerViewerBuilder.create();
            this.mainSceneSwapper.toAlternatePane(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onActionOrders() {
        System.out.println("orders");
    }

    @FXML
    private void onActionInv() {
        System.out.println("inv");
    }

    @FXML
    private void onActionExit() {
        // delegate the program-exiting task to the main scene swapper, as it may have things to do
        mainSceneSwapper.exitProgram();
    }


    public void setMainSceneSwapper(MainSceneSwapper mainSceneSwapper) {
        // get access to the main scene swapper for this application
        this.mainSceneSwapper = mainSceneSwapper;
        if(mainSceneSwapper == null) {
            this.customerViewerBuilder = null;
        } else {
            this.customerViewerBuilder = new DBViewerBuilder(mainSceneSwapper, customerDataSupplier)
                    .addBackButton()
                    .addExitButton(this.mainSceneSwapper);
        }
    }
    public void setDatabaseCommunicator(DBCommunicator databaseCommunicator) {
        // get access to the database communicator for this application
        this.databaseCommunicator = databaseCommunicator;
    }

}
