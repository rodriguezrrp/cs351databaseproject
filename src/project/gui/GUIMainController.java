package project.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import project.gui.utils.MainSceneSwapper;

public class GUIMainController {

    @FXML
    private Button btnCust;
    @FXML
    private Button btnOrders;
    @FXML
    private Button btnInv;

    private MainSceneSwapper mainSceneSwapper;


    public GUIMainController() {}


    @FXML
    private void onActionCust() {
        System.out.println("cust");
    }

    @FXML
    private void onActionOrders() {
        System.out.println("orders");
    }

    @FXML
    private void onActionInv() {
        System.out.println("inv");
    }


    public void setMainSceneSwapper(MainSceneSwapper mainSceneSwapper) {
        // get access to the main scene swapper for this program
        this.mainSceneSwapper = mainSceneSwapper;
    }

}
