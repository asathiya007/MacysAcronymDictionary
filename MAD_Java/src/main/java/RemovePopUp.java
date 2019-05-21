package main.java;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RemovePopUp {

    // connection to MySQL database
    private Connection conMySQL;
    // acronym information
    private String acronym;
    private String standsFor;
    private String shortDesc;

    /**
     * Constructor for the RemovePopUp class.
     *
     * @param acronym the acronym that will be removed.
     * @param standsFor the acronym's stands-for.
     * @param shortDesc the acronym's short description.
     */
    public RemovePopUp(String acronym, String standsFor, String shortDesc) {
        // accept acronym info
        this.acronym = acronym;
        this.standsFor = standsFor;
        this.shortDesc = shortDesc;

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
        popUp.setTitle("MAD - Remove Acronym");
        popUp.setMinWidth(390);
        popUp.setMinHeight(245);
        popUp.setMaxWidth(390);
        popUp.setMaxHeight(245);

        // Text to alert the user that the acronym is not in the database
        Text alertUser = new Text();
        alertUser.setText("Confirm the removal of this data, or exit this "
            + "pop-up");

        // Text to display acronym, make uneditable
        TextField acronymText = new TextField();
        acronymText.setText(acronym);
        acronymText.setEditable(false);

        // Text for acronym stands-for, make uneditable
        TextField standsForText = new TextField();
        standsForText.setText(standsFor);
        standsForText.setEditable(false);

        // TextArea for short description, wrap text, make uneditable
        TextArea shortDescText = new TextArea();
        shortDescText.setWrapText(true);
        shortDescText.setText(shortDesc);
        shortDescText.setEditable(false);

        // Button to remove acronym
        Button removeAcronym = new Button();
        removeAcronym.setText("Remove Acronym");
        removeAcronym.setOnAction(event -> {
            try {
                // remove the data from the database using a PreparedStatement
                PreparedStatement prepStmt = conMySQL.prepareStatement("DELETE "
                    + "FROM acro_table WHERE acronym = '" + acronym + "';");
                prepStmt.execute();
                prepStmt.close();
            } catch (SQLException e) {
                Logger.getLogger(MacysAcroDict.class.getName()).log(
                    Level.SEVERE, null, e);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // clear the fields and close the pop-up window
            acronymText.setText("");
            standsForText.setText("");
            shortDescText.setText("");
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
        horzLayout.getChildren().addAll(removeAcronym, exit);

        // VBox for vertical layout
        VBox vertLayout = new VBox();
        vertLayout.getChildren().addAll(alertUser, acronymText, standsForText,
            shortDescText, horzLayout);

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
