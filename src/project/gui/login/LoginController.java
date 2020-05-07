package project.gui.login;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import project.db.DBCommunicator;

import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField userField;
    @FXML
    private PasswordField pswdField;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnYes;
    @FXML
    private Region spacerRegion;
    @FXML
    private Label loginErrMsg;

    private DBCommunicator dbCommunicator;

    private boolean loginSuccess = false;
    private String loginName = null;

    public LoginController(DBCommunicator dbCommunicator) {
        this.dbCommunicator = dbCommunicator;
    }

    @FXML
    private void initialize() {
        // setup the button row spacer to space them apart properly
        HBox.setHgrow(spacerRegion, Priority.ALWAYS);
        loginErrMsg.setVisible(false);
    }

    @FXML
    private void onActionCancel() {
//        this.btnCancel.getScene().getWindow().fireEvent(new WindowEvent(
//                this.btnCancel.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST
//        ));
        ((Stage) this.btnCancel.getScene().getWindow()).close();
    }

    @FXML
    private void onActionYes() {
        try {
            String loginName = dbCommunicator.authLogin(
                    userField.getText(),
                    pswdField.getText()
            );
            this.loginSuccess = ((this.loginName = loginName) != null);
            if(loginSuccess) {
                ((Stage) this.btnCancel.getScene().getWindow()).close();
            } else {
                loginErrMsg.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR,
                    "Failed to validate login!"
                            + "\n" + e.getClass().getCanonicalName()
                            + "\n" + e.getMessage(),
                    ButtonType.CLOSE);
            a.show();
        }
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public String getLoginName() {
        return loginName;
    }

}
