
package library.assistant.ui.main;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DatabaseHandler;
import library.assistant.util.LibraryAssistantUtil;


public class MainController implements Initializable {

    @FXML
    private HBox book_info;
    @FXML
    private HBox member_info;
    @FXML
    private TextField bookIdInput;
    @FXML
    private Text bookName;
    @FXML
    private Text bookAuthor;
    @FXML
    private Text bookStatus;
    @FXML
    private TextField memberIdInput;
    @FXML
    private Text memberName;
    @FXML
    private Text memberContact;

    DatabaseHandler handler;
    
    ObservableList<String> issueData = FXCollections.observableArrayList();
    
    @FXML
    private JFXTextField bookId;
    @FXML
    private ListView<String> issueDataList;
    
    Boolean isReadyForSubmisson = false;
    @FXML
    private StackPane rootPane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JFXDepthManager.setDepth(book_info,3);
        JFXDepthManager.setDepth(member_info,3);
        handler = DatabaseHandler.getInstance();
        clearMemberCache();
        clearBookCache();
    }    

    @FXML
    private void loadAddMember(ActionEvent event) {
        loadWindow("/library/assistant/ui/addMember/member_add.fxml","Add New Member");
    }

    @FXML
    private void loadAddBook(ActionEvent event) {
        loadWindow("/library/assistant/ui/addbook/add_book.fxml","Add New Book");
        
    }

    @FXML
    private void loadViewMember(ActionEvent event) {
        loadWindow("/library/assistant/ui/listmember/member_list.fxml","Member List");
        
    }
    

    @FXML
    private void loadViewBook(ActionEvent event) {
        loadWindow("/library/assistant/ui/listbook/book_list.fxml","Book List");
        
    }
    @FXML
    private void loadSettings(ActionEvent event) {   
        loadWindow("/library/assistant/settings/settings.fxml","Settings");
        
    }
    
    void loadWindow(String loc,String title)
    {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            LibraryAssistantUtil.setStageIcon(stage);
            stage.show();
            
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadBookID(ActionEvent event) {
        clearBookCache();
        String id = bookIdInput.getText();
        String qu = "SELECT * FROM BOOK WHERE id ='"+id+"'";
        ResultSet rs = handler.execQuery(qu);
        Boolean flag = false;
        try {
            while(rs.next())
            {
                String titlex = rs.getString("title");
                String authorx = rs.getString("author");
                Boolean isAvailable = rs.getBoolean("isAvaill");
                flag = true;
                bookName.setText(titlex);
                bookAuthor.setText(authorx);
                String isAvail = (isAvailable)? "Available" : "Not Available";
                bookStatus.setText(isAvail);
            }
            if(!flag)
            {
                bookName.setText("No such book is available");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadMemberID(ActionEvent event) {
        clearMemberCache();
        String id = memberIdInput.getText();
        String qu = "SELECT * FROM MEMBER WHERE id ='"+id+"'";
        ResultSet rs = handler.execQuery(qu);
        Boolean flag = false;
        try {
            while(rs.next())
            {
                String namex = rs.getString("name");
                String contactx = rs.getString("mobile");
                flag = true;
                memberName.setText(namex);
                memberContact.setText(contactx);
            }
            if(!flag)
            {
                memberName.setText("No such memmber is available");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void clearBookCache()
    {
        bookName.setText(" ");
        bookAuthor.setText(" ");
        bookStatus.setText(" ");
    }
    void clearMemberCache()
    {
        memberName.setText(" ");
        memberContact.setText(" ");
    }

    @FXML
    private void loadIssueOperation(ActionEvent event) {
        
        String memberId = memberIdInput.getText();
        String bookId = bookIdInput.getText();
        
        if(bookId.isEmpty()||memberId.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Enter both Book ID and Member ID");
            alert.showAndWait();
            return;
        }
        else{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Issue Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to issue the book  "+bookName.getText()+" to "+ memberName.getText()+" ?");
        Optional<ButtonType> response = alert.showAndWait();
            if(response.get() == ButtonType.OK)
            {
                String str = "INSERT INTO ISSUE(memberId,BookId) VALUES (" +
                    "'" + memberId + "'," +
                    "'" + bookId + "'" +
                    ")";
                String str2 = "UPDATE BOOK SET isAvaill = false WHERE id = '"+bookId+"'";
                if(handler.execAction(str) && handler.execAction(str2))
                {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Success");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Book Issued");
                    alert1.showAndWait();
                    clearBookCache();
                    clearMemberCache();
                    memberIdInput.clear();
                    bookIdInput.clear();
                }
                else
                {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Failure");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Book Not Issued");

                    alert1.showAndWait();
                }
            }
            else{
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Issue Operation Failed");
                alert1.showAndWait();
            }
        }        
    }

    @FXML
    private void loadBookInfo2(ActionEvent event) {
        
        String checkBookId = bookId.getText();
        String qu = "Select * From ISSUE WHERE bookId = '"+checkBookId+"'";
        ResultSet rs = handler.execQuery(qu);
        if(!handler.checkQuery(qu))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Enter Correct Book Id");
            alert.showAndWait();
            return;
        }
        try {
            while(rs.next())
            {
                String retBookId = checkBookId;
                String retMemberId = rs.getString("memberId");
                Timestamp retTime = rs.getTimestamp("issueTime");
                int retRenewCount = rs.getInt("renew_count");
                
                issueData.add("Issue Date Time : "+retTime.toGMTString());
                issueData.add("Renew Count : "+retRenewCount);
                issueData.add("Book Information :");
                String qub = "Select * From BOOK WHERE id = '"+retBookId+"'";
                ResultSet rsb = handler.execQuery(qub);
                while(rsb.next())
                {
                    String retBTitle = rsb.getString("title");
                    String retBId = rsb.getString("id");
                    String retBAuthor = rsb.getString("author");
                    String retBPublisher = rsb.getString("publisher");
                    Boolean retBAvailability = rsb.getBoolean("isAvaill");
                     
                    issueData.add("\tBook Name : "+retBTitle);
                    issueData.add("\tBook Id : "+retBId);
                    issueData.add("\tAuthor : "+retBAuthor);
                    issueData.add("\tPulisher : "+retBPublisher);

                }
                issueData.add("Member Information :");
                String qum = "Select * From MEMBER WHERE id = '"+retMemberId+"'";
                ResultSet rsm = handler.execQuery(qum);
                while(rsm.next())
                {
                    String retMMame = rsm.getString("name");
                    String retMMobile = rsm.getString("mobile");
                    String retMId = rsm.getString("id");
                    String retMEmail = rsm.getString("email");
                     
                    issueData.add("\tMember Name : "+retMMame);
                    issueData.add("\tMemeber Id : "+retMId);
                    issueData.add("\tMember Mobile : "+retMMobile);
                    issueData.add("\tMember Email : "+retMEmail);

                }
                isReadyForSubmisson = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
             
        issueDataList.getItems().setAll(issueData); 
    }

    @FXML
    private void loadSubmitOperation(ActionEvent event) {
        
        if(!isReadyForSubmisson)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to submit");
            alert.showAndWait();
            return;
        }
        
        String id = bookId.getText();
        String updateIssueTable = "DELETE FROM ISSUE WHERE bookId = '"+id+"'";
        String updateBookTable = "UPDATE BOOK SET isAvaill = true WHERE id = '"+id+"'";
        
        Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
        alert1.setTitle("Confirm Submission Operation");
        alert1.setHeaderText(null);
        alert1.setContentText("Are you sure you want to return the book ?");
        Optional<ButtonType> response = alert1.showAndWait();
        if(response.get() == ButtonType.OK)
        {
            if(handler.execAction(updateIssueTable) && handler.execAction(updateBookTable))
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Book has been Submitted");
                alert.showAndWait();
                issueDataList.getItems().clear();
                bookId.clear();   
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed");
                alert.setHeaderText(null);
                alert.setContentText("Book submission has been failed");
                alert.showAndWait();
            }
        }
        else{
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Failed");
                alert2.setHeaderText(null);
                alert2.setContentText("Submission Operation Cancelled");
                alert2.showAndWait();
            }
    }

    @FXML
    private void loadRenewOperation(ActionEvent event) {
        
        if(!isReadyForSubmisson)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to renew");
            alert.showAndWait();
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Renew Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to renew the book ?");
        Optional<ButtonType> response = alert.showAndWait();
        if(response.get() == ButtonType.OK)
        {
            String id = bookId.getText();
            String updateIssueTable = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count  = renew_count + 1 WHERE bookId = '"+id+"'";
            if(handler.execAction(updateIssueTable))
            {
                //issueDataList.
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Success");
                alert2.setHeaderText(null);
                alert2.setContentText("Book has been Renewed");
                alert2.showAndWait();
            }
            else
            {
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("Failed");
                alert2.setHeaderText(null);
                alert2.setContentText("Book Renewal has been failed");
                alert2.showAndWait();
            }
            
        }
        else
        {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("Renew Operation Cancelled");
            alert1.showAndWait();
        }
        
    }

    @FXML
    private void handleCloseMenuItem(ActionEvent event) {
        ((Stage)rootPane.getScene().getWindow()).close();
        
    }

    @FXML
    private void handleMenuAddMember(ActionEvent event) {
        loadWindow("/library/assistant/ui/addMember/member_add.fxml","Add New Member");
    }

    @FXML
    private void handleMenuAddBook(ActionEvent event) {
        loadWindow("/library/assistant/ui/addbook/add_book.fxml","Add New Book");
    }

    @FXML
    private void handleMenuViewMember(ActionEvent event) {
        loadWindow("/library/assistant/ui/listmember/member_list.fxml","Member List");
    }

    @FXML
    private void handleMenuViewBook(ActionEvent event) {
        loadWindow("/library/assistant/ui/listbook/book_list.fxml","Book List");
    }

    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        
        Stage stage = ((Stage)rootPane.getScene().getWindow());   

        stage.setFullScreen(!stage.isFullScreen());

    }
}