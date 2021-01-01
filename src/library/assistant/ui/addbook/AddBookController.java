 
package library.assistant.ui.addbook;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.listbook.BookListController;

public class AddBookController implements Initializable {
   
    @FXML
    private JFXTextArea title;
    @FXML
    private JFXTextArea id;
    @FXML
    private JFXTextArea author;
    @FXML
    private JFXTextArea publisher;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    
    private DatabaseHandler databasehandler;
    @FXML
    private AnchorPane rootPane;
    
    private Boolean isInEditMode = Boolean.FALSE;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databasehandler = DatabaseHandler.getInstance();
        checkData();
    }    

    @FXML
    private void addBook(ActionEvent event) {
        String bookId = id.getText();
        String bookAuthor = author.getText();
        String bookName = title.getText();
        String bookPublisher = publisher.getText();
        
        if(bookId.isEmpty()||bookAuthor.isEmpty()||bookName.isEmpty()||bookPublisher.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Enter All the Feilds");
            alert.showAndWait();
            return;
        }
        
        if(isInEditMode)
        {
            handleEdit();
        }
        else
        {
        String qu = "Insert into BOOK Values("+
                "'" + bookId + "'," +
                "'" + bookName + "'," +
                "'" + bookAuthor + "'," +
                "'" + bookPublisher + "'," +
                "" + "true" + "" +
                ")";
        System.out.println(qu);  
        if(databasehandler.execAction(qu))
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
    private void cancelBook(ActionEvent event) {
        Stage stage =(Stage) rootPane.getScene().getWindow();
        stage.close();
    }
    
    public void checkData(){
        String qu = "SELECT title FROM BOOK";
        ResultSet rs = databasehandler.execQuery(qu);
        try {
            while(rs.next())
            {
                String titlex = rs.getString("title");
                System.out.println(titlex);
            }        
        }
        catch(SQLException ex)
        {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE,null,ex);
        
        }
        
    }
    
    public void inflateUI(BookListController.Book book)
    {
        title.setText(book.getTitle());
        id.setText(book.getId());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        
        id.setEditable(false);
        isInEditMode = Boolean.TRUE;
        
    }
    
    private void handleEdit()
    {
        BookListController.Book book = new BookListController.Book(title.getText(),id.getText(),author.getText(),publisher.getText(),true);
        if(databasehandler.updatebook(book))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Success\n Book Edited");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Failure \n Book Not Edited");
            alert.showAndWait();
        
        }
        
    }
    
}
