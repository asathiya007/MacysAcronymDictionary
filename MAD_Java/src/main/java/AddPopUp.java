package main.java;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The add pop-up class.
 *
 * @author Akshay Sathiya
 * @version 05-21-2019
 */
public class AddPopUp {

    // connection to MySQL database
    private Connection conMySQL;
    // acronym String
    private String acronym;

    /**
     * Constructor for the AddPopUp class.
     *
     * @param acronym the acronym to be added.
     */
    public AddPopUp(String acronym) {
        // accept the acronym
        this.acronym = acronym;

        // link the Java code to the MySQL database
        link();
    }

    /**
     * Displays the pop-up window for adding the new acronym
     */
    public void display() {
        // create the new stage, with constraints
        Stage popUp = new Stage();
        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.setTitle("MAD - Add New Term");
        popUp.setMinWidth(390);
        popUp.setMinHeight(245);
        popUp.setMaxWidth(390);
        popUp.setMaxHeight(245);

        // Text to alert the user that the acronym is not in the database
        Text alertUser = new Text();
        alertUser.setText("That term is not in the database. "
            + "Add it, or exit this pop-up");

        // TextField for acronym input
        TextField acronymInput = new TextField();
        acronymInput.setText(acronym);

        // TextField for acronym stands-for
        TextField standsFor = new TextField();
        standsFor.setPromptText("Stands for...");

        // TextArea for short description, wrap text
        TextArea shortDesc = new TextArea();
        shortDesc.setWrapText(true);
        shortDesc.setPromptText("Short description about the acronym...");

        // Button to add acronym
        Button addAcronym = new Button();
        addAcronym.setText("Add New Term");
        addAcronym.setOnAction(event -> {
            // fetch the input
            String acroStandsFor = standsFor.getText();
            String acroShortDesc = shortDesc.getText();

            // if an empty string or null is provided, alert the user
            if (acroStandsFor == null || acroStandsFor.equals("")
                || acroShortDesc == null || acroShortDesc.equals("")) {
                alertUser.setText("Please provide complete input");
                return;
            }

            try {
                // add the data to the database using a PreparedStatement
                PreparedStatement prepStmt = conMySQL.prepareStatement("INSERT "
                    + "INTO acro_table VALUES (?, ?, ?);");
                prepStmt.setString(1, acronym);
                prepStmt.setString(2, acroStandsFor);
                prepStmt.setString(3, acroShortDesc);
                prepStmt.execute();
                prepStmt.close();
            } catch (SQLException e) {
                Logger.getLogger(MacysAcroDict.class.getName()).log(
                    Level.SEVERE, null, e);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // clear the input fields and close the pop-up window
            standsFor.setText("");
            shortDesc.setText("");
            popUp.close();
        });

        // Button to exit
        Button exit = new Button();
        exit.setText("Exit Pop-Up");
        exit.setOnAction(event -> {
            // close the pop-up stage
            popUp.close();
        });

        // HBox for horizontal layout
        HBox horzLayout = new HBox();
        horzLayout.getChildren().addAll(addAcronym, exit);

        // VBox for vertical layout
        VBox vertLayout = new VBox();
        vertLayout.getChildren().addAll(alertUser, acronymInput, standsFor,
            shortDesc, horzLayout);

        // Set the scene
        Scene scene = new Scene(vertLayout);

        // Show the stage
        popUp.setScene(scene);
        popUp.show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
