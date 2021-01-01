
package library.assistant.ui.addMember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import library.assistant.database.DatabaseHandler;

public class MemberAddController implements Initializable {

    DatabaseHandler handler;
    @FXML
    private JFXTextArea id;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXTextArea name;
    @FXML
    private JFXTextArea mobile;
    @FXML
    private JFXTextArea email;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
    }    
    @FXML
    private void addMember(ActionEvent event) {
        String namex = name.getText();
        String idx = id.getText();
        String mobilex = mobile.getText();
        String emailx = email.getText();
        Boolean flag = namex.isEmpty() || idx.isEmpty() || mobilex.isEmpty() || emailx.isEmpty();
        
        if(flag)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Enter All the Feilds");
            alert.showAndWait();
            return;
        }
        else{
        String st = "INSERT INTO MEMBER VALUES("+
                "'"+ idx +"',"+
                "'"+ namex +"',"+
                "'"+ mobilex +"',"+
                "'"+ emailx +"'"+
                ")";
        System.out.println(st);
        if(handler.execAction(st))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Success");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed");
            alert.showAndWait();
        }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
    }

    
}
