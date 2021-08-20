package controllers;

import model.Controller;
import model.Profile;
import run.Main;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomeScreenController implements Initializable {

    public TilePane selectProfileTilePane;
    public Label noProfilesFoundLabel;

    public AnchorPane addProfileContainerAnchorPane;
    public Button addProfileToggleButton, addProfileSubmissionButton, aboutButton;
    public TextField addProfileTextField;

    public static Stage aboutPopup = new Stage();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Handling the "Add New Profile" text field and submission display to hide on startup
        addProfileTextField.setVisible(false);
        addProfileSubmissionButton.setVisible(false);
        addProfileTextField.managedProperty().bind(addProfileTextField.visibleProperty());
        addProfileSubmissionButton.managedProperty().bind(addProfileSubmissionButton.visibleProperty());

        // "Add New Profile" Button action
        addProfileToggleButton.setOnAction(actionEvent -> {
            if (addProfileTextField.isVisible()) {
                addProfileTextField.setVisible(false);
                addProfileSubmissionButton.setVisible(false);
            } else {
                addProfileTextField.setVisible(true);
                addProfileSubmissionButton.setVisible(true);
            }
        });

        // Add Profile Submission Button Action
        addProfileSubmissionButton.setOnAction(actionEvent -> {
            if (addProfileTextField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Empty Nickname Entry");
                alert.setContentText("Can't create a Profile with no name, please enter one and try again.");
                alert.showAndWait();
            } else {
                addProfile();
            }
        });

        // Show About Page Button
        aboutButton.setOnAction(actionEvent -> {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AboutScreen.fxml"));
            try {
                Parent root = loader.load();
                aboutPopup.setScene(new Scene(root));
                aboutPopup.setTitle("Digital Closet - About");
                aboutPopup.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        // Begin adding Profile selection buttons and their action events
        loadProfileSelectList();

    }

    public void loadProfileSelectList() {

        // Clear any existing Profile selection buttons in the Selection Tile Pane
        selectProfileTilePane.getChildren().clear();

        // Collect existing Profiles from SQL database
        ArrayList<Profile> profiles = Controller.getAllProfiles();

        // Let the user know in the case that there are no Profiles
        if (profiles.isEmpty()) {
            noProfilesFoundLabel.setVisible(true);
        } else {
            noProfilesFoundLabel.setVisible(false);

            // Restricts adding Profiles at 10
            if (profiles.size() >= 10){
                addProfileTextField.setPromptText("Profile list full!");
                addProfileTextField.setDisable(true);
                addProfileSubmissionButton.setDisable(true);
            } else {
                addProfileTextField.setPromptText("Enter Profile Name");
                addProfileTextField.setDisable(false);
                addProfileSubmissionButton.setDisable(false);
            }

            // Add a menu option for each existing Profile
            for (Profile p : profiles) {

                AnchorPane buttonContainer = new AnchorPane();
                Button profileButton = new Button(p.getNickname());
                Button deleteButton = new Button("X");

                // Button styling
                buttonContainer.setPrefSize(370, 35);
                profileButton.setPrefSize(370, 35);
                deleteButton.setPrefSize(25, 29);
                buttonContainer.getChildren().addAll(profileButton, deleteButton);
                AnchorPane.setRightAnchor(deleteButton, 15.0);
                AnchorPane.setTopAnchor(deleteButton, 3.0);
                AnchorPane.setBottomAnchor(deleteButton, 3.0);

                selectProfileTilePane.getChildren().add(buttonContainer);

                // Load Profile Menu on profile name selection
                profileButton.setOnAction(actionEvent -> {
                    Button target = (Button) actionEvent.getTarget();

                    for (Profile q : profiles) {
                        if (q.getNickname().equals(target.getText())) {
                            Controller.selectedProfile = q;
                            switchScene("/screens/ProfileMenuScreen.fxml");
                        }
                    }
                });

                // "X" Delete Button Action
                deleteButton.setOnAction(actionEvent -> {

                    // Display warning dialogue
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Digital Closet - Delete " + profileButton.getText() + "?");
                    alert.setHeaderText("Warning!");
                    alert.setContentText("Deleting a Profile is permanent and will also delete all associated clothing!");

                    ButtonType confirmDelete = new ButtonType("Delete anyway", ButtonBar.ButtonData.OK_DONE);
                    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(confirmDelete, buttonTypeCancel);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent()) {
                        if (result.get() == confirmDelete) {
                            Controller.deleteProfile(profileButton.getText());

                            loadProfileSelectList();
                        }
                    }

                });

            }
        }

    }

    public void addProfile() {

        boolean dupe = false;
        ArrayList<Profile> profiles = Controller.getAllProfiles();

        // Duplicate Profile Checking
        for (Profile p : profiles) {
            if (addProfileTextField.getText().equals(p.getNickname())) {
                dupe = true;
            }
        }

        if (dupe) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Profile Already Exists");
            alert.setContentText("A profile with the nickname \"" + addProfileTextField.getText() + "\" already exists!");
            alert.showAndWait();
        } else {
            Controller.addProfile(addProfileTextField.getText());

            addProfileTextField.setVisible(false);
            addProfileSubmissionButton.setVisible(false);
            addProfileTextField.setText("");
            loadProfileSelectList();
        }

    }


    public void switchScene(String sceneFileLocation) {
        // need to use double dot for relative pathing
        // I.E.: "/screens/ProfileMenuScreen.fxml"

        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneFileLocation));
        try {
            Parent root = loader.load();
            Main.stage.setScene(new Scene(root));
        } catch (IOException e) {e.printStackTrace();}
    }
}