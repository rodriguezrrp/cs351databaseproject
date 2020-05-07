package project.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import project.db.DBCommunicator;
import project.gui.utils.dbviewer.DBViewerBuilder;
import project.gui.utils.MainSceneSwapper;
import project.gui.utils.form.FormPaneBuilder;
import project.gui.utils.form.FormPaneController;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
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
    @FXML
    private Label welcomeLbl;

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

    private FormPaneBuilder addRepFormBuilder;

    // give it the customer's name and credit limit, it provides the builder you need
    private BiFunction<String, String, FormPaneBuilder> custCredLimUpdaterFormBuilderGenerator;
    // procures a list of the customer's names out of the database
    private Supplier<List<String>> custNameSelectDataSupplier = () -> {
        try {
            ResultSet res = databaseCommunicator.getListOfCustNames();
            List<String> nameList = new ArrayList<>();
            while(res.next()) {
                nameList.add(res.getString("CustomerName"));
            }
            return nameList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    };
    private FormPaneBuilder custCredLimFormBuilder;


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
//        System.out.println("cust");
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
//        System.out.println("orders");
        this.mainSceneSwapper.toPane(() -> {
            try {
                return this.ordersViewerBuilder.create();
            } catch (IOException e) {
                System.err.println("ordersViewerBuilder was unable to create a pane!");
                e.printStackTrace();
                return null;
            }
        });
    }

//    @FXML
//    private void onActionInv() {
//        System.out.println("inv");
//    }

    @FXML
    private void onActionExit() {
        // delegate the program-exiting task to the main scene swapper, as it may have things to do
        mainSceneSwapper.exitProgram();
    }


    public void setMainSceneSwapper(MainSceneSwapper mainSceneSwapper) {
        // get access to the main scene swapper for this application
        this.mainSceneSwapper = mainSceneSwapper;
        initializeBuilders(mainSceneSwapper == null);
    }
    public void setDatabaseCommunicator(DBCommunicator databaseCommunicator) {
        // get access to the database communicator for this application
        this.databaseCommunicator = databaseCommunicator;
    }

    public void personalizeName(String name) {
        if(name == null) return;
        this.welcomeLbl.setText("Hello, " + name + "!");
    }

    private void initializeBuilders(boolean justSetAllToNull) {
        this.custCredLimUpdaterFormBuilderGenerator = justSetAllToNull ? null : (String custName, String initCustCredLim) -> {
//            System.out.println("custName = " + custName);
//            System.out.println("initCustCredLim = " + initCustCredLim);
            return new FormPaneBuilder(
                    mainSceneSwapper,
                    "Update "+custName+"'s Credit Limit", "Update",
//                    custDataSupplier,
                    (Map<String,String> formDataMap) -> {
                        // when the form is closed, update the customer's data in the database
                        try {
                            boolean success = databaseCommunicator.updateCustCredLim(custName,formDataMap.get("CreditLimit"));
                            if(success) {
                                return FormPaneController.FormClosingAction.CLOSE_FORM_WITH_SUCCESS;
                            } else {
                                Alert a = new Alert(Alert.AlertType.ERROR,
                                        "Failed to submit the form's data to the database!" +
                                                "\nCheck the program's console output for details",
                                        ButtonType.CLOSE);
                                a.show();
                                return FormPaneController.FormClosingAction.CLOSE_FORM_WITH_FAILURE;
                            }
                        } catch (SQLException e) {
                            System.err.println("Failed to update customer cred limit!");
                            e.printStackTrace();
                            // create an alert to let the user know something went wrong.
                            Alert a = new Alert(Alert.AlertType.ERROR,
                                    "Failed to update credit limit! Please try again."
                                            + "\n" + e.getClass().getCanonicalName()
                                            + "\n" + e.getMessage(),
                                    ButtonType.CLOSE);
                            a.show();
                            return FormPaneController.FormClosingAction.CLOSE_FORM_WITH_FAILURE;
                        }
                    })
                    .addColumnLabelForField("CreditLimit","New Credit Limit:", initCustCredLim);
        };

        // note: when this form exits, it starts up the updater form (see above)
        this.custCredLimFormBuilder = justSetAllToNull ? null : new FormPaneBuilder(
                mainSceneSwapper,
                "Select Customer to Edit", "Select",
//                custNameSelectDataSupplier,
                (formData) -> { return FormPaneController.FormClosingAction.CLOSE_FORM_WITH_SUCCESS; }
                )
                .addColumnLabelForDropdown(custNameSelectDataSupplier, "CustomerName", "Customer To Edit:")
                .afterFormExits((FormPaneController.FormClosingAction closingAction, Map<String,String> formData) -> {
//                    System.out.println("closingAction = " + closingAction);
//                    System.out.println("formData = " + formData);
                    if(formData == null || closingAction.equals(FormPaneController.FormClosingAction.CLOSE_FORM_WITH_FAILURE))
                        return;
//                    System.out.println("We out here");
                    String name = formData.get("CustomerName");
                    String lim = databaseCommunicator.getCredLimOfCust(name);
                    FormPaneBuilder nextFormBldr = this.custCredLimUpdaterFormBuilderGenerator.apply(name, lim);
//                    this.mainSceneSwapper.back();
                    this.mainSceneSwapper.toPane(() -> {
                        try {
                            return nextFormBldr.create();
                        } catch (IOException e) {
                            System.err.println("custCredLimFormBuilder's afterFormExits was unable to create a pane!");
                            e.printStackTrace();
                            return null;
                        }
                    });
                });

        this.addRepFormBuilder = justSetAllToNull ? null : new FormPaneBuilder(
                mainSceneSwapper,
                "Add Representative", "Add",
                // function that consumes form's data:
                (Map<String,String> formDataMap) -> {
                    // when the form is closed, add the data into the database
                    try {
                        boolean success = databaseCommunicator.addRep(formDataMap);
                        if(success) {
                            return FormPaneController.FormClosingAction.CLOSE_FORM_WITH_SUCCESS;
                        } else {
                            Alert a = new Alert(Alert.AlertType.ERROR,
                                    "Failed to submit the form's data to the database!" +
                                            "\nCheck the program's console output for details",
                                    ButtonType.CLOSE);
                            a.show();
                            return FormPaneController.FormClosingAction.CLOSE_FORM_WITH_FAILURE;
                        }
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
                        return FormPaneController.FormClosingAction.CLOSE_FORM_WITH_FAILURE;
                    }
                })
                .addColumnLabelForField("FirstName","First Name:")
                .addColumnLabelForField("LastName","Last Name:")
                .addColumnLabelForField("Street","Street:")
                .addColumnLabelForField("City","City:")
                .addColumnLabelForField("State","State:")
                .addColumnLabelForField("PostalCode","Postal Code:")
                .addColumnLabelForField("Commission","Commission:")
                .addColumnLabelForField("Rate","Rate:")
                .afterFormExits((formClosingAction, formData) -> {  // TODO remove this chained method and lambda, was just for testing the afterFormExit handler
                    System.out.println("test: hello from after form exit!");
                    System.out.println("    formClosingAction = " + formClosingAction);
                    System.out.println("    formData = " + formData);
                });

        this.customerViewerBuilder = justSetAllToNull ? null : new DBViewerBuilder(this.mainSceneSwapper, customerDataSupplier)
                .addBackButton()
                .addButton("Update Customer Credit Limit",
                        () -> { return (ActionEvent actionEvent) -> {
                            /* start of event handler lambda */
                            this.mainSceneSwapper.toPane(() -> {
                                try {
                                    return custCredLimFormBuilder.create();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            });
                            /* end of event handler lambda */
                        }; },
                        DBViewerBuilder.ToolbarSide.LEFT
                )
                .addExitButton(/*this.mainSceneSwapper*/);

        this.repViewerBuilder = justSetAllToNull ? null : new DBViewerBuilder(this.mainSceneSwapper, repDataSupplier)
                .addBackButton()
                .addButton("Add Representative",
                        () -> { return (ActionEvent actionEvent) -> {
                            /* start of event handler lambda */
                            this.mainSceneSwapper.toPane(() -> {
                                try {
                                    return addRepFormBuilder.create();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            });
                            /* end of event handler lambda */
                        }; },
                        DBViewerBuilder.ToolbarSide.LEFT
                )
                .addExitButton(/*this.mainSceneSwapper*/);

        this.ordersViewerBuilder = justSetAllToNull ? null : new DBViewerBuilder(this.mainSceneSwapper, ordersDataSupplier)
                .addBackButton()
                .addExitButton();
    }

}
