package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
 * @version 05-20-2019
 */
public class MacysAcroDict extends Application {

    // connection to MySQL database
    private Connection conMySQL;
    private String startPrompt = "Explore Macy's acronyms below";

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
        stage.setMinWidth(390);
        stage.setMinHeight(272);
        stage.setMaxWidth(390);
        stage.setMaxHeight(272);
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
        // TextField for acronym input
        TextField acronymInput = new TextField();
        acronymInput.setPromptText("Enter acronym...");

        // TextArea to display output, read-only, wrap text, max width-390
        TextArea displayOutput = new TextArea();
        displayOutput.setEditable(false);
        displayOutput.setWrapText(true);
        displayOutput.setMaxWidth(390);

        // Text for header of output display area
        Text outputHeader = new Text();
        outputHeader.setText(startPrompt);

        // Button to add a new acronym to the database
        Button add = new Button();
        add.setText("Add Acronym");
        add.setOnAction(event -> {
            // fetch the input and clear the input field
            String acronym = acronymInput.getText();
            acronymInput.setText("");

            // clear the text area output field
            displayOutput.setText("");

            // if an empty string or null is provided, alert user and stop
            if (acronym == null || acronym.equals("")) {
                outputHeader.setText("Please enter a valid acronym");
                return;
            }

            try {
                /* check to see if the database already contains the data, if
                so, display it */
                Statement stmt = conMySQL.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM acro_table "
                    + "WHERE acronym = " + "'" + acronym + "';");
                if (rs.next()) {
                    // alert user that the data already exists in the database
                    outputHeader.setText("Acronym already in database, see data"
                        + " below");

                    // fetch the acronym data from the database
                    String acronymName = rs.getString("acronym");
                    String acronymStandsFor = rs.getString("stands_for");
                    String acronymShortDef = rs.getString("short_def");

                    // print the data to the Text Area
                    displayOutput.appendText("ACRONYM : " + acronymName
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
        search.setText("Search Acronym");
        search.setOnAction(event -> {
            // fetch the input and clear the input field
            String acronym = acronymInput.getText();
            acronymInput.setText("");

            // clear the text area output field
            displayOutput.setText("");

            // if an empty string or null is provided, alert user and stop
            if (acronym == null || acronym.equals("")) {
                outputHeader.setText("Please enter a valid acronym");
                return;
            }

            try {
                /* Create a Statement to execute a SQL query and store the
                result in a ResultSet. */
                Statement stmt = conMySQL.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM "
                    + "acro_table WHERE acronym = " + "'" + acronym
                    + "';");

                /* if the ResultSet is not empty, then show the acronym data,
                else add the new acronym */
                if (rs.next()) {
                    // fetch the acronym data from the database
                    String acronymName = rs.getString("acronym");
                    String acronymStandsFor = rs.getString("stands_for");
                    String acronymShortDef = rs.getString("short_def");

                    // print the data to the Text Area
                    displayOutput.appendText("ACRONYM : " + acronymName
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
        remove.setText("Remove Acronym");
        remove.setOnAction(event -> {
            // fetch the input and clear the input field
            String acronym = acronymInput.getText();
            acronymInput.setText("");

            // clear the text area output field
            displayOutput.setText("");

            // if an empty string or null is provided, alert user and stop
            if (acronym == null || acronym.equals("")) {
                outputHeader.setText("Please enter a valid acronym");
                return;
            }

            // prevent removal of the default MAD acronym
            if (acronym.equalsIgnoreCase("MAD")) {
                outputHeader.setText("Cannot remove the default MAD acronym");
                return;
            }

            try {
                /* check to see if the database already contains the data, if
                not, prompt them to add it, otherwise remove it */
                Statement stmt = conMySQL.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM acro_table "
                    + "WHERE acronym = " + "'" + acronym + "';");
                if (!rs.next()) {
                    /* alert the user that no such acronym exists, and invite
                    them to add it into the database */
                    (new AddPopUp(acronym)).display();
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
        edit.setText("Edit Acronym");
        edit.setOnAction(event -> {
            // fetch the input and clear the input field
            String acronym = acronymInput.getText();
            acronymInput.setText("");

            // clear the text area output field
            displayOutput.setText("");

            // if an empty string or null is provided, alert user and stop
            if (acronym == null || acronym.equals("")) {
                outputHeader.setText("Please enter a valid acronym");
                return;
            }

            // prevent editing of the default MAD acronym
            if (acronym.equalsIgnoreCase("MAD")) {
                outputHeader.setText("Cannot edit the default MAD acronym "
                    + "data");
                return;
            }

            try {
                /* check to see if the database already contains the data, if
                not, prompt them to add it, otherwise edit it */
                Statement stmt = conMySQL.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM acro_table "
                    + "WHERE acronym = " + "'" + acronym + "';");
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
        reset.setText("Reset Database");
        reset.setOnAction(event -> {
            // check for authentication first, stop if authentication failed
            Optional<String> result = authReset.showAndWait();
            if (!result.isPresent()
                || !result.get().equalsIgnoreCase("macystech")) {
                outputHeader.setText("Database has not been reset, "
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

            // clear all input fields
            acronymInput.setText("");
            displayOutput.setText("");

            // alert the user that the database has been reset
            outputHeader.setText("Database has been reset, add new acronyms "
                + "below");
        });

        // HBox for other operations
        HBox extra = new HBox();
        extra.getChildren().addAll(remove, edit, reset);

        // VBox for entire layout, contains Text, TextArea, and HBox
        VBox fullLayout = new VBox();
        fullLayout.getChildren().addAll(outputHeader, displayOutput, inputArea,
            extra);

        // Set the scene
        return new Scene(fullLayout);
    }
}
