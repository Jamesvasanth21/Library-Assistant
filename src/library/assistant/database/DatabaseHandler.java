package library.assistant.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import library.assistant.ui.listbook.BookListController.Book;

public final class DatabaseHandler {
    
    private static DatabaseHandler handler=null;
    
    private static Connection conn = null;
    private static final String driverConnection = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String DB_URL = "jdbc:derby:database/Library;create=true";
    private static Statement stmt = null;

    private DatabaseHandler()
    {
        createConnection();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();
    }
    /* All individual actions ie listbook, add book will create new datbase handler object 
    which will crash the application, so we are creating the instance once i.e.for the first time 
    and using it everywhere.*/
    public static DatabaseHandler getInstance()
    {
        if(handler ==null)
        {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    void createConnection() {
        try {
            Class.forName(driverConnection);
            conn = DriverManager.getConnection(DB_URL);
        }
        catch(SQLException | ClassNotFoundException e)
        {
            JOptionPane.showMessageDialog(null,"Can't Load Database","Database Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("in connection" + e);
            System.exit((0));
        }
    }

    void setupBookTable(){
        String TABLE_NAME ="BOOK";
        try{
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);

            if(tables.next())
            {
                System.out.println("Table "+TABLE_NAME+" already exists. Ready to go!" );
            }
            else{
                stmt.execute("CREATE TABLE " +TABLE_NAME+ "(" 
                        +" id varchar(200) primary key, \n"
                        +" title varchar(200),\n"
                        +" author varchar(200),\n"
                        +" publisher varchar(200),\n"
                        +" isAvaill boolean default true"
                        +")"
                );
            }
        }
        catch(SQLException e){
            System.err.println(e.getMessage()+".....setupDatabase");
        }
        finally{
        }
    }
    
    void setupMemberTable(){
        String TABLE_NAME ="MEMBER";
        try{
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);

            if(tables.next())
            {
                System.out.println("Table "+TABLE_NAME+" already exists. Ready to go!" );
            }
            else{
                stmt.execute("CREATE TABLE " +TABLE_NAME+ "(" 
                        +" id varchar(200) primary key, \n"
                        +" name varchar(200),\n"
                        +" mobile varchar(20),\n"
                        +" email varchar(100)\n"
                        +")"
                );
            }
        }
        catch(SQLException e){
            System.err.println(e.getMessage()+".....setupDatabase");
        }
        finally{
        }
    }
    void setupIssueTable(){
        String TABLE_NAME = "ISSUE";
        try{
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);

            if(tables.next())
            {
                System.out.println("Table "+TABLE_NAME+" already exists. Ready to go!" );
            }
            else{
                stmt.execute("CREATE TABLE " +TABLE_NAME+ "(" 
                        +" bookId varchar(200) primary key, \n"
                        +" memberId varchar(200),\n"
                        +" issueTime timestamp default CURRENT_TIMESTAMP,\n"
                        +" renew_count integer default 0,\n"
                        +" FOREIGN KEY (bookId) REFERENCES BOOK(id),\n"
                        +" FOREIGN KEY (memberId)REFERENCES MEMBER(id)\n"
                        +")"
                );
            }
        }
        catch(SQLException e){
            System.err.println(e.getMessage()+".....setupDatabase");
        }
        finally{
        }
    }
    
    public ResultSet execQuery(String query){
        ResultSet result;
        try{
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        }
        catch( SQLException ex)
        {
            System.out.println("Exception at execQuery:dataHandler"+ex.getLocalizedMessage());
            return null;
        }
        finally
        {}
        return result;
    }
    public boolean checkQuery(String qu){
        try
        {
            ResultSet result;
            stmt= conn.createStatement();
            result = stmt.executeQuery(qu);
            if(result.next())
                return true;
            else 
                return false;
        }
        catch(SQLException ex)
        {
            JOptionPane.showMessageDialog(null,"Error" +ex.getMessage(),"Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception ar execQuery:dataHandler"+ex.getLocalizedMessage());
            return false;
        }
        finally{
        }
    }
    public boolean execAction(String qu){
        try
        {
            stmt= conn.createStatement();
            stmt.execute(qu);
            return true;
        }
        catch(SQLException ex)
        {
            JOptionPane.showMessageDialog(null,"Error" +ex.getMessage(),"Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception ar execQuery:dataHandler"+ex.getLocalizedMessage());
            return false;
        }
        finally{
        }
    }
    
    public boolean deleteBook(Book book)
    {
        try {
            String deleteStatement = "DELETE FROM BOOK WHERE ID = ?";
            
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, book.getId());
            int res = stmt.executeUpdate();
            if(res==1)
            {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public boolean isBookAlreadyIssued(Book book)
    {
        try {
            String checkStatement = "SELECT COUNT(*) FROM ISSUE WHERE bookId = ?";
            
            PreparedStatement stmt1 = conn.prepareStatement(checkStatement);
            stmt1.setString(1, book.getId());
            ResultSet rs = stmt1.executeQuery();
            if(rs.next())
            {
                int count = rs.getInt(1);
                return count>0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean updatebook(Book book)
    {
        try {
            String updateStatement = "UPDATE BOOK SET TITLE =?,AUTHOR =?,PUBLISHER=? WHERE ID=?";
            
            PreparedStatement stmt1 = conn.prepareStatement(updateStatement);
            stmt1.setString(1, book.getTitle());
            stmt1.setString(2, book.getAuthor());
            stmt1.setString(3, book.getPublisher());
            stmt1.setString(4, book.getId());
            
            int result = stmt1.executeUpdate();
            
            return (result>0);
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public static void main(String[] args) {
       
        DatabaseHandler db = new DatabaseHandler();
        
    }
    
}
