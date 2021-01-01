package library.assistant.ui.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.settings.Preferences;
import library.assistant.ui.main.MainController;
import org.apache.commons.codec.digest.DigestUtils;

public class LoginController implements Initializable {

    Preferences  preference;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private Label titleLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preference = Preferences.getPreferences();
        
    }    

    @FXML
    private void handleLoginAction(ActionEvent event) {
        titleLabel.setText("Library Assistant Login");
        titleLabel.setStyle("-fx-background-color:black;-fx-text-fill:white");
        String uname = username.getText();
        String pword =  DigestUtils.sha1Hex(password.getText()); 
        
        if(uname.equals(preference.getUsername())&&pword.equals(preference.getPassword()))
        {
            closeStage();
            loadMain(); 
        }
        else
        {   
            titleLabel.setText("Invalid Credentials");
            titleLabel.setStyle("-fx-background-color:#d32f2f;-fx-text-fill:white");
        }
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        System.exit(0);
    }
    
    
    void loadMain()
    {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/library/assistant/ui/main/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Library Assitant");
            stage.setScene(new Scene(parent));
            
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void  closeStage()
    {
        ((Stage)username.getScene().getWindow()).close();
    }
}
