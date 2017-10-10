import javax.swing.*;
import java.sql.*;

public class Driver {

    private final String oracleLoginName = "";
    private final String oraclePassword = "";

    public static void main(String[] args) {

    }

    // Adds a customer to the database and calculates the number of orders for that customer
    public void AddCustomer(int id, String name, String level) {
        Connection dbConnection;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch(Exception ex) {
            System.out.println("Unable to load driver class: " + ex.getMessage());
        }

        try{
            // initializes connection object
            dbConnection= DriverManager.getConnection
                    ("jdbc:oracle:thin:@//oracle.cs.ou.edu:1521/pdborcl.cs.ou.edu",oracleLoginName,oraclePassword);
            Statement stmt = dbConnection.createStatement();

            // initialize the number of orders
            Integer numberOfOrders = 0;
            String avgNumberOfOrdersQuery1 = String.format("SELECT AVG(number_of_orders) " +
                    "FROM customer" +
                    "WHERE lvl = '%s'; ", level);

            // returns set of average number of orders
            ResultSet resultSet1 = stmt.executeQuery(avgNumberOfOrdersQuery1);
            if (resultSet1.next()) {
                numberOfOrders = (int)Math.round(resultSet1.getDouble(1));
            } else {
                String avgNumberOfOrdersQuery2 = "SELECT AVG(number_of_orders) FROM customer";

                ResultSet resultSet2 = stmt.executeQuery(avgNumberOfOrdersQuery2);
                if(resultSet2.next()) {
                    numberOfOrders = (int)Math.round(resultSet2.getDouble(1));
                } else {
                    numberOfOrders = 0;
                }
            }

            // calculate number of orders here
            String insertStatement = String.format("INSERT INTO Customer (cid, cname, number_of_orders, lvl) + " +
                    "values(%d,%s,%d,%s)",id,name,numberOfOrders,level);
        }
        catch( SQLException x ){
            System.out.println( "Couldn't get connection!" );
        }
        catch( Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Exception occurred in executing the statement!");
        }
    }

    // Increases the salaries of all the translators
    public void TranslatorSalaryHike(String authorName) {
        Connection dbConnection;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch(Exception ex) {
            System.out.println("Unable to load driver class: " + ex.getMessage());
        }

        //////////////////
        /// REWRITE USING PL SQL
        //////////////////

        try{
            dbConnection= DriverManager.getConnection
                    ("jdbc:oracle:thin:@//oracle.cs.ou.edu:1521/pdborcl.cs.ou.edu",oracleLoginName,oraclePassword);
            Statement stmt = dbConnection.createStatement();

            String translatedAuthor = String.format("UPDATE translator set salary = salary * 1.1 " +
                    "WHERE tid in " +
                    "(SELECT tid FROM book WHERE btitle in " +
                    "(SELECT btitle FROM wrote WHERE aid in " +
                    "(SELECT aid FROM author WHERE aname = '%s')))",authorName);

            String didNotTranslateAuthorAnd3OrMoreBooks = String.format("UPDATE translator set salary = salary * 1.05 " +
                    "WHERE tid NOT in " +
                    "(SELECT tid FROM book WHERE btitle in " +
                    "(SELECT btitle FROM wrote WHERE aid in " +
                    "(SELECT aid FROM author WHERE aname = '%s'))) " +
                    "AND tname in " +
                    "(SELECT tname FROM translator " +
                    "JOIN book ON book.tid = translator.tid " +
                    "GROUP BY tname HAVING COUNT(tname) > 2)",authorName);

            String didNotTranslateAuthorAndLessThan3Books = String.format("UPDATE translator set salary = salary * 1.02 " +
                    "WHERE tid NOT in " +
                    "(SELECT tid FROM book WHERE btitle in " +
                    "(SELECT btitle FROM wrote WHERE aid in " +
                    "(SELECT aid FROM author WHERE aname = '%s'))) " +
                    "AND tname NOT in " +
                    "(SELECT tname FROM translator " +
                    "JOIN book ON book.tid = translator.tid " +
                    "GROUP BY tname HAVING COUNT(tname) > 2)",authorName);

            // executes the statements
            stmt.execute(translatedAuthor);
            stmt.execute(didNotTranslateAuthorAnd3OrMoreBooks);
            stmt.execute(didNotTranslateAuthorAndLessThan3Books);
        }
        catch( SQLException x ){
            System.out.println( "Couldn't get connection!" );
        }
        catch( Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Exception occurred in executing the statement!");
        }

    }

    // Returns a result set of customers
    public ResultSet GetCustomers() {

        Connection dbConnection;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch(Exception ex) {
            System.out.println("Unable to load driver class: " + ex.getMessage());
        }

        try{
            dbConnection= DriverManager.getConnection
                    ("jdbc:oracle:thin:@//oracle.cs.ou.edu:1521/pdborcl.cs.ou.edu",oracleLoginName,oraclePassword);
            Statement stmt = dbConnection.createStatement();

            // Query for selecting customer
            String customerQuery = "SELECT * FROM customer;";

            return stmt.executeQuery(customerQuery);

        }
        catch( SQLException x ){
            System.out.println( "Couldn't get connection!" );
        }
        catch( Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Exception occurred in executing the statement!");
        }
        return null;
    }

    // Returns a result set of translators
    public ResultSet GetTranslators() {

        Connection dbConnection;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch(Exception ex) {
            System.out.println("Unable to load driver class: " + ex.getMessage());
        }

        try{
            // initializes database connection
            dbConnection= DriverManager.getConnection
                    ("jdbc:oracle:thin:@//oracle.cs.ou.edu:1521/pdborcl.cs.ou.edu",oracleLoginName,oraclePassword);
            Statement stmt = dbConnection.createStatement();

            // Query for selecting all translation
            String translatorQuery = "SELECT * FROM translator;";

            // Executes query and returns the result set
            return stmt.executeQuery(translatorQuery);
        }
        catch( SQLException x ){
            System.out.println( "Couldn't get connection!" );
        }
        catch( Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Exception occurred in executing the statement!");
        }
        return null;
    }

    // Gracefully Exits program
    public void Option4() {
        System.exit(0);
    }

    class ProjectFrame extends JFrame {

        // contains four buttons
        public  ProjectFrame() {
            //AddCustomer();
        }
    }

    class InputFrame extends JFrame {

    }

    class OutputPanel extends JPanel {

    }
}
