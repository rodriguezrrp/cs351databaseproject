package project.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import project.db.DBCommunicator;
import project.gui.utils.dbviewer.DBViewerBuilder;
import project.gui.utils.MainSceneSwapper;
import project.gui.utils.form.FormPaneBuilder;
import project.gui.utils.form.FormPaneController;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GUIMainController {

    @FXML
    private Button btnCust;
    @FXML
    private Button btnReps;
    @FXML
    private Button btnOrders;
    @FXML
    private Button btnInv;

    private MainSceneSwapper mainSceneSwapper;
    private DBCommunicator databaseCommunicator;

    private DBViewerBuilder customerViewerBuilder;
    private Supplier<ResultSet> customerDataSupplier = () -> {
        try {
            return databaseCommunicator.getCustomerData();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    };

    private DBViewerBuilder repViewerBuilder;
    private Supplier<ResultSet> repDataSupplier = () -> {
        try {
            return databaseCommunicator.getRepData();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    };

    private DBViewerBuilder ordersViewerBuilder;
    private Supplier<ResultSet> ordersDataSupplier = () -> {
        try {
            return databaseCommunicator.getOrdersData();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    };


    public GUIMainController() { }



    @FXML
    private void onActionReps() {
//        System.out.println("reps");
//        try {
//            Pane root = this.repViewerBuilder.create();
//            this.mainSceneSwapper.toPane(root);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.mainSceneSwapper.toPane(() -> {
            try {
                return this.repViewerBuilder.create();
            } catch (IOException e) {
                System.err.println("repViewerBuilder was unable to create a pane!");
                e.printStackTrace();
                return null;
            }
        });
    }

    @FXML
    private void onActionCust() {
        System.out.println("cust");
        this.mainSceneSwapper.toPane(() -> {
            try {
                return this.customerViewerBuilder.create();
            } catch (IOException e) {
                System.err.println("customerViewerBuilder was unable to create a pane!");
                e.printStackTrace();
                return null;
            }
        });
    }

    @FXML
    private void onActionOrders() {
        System.out.println("orders");
        this.mainSceneSwapper.toPane(() -> {
            try {
                return this.repViewerBuilder.create();
            } catch (IOException e) {
                System.err.println("customerViewerBuilder was unable to create a pane!");
                e.printStackTrace();
                return null;
            }
        });
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
            this.repViewerBuilder = null;
        } else {
            this.customerViewerBuilder = new DBViewerBuilder(mainSceneSwapper, customerDataSupplier)
                    .addBackButton()
                    .addExitButton(/*this.mainSceneSwapper*/);
            this.repViewerBuilder = new DBViewerBuilder(mainSceneSwapper, repDataSupplier)
                    .addBackButton()
                    .addButton("Add Representative",
                            () -> { return (ActionEvent actionEvent) -> {
                                // start of event handler lambda
                                FormPaneBuilder formPaneBuilder = new FormPaneBuilder(
                                        mainSceneSwapper,
                                        "Add Representative", "Add",
                                        (Map<String,String> formDataMap) -> {
                                            // when the form is closed, add the data into the database
                                            try {
                                                boolean success = databaseCommunicator.addRep(formDataMap);
                                                if(!success) {
                                                    Alert a = new Alert(Alert.AlertType.ERROR,
                                                            "Failed to submit the form's data to the database!" +
                                                                    "\nCheck the program's console output for details",
                                                            ButtonType.CLOSE);
                                                }
                                                return FormPaneController.FormClosingAction.CLOSE_FORM;
                                            } catch (SQLException e) {
                                                System.err.println("Failed to add rep!");
                                                e.printStackTrace();
                                                // create an alert to let the user know something went wrong.
                                                Alert a = new Alert(Alert.AlertType.ERROR,
                                                        "Failed to add rep! Please try again."
                                                                + "\n" + e.getClass().getCanonicalName()
                                                                + "\n" + e.getMessage(),
                                                        ButtonType.CLOSE);
                                                a.show();
                                                return FormPaneController.FormClosingAction.CLOSE_FORM;
                                            }
                                        })
                                        .addColumnLabelForField("FirstName","First Name:")
                                        .addColumnLabelForField("LastName","Last Name:")
                                        .addColumnLabelForField("Street","Street:")
                                        .addColumnLabelForField("City","City:")
                                        .addColumnLabelForField("State","State:")
                                        .addColumnLabelForField("PostalCode","Postal Code:")
                                        .addColumnLabelForField("Commission","Commission:")
                                        .addColumnLabelForField("Rate","Rate:");
                                mainSceneSwapper.toPane(() -> {
                                    try {
                                        return formPaneBuilder.create();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                });
                                // end of event handler lambda
                            }; },
                            DBViewerBuilder.ToolbarSide.LEFT
                    )
                    .addExitButton(/*this.mainSceneSwapper*/);
            this.ordersViewerBuilder = new DBViewerBuilder(mainSceneSwapper, ordersDataSupplier)
                    .addBackButton()
                    .addExitButton();
        }
    }
    public void setDatabaseCommunicator(DBCommunicator databaseCommunicator) {
        // get access to the database communicator for this application
        this.databaseCommunicator = databaseCommunicator;
    }

}
