package library.assistant.ui.listbook;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.addbook.AddBookController;
import library.assistant.ui.main.MainController;
import library.assistant.util.LibraryAssistantUtil;

public class BookListController implements Initializable {

    DatabaseHandler handler;
    
    ObservableList<Book> list = FXCollections.observableArrayList();
    
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> idCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> publisherCol;
    @FXML
    private TableColumn<Book, Boolean> availabilityCol;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Book> tableView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       initCol();
       
       loadData();
    }  
    private void loadData()        
    {
        list.clear();
        handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM BOOK";
        ResultSet rs = handler.execQuery(qu);
        try {
            while(rs.next())
            {
                String titlex = rs.getString("title");
                String idx = rs.getString("id");
                String authorx = rs.getString("author");
                String publisherx = rs.getString("publisher");
                Boolean availabilityx = rs.getBoolean("isAvaill");
                list.add(new Book(titlex,idx,authorx,publisherx,availabilityx));
            }        
        }
        catch(SQLException ex)
        {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE,null,ex);
        
        }
        tableView.setItems(list);
        
    }
    private void initCol(){
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
        
    }

    @FXML
    private void handleBookDelete(ActionEvent event) {
        
        Book selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if(selectedForDeletion==null)
        {
            return;
        }
        Boolean alreadyIssued = DatabaseHandler.getInstance().isBookAlreadyIssued(selectedForDeletion);
        if(alreadyIssued)
        {
            Alert issuedAlert = new Alert(Alert.AlertType.INFORMATION);
            issuedAlert.setTitle("Information");
            issuedAlert.setHeaderText(null);
            issuedAlert.setContentText("Book  Can't be Deleted! \n The Book is Issued to Someone");
            issuedAlert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete");
            alert.setContentText("Are you sure you want to delete the book " + selectedForDeletion.getTitle() +" ?");

            Optional<ButtonType> answer = alert.showAndWait();

            if (answer.get() == ButtonType.OK)
            {
                Boolean result = DatabaseHandler.getInstance().deleteBook(selectedForDeletion);
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                if(result)
                {
                    alert1.setTitle("Success");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Book Deleted! \n"+ selectedForDeletion.getTitle() +" was deleted successfully");
                    alert1.showAndWait();
                    list.remove(selectedForDeletion);
                }
                else
                {
                    alert1.setTitle("Failed");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Book Deletion Failed");
                    alert1.showAndWait();
                }
            }
            else
            {
                return;
            }
        }
    }

    @FXML
    private void handleBookEdit(ActionEvent event) {
        
        Book selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if(selectedForEdit==null)
        {
            return;
        }
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addbook/add_book.fxml"));
            Parent parent = loader.load();
            
            AddBookController controller = (AddBookController)loader.getController();
            controller.inflateUI(selectedForEdit);
            
             
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book Details");
            stage.setScene(new Scene(parent));
            LibraryAssistantUtil.setStageIcon(stage);
            stage.show();
            
            stage.setOnCloseRequest((e)->{
                handleBookRefresh(new ActionEvent());
            });
            
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @FXML
    private void handleBookRefresh(ActionEvent event) {
        loadData();
    }
    public static class Book{
    
        private final SimpleStringProperty title;
        private final SimpleStringProperty id;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleBooleanProperty availability; 

        public Book(String title,String id,String author, String pub, Boolean avail)
        {
            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(pub);
            this.availability = new SimpleBooleanProperty(avail);
        }

        public String getTitle() {
            return title.get();
        }

        public String getId() {
            return id.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getPublisher() {
            return publisher.get();
        }

        public Boolean getAvailability() {
            return availability.get();
        }
    }
}
