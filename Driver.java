import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

class SteeringWheel {

    private final String oracleLoginName = "";
    private final String oraclePassword = "";

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
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Warning", dialogButton);
        if (dialogResult == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

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
                    Option4();
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
                            "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);
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
                        while (cSet.next()) {
                            returnString.append(cSet.getString("cid") + ": " + cSet.getString("cname") + ", " + cSet.getString("number_of_orders") + ", " + cSet.getString("level") + "\n");
                        }
                        returnString.append("\nTranslators:\n");
                        while (tSet.next()) {
                            returnString.append(tSet.getString("cid") + ": " + tSet.getString("cname") + ", " + tSet.getString("number_of_orders") + ", " + tSet.getString("level") + "\n");
                        }
                        displayArea.setText(returnString.toString());
                        displayPanel.setViewportView(displayArea);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Option4();
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
            hikeSallariesButton.setText("Hike Sallaries");
            hikeSallariesButton.setToolTipText("Click this to open the \"Hike Sallary\" pop-up, which will allow you to enter an author to hike the translators sallaries over.");
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
        SteeringWheel s = new SteeringWheel();
        SteeringWheel.ProjectFrame p = s.new ProjectFrame();

        //p.pack();
        p.setVisible(true);
    }
}