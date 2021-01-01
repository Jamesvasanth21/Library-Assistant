package library.assistant.settings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public class SettingsController implements Initializable {

    @FXML
    private JFXTextField daysAllowed;
    @FXML
    private JFXTextField fine;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDefaultValues();
    }    

    @FXML
    private void handleSaveButton(ActionEvent event) {
        int newDays = Integer.parseInt(daysAllowed.getText());
        float newFine = Float.parseFloat(fine.getText());
        String eUsername = username.getText();
        String ePassword = password.getText();
        
        Preferences preferences= Preferences.getPreferences();
        preferences.setNoOfDays(newDays);
        preferences.setFine(newFine);
        preferences.setUsername(eUsername);
        preferences.setPassword(ePassword);
        Preferences.editConfig(preferences);
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        
        Stage stage =(Stage)daysAllowed.getScene().getWindow();
        stage.close();
    }
    private void initDefaultValues()
    {
        Preferences preferences= new Preferences();
        daysAllowed.setText(String.valueOf(preferences.getNoOfDays()));
        fine.setText(String.valueOf(preferences.getFine()));
        username.setText(String.valueOf(preferences.getUsername()));
        password.setText(String.valueOf(preferences.getPassword()));
        
    }
}
