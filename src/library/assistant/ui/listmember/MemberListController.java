
package library.assistant.ui.listmember;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import library.assistant.database.DatabaseHandler;

public class MemberListController implements Initializable {
     
    DatabaseHandler handler;
    ObservableList<Member> list = FXCollections.observableArrayList();
    @FXML
    private TableView<Member> tableView;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> idCol;
    @FXML
    private TableColumn<Member, String> mobileCol;
    @FXML
    private TableColumn<Member, String> emailCol;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    } 
    private void loadData()        
    {
        handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM MEMBER";
        ResultSet rs = handler.execQuery(qu);
        try {
            while(rs.next())
            {
                String namex = rs.getString("name");
                String mobilex = rs.getString("mobile");
                String idx = rs.getString("id");
                String emailx = rs.getString("email");
                list.add(new MemberListController.Member(namex,idx,mobilex,emailx));
            }        
        }
        catch(SQLException ex)
        {
            Logger.getLogger(MemberListController.class.getName()).log(Level.SEVERE,null,ex);
        
        }
        tableView.getItems().setAll(list);
        
    }
    private void initCol(){
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
    public static class Member{
    
        private final SimpleStringProperty name;
        private final SimpleStringProperty id;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;
       
        Member(String name,String id,String mobile, String email)
        {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
         
        }

        public String getName() {
            return name.get();
        }

        public String getId() {
            return id.get();
        }

        public String getMobile() {
            return mobile.get();
        }

        public String getEmail() {
            return email.get();
        }
    }
}
