import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

class GroupProject {

    // Oracle Login Info
    private final String oracleLoginName = "woma0627";
    private final String oraclePassword = "LIap0Cb7";

    // Adds a customer to the database and calculates the number of orders for that customer
    public void AddCustomer(int id, String name, String level) {
        // Declares DB connection and creates DB Driver
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

            // Instantiate Stored Procedures
            init(dbConnection);

            // initialize the number of orders
            int numberOfOrders = 0;


            String avgNumberOfOrdersQuery1 = String.format("SELECT AVG(number_of_orders), Count(cid) " +
                    "FROM customer " +
                    "WHERE lvl = '%s'", level);

            ResultSet resultSet1 = stmt.executeQuery(avgNumberOfOrdersQuery1);
            if (resultSet1.next()) {
                if (resultSet1.getInt(2) != 0) {
                    numberOfOrders = (int) Math.round(resultSet1.getDouble(1));
                }
                else {
                    String avgNumberOfOrdersQuery2 = "SELECT AVG(number_of_orders) FROM customer";

                    ResultSet resultSet2 = stmt.executeQuery(avgNumberOfOrdersQuery2);
                    if(resultSet2.next()) {
                        numberOfOrders = (int)Math.round(resultSet2.getDouble(1));
                    }
                }
            }

            {
                CallableStatement addCustomer = dbConnection.prepareCall ("begin addcustomer (?,?,?,?); end;");
                addCustomer.setInt(1, id);
                addCustomer.setString(2,name);
                addCustomer.setInt(3,numberOfOrders);
                addCustomer.setString(4,level);
                addCustomer.execute ();
                addCustomer.close();
            }
            dbConnection.close();
        }
        catch( SQLException x ){
            System.out.println( "Couldn't get connection!" );
            System.out.println(x.getMessage());
        }
        catch( Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Exception occurred in executing the statement!");
        }
    }

    // Increases the salaries of all the translators by amounts that are based on the authors translated
    public void TranslatorSalaryHike(String authorName) {
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

            // executes procedures to update translator salaries as needed
            // updates salaries of translators that translated books by the given author
            {
                CallableStatement translatedAuthor = dbConnection.prepareCall ("begin hikesalarytranslatedauthor (?); end;");
                translatedAuthor.setString(1, authorName);
                translatedAuthor.execute ();
                translatedAuthor.close();
            }
            // updates salaries of translators that have not translated books by the given author but have translated more than 3 books
            {
                CallableStatement didNotTranslateAuthorAnd3OrMoreBooks = dbConnection.prepareCall ("begin hikesalaryoption2 (?); end;");
                didNotTranslateAuthorAnd3OrMoreBooks.setString(1, authorName);
                didNotTranslateAuthorAnd3OrMoreBooks.execute ();
                didNotTranslateAuthorAnd3OrMoreBooks.close();
            }
            // updates salaries of translators that have not translated books by the given author and have not translated more than 3 books
            {
                CallableStatement didNotTranslateAuthorAndLessThan3Books = dbConnection.prepareCall ("begin hikesalaryoption3 (?); end;");
                didNotTranslateAuthorAndLessThan3Books.setString(1, authorName);
                didNotTranslateAuthorAndLessThan3Books.execute ();
                didNotTranslateAuthorAndLessThan3Books.close();
            }
        }
        catch( SQLException x ){
            System.out.println( "Couldn't get connection!" );
        }
        catch( Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Exception occurred in executing the statement!");
        }

    }

    // Returns a result set of all customers
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
            String customerQuery = "SELECT cid, cname, number_of_orders, lvl FROM customer";

            // return result of query
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

    // Returns a result set of all translators
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
            String translatorQuery = "SELECT tid, tname, salary FROM translator";

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
    public void Exit() {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Warning", dialogButton);
        if (dialogResult == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // Creates the stored PL/SQL procedures
    private void init (Connection conn)
            throws SQLException
    {
        Statement stmt = conn.createStatement();
        try {
            // stored procedure for adding a customer
            stmt.execute("CREATE or REPLACE PROCEDURE addcustomer (cid INT, cname VARCHAR, number_of_orders INT, lvl VARCHAR) " +
                    "is begin INSERT INTO Customer values (cid, cname, number_of_orders, lvl); end;");

            // stored procedures for salary hikes
            stmt.execute("CREATE or REPLACE PROCEDURE  hikesalarytranslatedauthor (authorName VARCHAR ) " +
                    "is begin UPDATE translator set salary = salary * 1.1 " +
                    "WHERE tid in " +
                    "(SELECT tid FROM book WHERE btitle in " +
                    "(SELECT btitle FROM wrote WHERE aid in " +
                    "(SELECT aid FROM author WHERE aname = authorName))); end;");


            stmt.execute("CREATE or REPLACE PROCEDURE hikesalaryoption2 (authorName VARCHAR ) " +
                    "is begin UPDATE translator set salary = salary * 1.05 " +
                            "WHERE tid NOT in " +
                            "(SELECT tid FROM book WHERE btitle in " +
                            "(SELECT btitle FROM wrote WHERE aid in " +
                            "(SELECT aid FROM author WHERE aname = authorName))) " +
                            "AND tname in " +
                            "(SELECT tname FROM translator " +
                            "JOIN book ON book.tid = translator.tid " +
                            "GROUP BY tname HAVING COUNT(tname) > 2); end;");


            stmt.execute("CREATE or REPLACE PROCEDURE hikesalaryoption3 (authorname VARCHAR ) " +
                    "is begin UPDATE translator set salary = salary * 1.02 " +
                    "WHERE tid NOT in " +
                    "(SELECT tid FROM book WHERE btitle in " +
                    "(SELECT btitle FROM wrote WHERE aid in " +
                    "(SELECT aid FROM author WHERE aname = authorName))) " +
                    "AND tname NOT in " +
                    "(SELECT tname FROM translator " +
                    "JOIN book ON book.tid = translator.tid " +
                    "GROUP BY tname HAVING COUNT(tname) > 2); end;");

            stmt.close();
        } catch (SQLException e) {
        }
    }

    // GUI frame
    public class ProjectFrame extends JFrame {

        OutputPanel textPanel;
        JButton insertCustomerButton;
        JButton hikeSallariesButton;
        JButton updateDisplayButton;
        JButton quitButton;
        JPanel masterPanel;
        JScrollPane displayPanel;
        JTextArea displayArea;

        ProjectFrame() {
            setContentPane(masterPanel);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Exit();
                }
            });
            setTitle("Group25 Project");
            setSize(700, 450);
            insertCustomerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JTextField idField = new JTextField(5);
                    JTextField cNameField = new JTextField(5);
                    JTextField levelField = new JTextField(5);

                    JPanel myPanel = new JPanel();
                    myPanel.add(new JLabel("ID:"));
                    myPanel.add(idField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("Name:"));
                    myPanel.add(cNameField);
                    myPanel.add(Box.createHorizontalStrut(15));
                    myPanel.add(new JLabel("Level:"));
                    myPanel.add(levelField);
                    myPanel.add(Box.createHorizontalStrut(15));

                    int result = JOptionPane.showConfirmDialog(null, myPanel,
                            "Please Enter Customer Fields", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        AddCustomer(Integer.parseInt(idField.getText()), cNameField.getText(), levelField.getText());
                    }
                }
            });

            hikeSallariesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String userInput = JOptionPane.showInputDialog(null, "Please enter the name of the author.");
                    if (userInput == null) {
                        System.out.println("The user canceled");
                    } else {
                        TranslatorSalaryHike(userInput);
                    }
                }
            });

            updateDisplayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StringBuilder returnString = new StringBuilder("Customers:\n");
                    ResultSet cSet = GetCustomers();
                    ResultSet tSet = GetTranslators();
                    try {
                        returnString.append("CID\tNAME\tNUMBER OF ORDERS\tLEVEL\n");
                        while (cSet.next()) {
                            returnString.append(cSet.getInt(1) + ":\t "
                                    + cSet.getString(2) + ",\t "
                                    + cSet.getInt(3) + ",\t\t "
                                    + cSet.getString(4) + "\n");
                        }
                        returnString.append("\nTranslators:\n");
                        returnString.append("TID\tNAME\tSALARY\n");
                        while (tSet.next()) {
                            returnString.append(tSet.getInt(1) + ":\t "
                                    + tSet.getString(2) + ",\t "
                                    + tSet.getFloat(3) + "\n");
                        }
                        displayArea.setText(returnString.toString());
                        displayPanel.setViewportView(displayArea);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    } catch (NullPointerException e2) {
                        System.out.println("No data to display");
                    }
                }
            });

            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Exit();
                }
            });
        }

        {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
            $$$setupUI$$$();
        }

        /**
         * Method generated by IntelliJ IDEA GUI Designer
         * >>> IMPORTANT!! <<<
         * DO NOT edit this method OR call it in your code!
         *
         * @noinspection ALL
         */
        private void $$$setupUI$$$() {
            masterPanel = new JPanel();
            masterPanel.setLayout(new GridBagLayout());
            masterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Group25 Project", TitledBorder.RIGHT, TitledBorder.TOP));
            hikeSallariesButton = new JButton();
            hikeSallariesButton.setHideActionText(true);
            hikeSallariesButton.setText("Hike Salaries");
            hikeSallariesButton.setToolTipText("Click this to open the \"Hike Sallary\" pop-up, which will allow you to enter an author to hike the translators salaries over.");
            GridBagConstraints gbc;
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weighty = 0.25;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            masterPanel.add(hikeSallariesButton, gbc);
            updateDisplayButton = new JButton();
            updateDisplayButton.setText("Update Display");
            updateDisplayButton.setToolTipText("Click to refresh the displayed information on customers and translators.");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weighty = 0.25;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            masterPanel.add(updateDisplayButton, gbc);
            quitButton = new JButton();
            quitButton.setText("Quit");
            quitButton.setToolTipText("Click to quit the application.\\nWARNING: Ensure you refresh the display in order to check that your data is correct before exiting!");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weighty = 0.25;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            masterPanel.add(quitButton, gbc);
            insertCustomerButton = new JButton();
            insertCustomerButton.setText("Insert Customer");
            insertCustomerButton.setToolTipText("Click this button to open the \"Insert Customer\" pop-up, which will allow you to enter the customer's information and insert them into the customer table.");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0.25;
            gbc.weighty = 0.25;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            masterPanel.add(insertCustomerButton, gbc);
            final JPanel spacer1 = new JPanel();
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            masterPanel.add(spacer1, gbc);
            displayPanel = new JScrollPane();
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridheight = 5;
            gbc.weightx = 0.75;
            gbc.fill = GridBagConstraints.BOTH;
            masterPanel.add(displayPanel, gbc);
            displayPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Customers and Translators", TitledBorder.RIGHT, TitledBorder.TOP));
            displayArea = new JTextArea();
            displayArea.setEditable(false);
            displayArea.setEnabled(true);
            displayPanel.setViewportView(displayArea);
        }

        /**
         * @noinspection ALL
         */
        public JComponent $$$getRootComponent$$$() {
            return masterPanel;
        }
    }

    class InputFrame extends JFrame {

    }

    class OutputPanel extends JScrollPane {

    }
}

public class Driver {
    public static void main(String[] args) {
        GroupProject s = new GroupProject();
        GroupProject.ProjectFrame p = s.new ProjectFrame();

        p.setVisible(true);
    }
}