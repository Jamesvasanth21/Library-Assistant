
package library.assistant.settings;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import org.apache.commons.codec.digest.DigestUtils;

public class Preferences {
    
    int noOfDays;
    float fine;
    String username;
    String password;
    
    public static final String CONFIG_FILE = "config.txt"; 
    
    public Preferences()
    {
        noOfDays = 14;
        fine=1;
        username="admin";
        setPassword("admin");
    }

    public int getNoOfDays() {
        return noOfDays;
    }

    public float getFine() {
        return fine;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setNoOfDays(int noOfDays) {
        this.noOfDays = noOfDays;
    }

    public void setFine(float fine) {
        this.fine = fine;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        if(password.length()<16)
        {
            this.password = DigestUtils.sha1Hex(password);
        }
        else
        {
            this.password = password;
        }
    }
    
    public static void initConfig()
    {
        Writer writer = null;
        try {
            Preferences preference = new Preferences();
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference,writer);
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
    public static Preferences getPreferences()
    {
    Gson gson = new Gson();
    Preferences preferences= new Preferences();
    try {
        preferences = gson.fromJson(new FileReader(CONFIG_FILE),Preferences.class);
    } catch (FileNotFoundException ex) {
        initConfig();
        Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
    }
    return preferences; 
    }
    
    
    public static void editConfig(Preferences preference)
    {
        Writer writer = null;
        try {
            
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference,writer);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Settings Updated");
            alert.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Settings Not Updated");
            alert.showAndWait();
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
}
