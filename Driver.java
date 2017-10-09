import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Driver {
    public static void main(String[] args) {
        final String oracleLoginName = "";
        final String oraclePassword = "";
        Connection dbConnection;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch(Exception ex) {
            System.out.println("Unable to load driver class: " + ex.getMessage());
        }

        try{
            dbConnection= DriverManager.getConnection
                    ("jdbc:oracle:thin:@//oracle.cs.ou.edu:1521/pdborcl.cs.ou.edu",oracleLoginName,oraclePassword);
            Statement stmt = dbConnection.createStatement(); // used to create SQL statements
        }
        catch( SQLException x ){
            System.out.println( "Couldn't get connection!" );
        }

    }
}

class ProjectFrame extends JFrame {

    // contains four buttons
    public void Option1() {

    }

    public void Option2() {

    }

    public void Option3() {

    }

    // Gracefully Exits program
    public void Option4() {
        System.exit(0);
    }
}

class InputFrame extends JFrame {

}

class OutputPanel extends JPanel {

}
