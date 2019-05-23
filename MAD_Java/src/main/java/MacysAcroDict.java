package main.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Optional;

/**
 * Represents the Macy's Acronym Dictionary (MAD)
 * application
 *
 * @author Akshay Sathiya
 * @version 05-21-2019
 */
public class MacysAcroDict extends Application {

    // connection to MySQL database
    private Connection conMySQL;
    private String startPrompt = "Explore Macy's acronyms and jargon below";

    /**
     * The main method of the MAD application, launches the JavaFX program
     *
     * @param args a String array of command line args
     */
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        // Set and show the stage, with size constraints
        stage.setTitle("Macy's Acronym Dictionary (MAD) - Akshay Sathiya");
        stage.setScene(setScene());
        stage.setMinWidth(425);
        stage.setMinHeight(454);
        stage.setMaxWidth(425);
        stage.setMaxHeight(454);
        stage.show();

        // link the Java code and the MySQL database
        link();
    }

    /**
     * Loads the JDBC driver and creates the connection between the MySQL
     * database and the Java code. Links the Java code and the MySQL database.
     */
    private void link() {
        try {
            // load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // establish Java-MySQL connection
            conMySQL = DriverManager.getConnection("jdbc:mysql://"
                + "localhost:3306/macys_acro_dict", "root", "mysql");
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(MacysAcroDict.class.getName()).log(Level.SEVERE,
                null, e);
        }
    }

    /**
     * Sets the scene of the JavaFX application, and programs the UI components
     */
    private Scene setScene() {
        // Font for TextFields
        Font textFieldFont = new Font(16);

        // TextField for acronym input
        TextField acronymInput = new TextField();
        acronymInput.setFont(textFieldFont);
        acronymInput.setPromptText("Enter term...");

        // Font for TextAreas
        Font textAreaFont = new Font(16);

        // TextArea to display output, read-only, wrap text, with constraints
        TextArea displayOutput = new TextArea();
        displayOutput.setFont(textAreaFont);
        displayOutput.setEditable(false);
        displayOutput.setWrapText(true);
        displayOutput.setMaxWidth(425);
        displayOutput.setMinHeight(280);

        // Font for output header
        Font outputHeaderFont = new Font(20);

        // Text for header of output display area
        Text outputHeader = new Text();
        outputHeader.setText(startPrompt);
        outputHeader.setFont(outputHeaderFont);

        // Font for Buttons
        Font buttonFont = new Font(16);

        // Button to add a new acronym to the database
        Button add = new Button();
        add.setFont(buttonFont);
        add.setText("Add Term ");
        add.setOnAction(event -> {
            // fetch the input and clear the input field
            String acronym = acronymInput.getText();
            acronymInput.setText("");

            // clear the text area output field
            displayOutput.setText("");

            // if an empty string or null is provided, alert user and stop
            if (acronym == null || acronym.equals("")) {
                outputHeader.setText("Please enter a valid term");
                return;
            }

            try {
                /* check to see if the database already contains the data, if
                so, display it */
                Statement stmt = conMySQL.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM acro_table "
                    + "WHERE acronym = " + "\"" + acronym + "\";");
                if (rs.next()) {
                    // alert user that the data already exists in the database
                    outputHeader.setText("Term already in database, see data"
                        + " below");

                    // fetch the acronym data from the database
                    String acronymName = rs.getString("acronym");
                    String acronymStandsFor = rs.getString("stands_for");
                    String acronymShortDef = rs.getString("short_def");

                    // print the data to the Text Area
                    displayOutput.appendText("TERM : " + acronymName
                        + "\n\n");
                    displayOutput.appendText("STANDS FOR : " + acronymStandsFor
                        + "\n\n");
                    displayOutput.appendText("SHORT DEFINITION: "
                        + acronymShortDef);
                    displayOutput.positionCaret(0);
                    return;
                }
                stmt.close();
            } catch (SQLException e) {
                Logger.getLogger(MacysAcroDict.class.getName()).log(
                    Level.SEVERE, null, e);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // display the add pop-up
            (new AddPopUp(acronym)).display();

            // reset the output header text
            outputHeader.setText(startPrompt);
        });

        // Button to search for an acronym in the database
        Button search = new Button();
        search.setFont(buttonFont);
        search.setText("Search Term");
        search.setOnAction(event -> {
            // fetch the input and clear the input field
            String acronym = acronymInput.getText();
            acronymInput.setText("");

            // clear the text area output field
            displayOutput.setText("");

            // if an empty string or null is provided, alert user and stop
            if (acronym == null || acronym.equals("")) {
                outputHeader.setText("Please enter a valid term");
                return;
            }

            try {
                /* Create a Statement to execute a SQL query and store the
                result in a ResultSet. */
                Statement stmt = conMySQL.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM "
                    + "acro_table WHERE acronym = " + "\"" + acronym
                    + "\";");

                /* if the ResultSet is not empty, then show the acronym data,
                else add the new acronym */
                if (rs.next()) {
                    // fetch the acronym data from the database
                    String acronymName = rs.getString("acronym");
                    String acronymStandsFor = rs.getString("stands_for");
                    String acronymShortDef = rs.getString("short_def");

                    // print the data to the Text Area
                    displayOutput.appendText("TERM : " + acronymName
                        + "\n\n");
                    displayOutput.appendText("STANDS FOR : " + acronymStandsFor
                        + "\n\n");
                    displayOutput.appendText("SHORT DEFINITION: "
                        + acronymShortDef);
                    displayOutput.positionCaret(0);
                } else {
                    /* alert the user that no such acronym exists, and invite
                    them to add it into the database */
                    (new AddPopUp(acronym)).display();
                }
                stmt.close();
            } catch (SQLException e) {
                Logger.getLogger(MacysAcroDict.class.getName()).log(
                    Level.SEVERE, null, e);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // reset the output header text
            outputHeader.setText(startPrompt);
        });

        // Button to remove an acronym from the database
        Button remove = new Button();
        remove.setFont(buttonFont);
        remove.setText("Remove Term");
        remove.setOnAction(event -> {
            // fetch the input and clear the input field
            String acronym = acronymInput.getText();
            acronymInput.setText("");

            // clear the text area output field
            displayOutput.setText("");

            // if an empty string or null is provided, alert user and stop
            if (acronym == null || acronym.equals("")) {
                outputHeader.setText("Please enter a valid term");
                return;
            }

            // prevent removal of the default MAD acronym
            if (acronym.equalsIgnoreCase("MAD")) {
                outputHeader.setText("Cannot remove the default MAD term");
                return;
            }

            try {
                /* check to see if the database already contains the data, if
                not, prompt them to add it, otherwise remove it */
                Statement stmt = conMySQL.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM acro_table "
                    + "WHERE acronym = " + "\"" + acronym + "\";");
                if (!rs.next()) {
                    // alert the user that no such acronym exists
                    outputHeader.setText("That term does not exist in "
                        + "the database");
                    return;
                } else {
                    /* display the remove pop-up */
                    (new RemovePopUp(acronym, rs.getString("stands_for"),
                        rs.getString("short_def"))).display();
                }
                stmt.close();
            } catch (SQLException e) {
                Logger.getLogger(MacysAcroDict.class.getName()).log(
                    Level.SEVERE, null, e);
            }

            // reset the output header text
            outputHeader.setText(startPrompt);
        });

        // Button to edit an acronym's data in the database
        Button edit = new Button();
        edit.setFont(buttonFont);
        edit.setText("Edit Term Data");
        edit.setOnAction(event -> {
            // fetch the input and clear the input field
            String acronym = acronymInput.getText();
            acronymInput.setText("");

            // clear the text area output field
            displayOutput.setText("");

            // if an empty string or null is provided, alert user and stop
            if (acronym == null || acronym.equals("")) {
                outputHeader.setText("Please enter a valid term");
                return;
            }

            // prevent editing of the default MAD acronym
            if (acronym.equalsIgnoreCase("MAD")) {
                outputHeader.setText("Cannot edit the default MAD term "
                    + "data");
                return;
            }

            try {
                /* check to see if the database already contains the data, if
                not, prompt them to add it, otherwise edit it */
                Statement stmt = conMySQL.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM acro_table "
                    + "WHERE acronym = " + "\"" + acronym + "\";");
                if (!rs.next()) {
                    /* alert the user that no such acronym exists, and invite
                    them to add it into the database */
                    (new AddPopUp(acronym)).display();
                } else {
                    /* display the edit pop-up */
                    (new EditPopUp(acronym, rs.getString("stands_for"),
                        rs.getString("short_def"))).display();
                }
                stmt.close();
            } catch (SQLException e) {
                Logger.getLogger(MacysAcroDict.class.getName()).log(
                    Level.SEVERE, null, e);
            }

            // reset the output header text
            outputHeader.setText(startPrompt);
        });

        // HBox for input area, contains TextField and Buttons
        HBox inputArea = new HBox();
        inputArea.getChildren().addAll(acronymInput, search, add);

        // TextInputDialog for reset confirmation
        TextInputDialog authReset = new TextInputDialog();
        authReset.setTitle("Reset Database Authentication");
        authReset.setHeaderText("Resetting the database requires "
            + "authentication");
        authReset.setContentText("Enter password: ");

        /* Reset button, resets the program, empties database, except for MAD
        runs initialization.sql SQL script
         */
        Button reset = new Button();
        reset.setFont(buttonFont);
        reset.setText("  Reset Database   ");
        reset.setOnAction(event -> {
            // check for authentication first, stop if authentication failed
            Optional<String> result = authReset.showAndWait();
            if (!result.isPresent()
                || !result.get().equalsIgnoreCase("macystech")) {
                outputHeader.setText("Database not reset, "
                    + "authentication failed");
                return;
            }

            // reset authentication attempt text
            authReset.getEditor().setText("");

            // run the initialization.sql SQL script
            try {
                ScriptRunner runner = new ScriptRunner(conMySQL, false, false);
                String file = "/Users/akshaysathiya/Desktop/ComputerScience/JDB"
                    + "C/AkshaySathiya/MacysAcronymDictionary/MAD_SQL/initializ"
                    + "ation.sql"; // change this file path on other computer(s)
                runner.runScript(new BufferedReader(new FileReader(file)));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                Logger.getLogger(MacysAcroDict.class.getName()).log(
                    Level.SEVERE, null, e);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // clear fields
            acronymInput.setText("");
            displayOutput.setText("");

            // alert the user that the database has been reset
            outputHeader.setText("Database has been reset, add new terms "
                + "below");
        });

        // clear button, clears the main screen
        Button clear = new Button();
        clear.setFont(buttonFont);
        clear.setText("     Clear Screen     ");
        clear.setOnAction(event -> {
            // clear the input field and the display window
            acronymInput.setText("");
            displayOutput.setText("");

            // restore default output header text
            outputHeader.setText(startPrompt);
        });

        // about button, displays information about the MAD application
        Button about = new Button();
        about.setFont(buttonFont);
        about.setText("About MAD and Akshay Sathiya");
        about.setOnAction(event -> {
            outputHeader.setText("About MAD and Akshay Sathiya");
            displayOutput.setText("Hey! I am Akshay Sathiya, the developer of "
                + "the Macy's Acronym Dictionary (MAD) application!\n\n");
            displayOutput.appendText("During my first week of work at Macy's "
                + "Tech, I felt lost with all the new acronyms and jargon, so I"
                + " built this JavaFX tool that uses JDBC to connect to a MySQL"
                + " database which contains the acronyms and information about "
                + "them. This tool helps me and other new hires comprehend the "
                + "acronyms and jargon quickly and easily.\n"
                + "\n"
                + "This tool is an excellent display of my skills with Java, "
                + "SQL, JavaFX, JDBC, and MySQL database technology, and it "
                + "will help other new hires better adjust to their new "
                + "workplace.\n"
                + "\n"
                + "In this application, I directly ran a SQL script (.sql file)"
                + " from my Java code by using a Java file (.java file) called "
                + "ScriptRunner.java and its dependencies. The code in that "
                + "Java file and its dependencies belongs to a GitHub user by "
                + "the screen name of BenoitDuffez. The associated code for "
                + "integrating ScriptRunner.java with my own Java code also "
                + "belongs to BenoitDuffez. The ScriptRunner.java file, its "
                + "dependencies, and the integration code can be found on one "
                + "of BenoitDuffez's GitHub repositories. Here is the link: "
                + "https://github.com/BenoitDuffez/ScriptRunner. I have used "
                + "the assets from this repository responsibly, rightfully, "
                + "and fairly in the development of my project.\n\n");
            displayOutput.appendText("Thank you for using my application, I "
                + "hope you enjoy!");
            displayOutput.positionCaret(0);
        });

        // the download data button, allows the user to obtain all data in DB
        Button download = new Button();
        download.setText("Download All MAD Data");
        download.setFont(buttonFont);
        download.setOnAction(event -> {
            // clear fields
            acronymInput.setText("");
            displayOutput.setText("");

            try {
                /* obtain all the database data  */
                Statement stmt = conMySQL.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM acro_table;");

                // create a new file and write to it
                File downloadData = new File(System.getProperty("user.home")
                    + "/Desktop", "mad_data.txt");
                BufferedWriter out = new BufferedWriter(
                    new FileWriter(downloadData));
                out.write("Macy's Acronym Database (MAD) - Akshay Sathiya\n");
                out.append("                   ALL DATA\n");
                out.append("==============================================\n"
                    + "\n");
                while (rs.next()) {
                    // write the data for each term to the file
                    out.append("TERM : " + rs.getString("acronym") + "\n");
                    out.append("STANDS FOR : " + rs.getString("stands_for")
                        + "\n");
                    out.append("SHORT DEFINITION : " + rs.getString("short_def")
                        + "\n\n");
                }
                stmt.close();
                out.close();
            } catch (SQLException e) {
                Logger.getLogger(MacysAcroDict.class.getName()).log(
                    Level.SEVERE, null, e);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // restore default output header text
            outputHeader.setText("All MAD data downloaded as a flat file");
        });

        // HBox for other operations
        HBox extra = new HBox();
        extra.getChildren().addAll(remove, edit, reset);

        // HBox for more operations
        HBox extra2 = new HBox();
        extra2.getChildren().addAll(clear, about);

        // VBox for entire layout, contains Text, TextArea, and HBox
        VBox fullLayout = new VBox();
        fullLayout.getChildren().addAll(outputHeader, displayOutput, inputArea,
            extra, extra2, download);

        // Set the scene
        return new Scene(fullLayout);
    }
}
