package controllers;

import model.*;
import run.Main;

import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ProfileMenuController implements Initializable {

    public static Stage addApparelPopup = new Stage();
    public static Stage apparelViewPopup = new Stage();
    public static Stage outfitViewPopup = new Stage();

    public Label titleLabel;
    public Button profileEditButton, addApparelButton, addOutfitButton, editCancelButton, editSaveButton, mainMenuButton;
    public TilePane apparelTilePane, outfitTilePane;
    public TextField editProfileTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Main.stage.setTitle("Digital Closet - " + Controller.selectedProfile.getNickname());
        titleLabel.setText(Controller.selectedProfile.getNickname());

        // Reloads Apparel List after closing the Add Apparel window
        addApparelPopup.showingProperty().addListener((observableValue, aBoolean, t1) -> {

            if (!addApparelPopup.isShowing()) {

                try {
                    loadProfileApparel();
                } catch (IOException ignored) {}

            }

        });

        // Reloads Apparel List after closing the Apparel Viewer window
        apparelViewPopup.showingProperty().addListener((observableValue, aBoolean, t1) -> {

            if (!apparelViewPopup.isShowing()) {
                try {
                    loadProfileApparel();
                } catch (IOException ignored) {}
            }

        });

        // Reloads Outfit List after closing the Outfit Viewer window
        outfitViewPopup.showingProperty().addListener((observableValue, aBoolean, t1) -> {

            if (!outfitViewPopup.isShowing()) {
                    loadProfileOutfits();
            }

        });

        // Profile Edit Button
        profileEditButton.setOnAction(actionEvent -> {

            titleLabel.setVisible(false);
            profileEditButton.setVisible(false);

            editCancelButton.setVisible(true);
            editSaveButton.setVisible(true);
            editProfileTextField.setVisible(true);

            editProfileTextField.setText(Controller.selectedProfile.getNickname());

        });

        // Cancel Edit Button Handler
        editCancelButton.setOnAction(actionEvent -> {

            titleLabel.setVisible(true);
            profileEditButton.setVisible(true);

            editCancelButton.setVisible(false);
            editSaveButton.setVisible(false);
            editProfileTextField.setVisible(false);

        });

        // Edit Save Button Handler
        editSaveButton.setOnAction(actionEvent -> {

            boolean dupe = false;
            ArrayList<Profile> profiles = Controller.getAllProfiles();

            // Disallows an empty Profile Nickname
            if (editProfileTextField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Empty Nickname Entry");
                alert.setContentText("Can't change a Profile's nickname to something empty, please try again.");
                alert.showAndWait();
            } else {

                // Disallows duplicate Profile Nicknames
                for (Profile p : profiles) {
                    if (editProfileTextField.getText().equals(p.getNickname())) {
                        dupe = true;
                    }
                }

                if (dupe) {

                    if (editProfileTextField.getText().equals(Controller.selectedProfile.getNickname())){
                        editProfileTextField.setVisible(false);
                        editCancelButton.setVisible(false);
                        editSaveButton.setVisible(false);

                        titleLabel.setText(editProfileTextField.getText());
                        titleLabel.setVisible(true);
                        profileEditButton.setVisible(true);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Profile Already Exists");
                        alert.setContentText("A profile with the nickname \"" + editProfileTextField.getText() + "\" already exists!");
                        alert.showAndWait();
                    }
                } else {
                    Controller.editProfile(Controller.selectedProfile.getNickname(), editProfileTextField.getText());


                    Controller.selectedProfile.setNickname(editProfileTextField.getText());

                    editProfileTextField.setVisible(false);
                    editCancelButton.setVisible(false);
                    editSaveButton.setVisible(false);

                    titleLabel.setText(editProfileTextField.getText());
                    titleLabel.setVisible(true);
                    profileEditButton.setVisible(true);
                }
            }

        });

        // Return to Main Menu Button
        mainMenuButton.setOnAction(actionEvent -> {
            Controller.selectedProfile = null;
            switchScene("/screens/HomeScreen.fxml");
        });

        // Loads the Apparel and Outfits
        try {
            loadProfileApparel();
            loadProfileOutfits();
        } catch (IOException e) {e.printStackTrace();}

    }

    public void loadProfileApparel() throws IOException {

        // Clears existing elements in the Tile Pane
        apparelTilePane.getChildren().clear();










        // Creates an "Add Apparel Button"
        Button addButton = new Button();
        ImageView addButtonImg = new ImageView(new Image(this.getClass().getResourceAsStream("/resources/plus-icon.jpg")));
        addButton.setGraphic(addButtonImg);
        addButton.setMinSize(100.0, 100.0);
        addButtonImg.setFitWidth(95.0);
        addButtonImg.setFitHeight(95.0);
        apparelTilePane.getChildren().add(addButton);
        addButton.setOnAction(actionEvent ->  {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AddApparelScreen.fxml"));
            try {
                Parent root = loader.load();
                addApparelPopup.setScene(new Scene(root));
                addApparelPopup.setTitle("Digital Closet - Add Apparel");
                addApparelPopup.show();
            } catch (IOException ignored) {}

        });

        // Collects Apparel associated with the selected profile from the SQL database
        HashMap<Integer, Apparel> apparelList = Controller.getAllApparelByProfile(Controller.selectedProfile.getNickname());
        for (Apparel a : apparelList.values()) {

            Button apparelSelectorButton = new Button(a.getNickname());
            ImageView apparelImageView = new ImageView();
            Image apparelImage = getApparelImage(a.getId(), a.getNickname());

            // Button Styling
            apparelSelectorButton.setMinSize(100.0, 100.0);
            apparelImageView.setImage(apparelImage);
            apparelImageView.setFitHeight(95);
            apparelImageView.setFitWidth(95);
            apparelImageView.setPreserveRatio(false);
            apparelSelectorButton.setGraphic(apparelImageView);
            apparelSelectorButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            apparelTilePane.getChildren().add(apparelSelectorButton);

            // Handles selecting the Apparel
            apparelSelectorButton.setOnAction(actionEvent ->  {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/ApparelViewerScreen.fxml"));
                try {
                    ApparelViewerScreenController.chosenApparel = a;
                    Parent root = loader.load();
                    apparelViewPopup.setScene(new Scene(root));
                    apparelViewPopup.setTitle("Digital Closet - Viewing " + a.getNickname());
                    apparelViewPopup.show();
                } catch (IOException ignored) {}

            });

        }

    }

    public void loadProfileOutfits() {

        // Clears existing elements in the Tile Pane
        outfitTilePane.getChildren().clear();

        // Collect Outfits associated with the selected profile from the SQL database
        ArrayList<Outfit> outfitList = Controller.getAllOutfitByProfile(Controller.selectedProfile.getNickname());
        HashMap<Integer, Apparel> apparelList = Controller.getAllApparelByProfile(Controller.selectedProfile.getNickname());

        for (Outfit o : outfitList) {

            FlowPane outfitFlowPane = new FlowPane();
            ScrollPane outfitContainer = new ScrollPane(outfitFlowPane);
            Button outfitSelector = new Button(o.getNickname());

            for (Integer i : o.getApparelIDList()) {
                if (i != 0) {
                    GridPane imageContainer = new GridPane();
                    ImageView image = new ImageView(getApparelImage(i, apparelList.get(i).getNickname()));
                    imageContainer.setGridLinesVisible(true);
                    imageContainer.getChildren().add(image);
                    imageContainer.setPrefSize(100.0, 100.0);
                    image.setFitWidth(98.0);
                    image.setFitHeight(98.0);
                }
            }

            // Styling
            outfitContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            outfitContainer.setPrefSize(728.0, 115.0);
            outfitFlowPane.setOrientation(Orientation.VERTICAL);
            outfitFlowPane.setPrefSize(727.0, 100.0);
            outfitSelector.setFont(new Font(14));
            outfitSelector.setWrapText(true);
            outfitSelector.setPrefSize(140.0, 100.0);

            outfitTilePane.getChildren().add(outfitContainer);

            // Adds the Image Preview of each Apparel in the Outfit
            outfitFlowPane.getChildren().add(outfitSelector);
            for (Integer i : o.getApparelIDList()) {
                if (i != 0) {
                    GridPane imageContainer = new GridPane();
                    ImageView image = new ImageView(getApparelImage(i, apparelList.get(i).getNickname()));
                    imageContainer.setGridLinesVisible(true);
                    imageContainer.getChildren().add(image);
                    imageContainer.setPrefSize(100.0, 100.0);
                    image.setFitWidth(98.0);
                    image.setFitHeight(98.0);
                    outfitFlowPane.getChildren().add(imageContainer);
                }
            }

            // Selector Handler
            outfitSelector.setOnAction(actionEvent ->  {

                OutfitViewerScreenController.chosenOutfit = o;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/OutfitViewerScreen.fxml"));

                try {
                    Parent root = loader.load();
                    outfitViewPopup.setScene(new Scene(root));
                    outfitViewPopup.setTitle("Digital Closet - Viewing " + o.getNickname());
                    outfitViewPopup.show();
                } catch (IOException e) {e.printStackTrace();}

            });

        }

        // Creates a "Create New Outfit" Button
        Button createOutfitButton = new Button("Create New Outfit");
        createOutfitButton.setPrefSize(275.0, 115.0);
        outfitTilePane.getChildren().add(createOutfitButton);

        createOutfitButton.setOnAction(actionEvent -> {
            Controller.createOutfit("Outfit " + (outfitList.size() + 1), Controller.selectedProfile.getNickname());
            loadProfileOutfits();
        });

    }

    private Image getApparelImage(int apparelID, String apparelNickname) {
        // Find correct image for each Apparel's button
        if (Files.exists(Path.of("./images/" + apparelID + apparelNickname + ".jpg"))) {
            return new Image("file:./images/" + apparelID + apparelNickname + ".jpg");
        } else if (Files.exists(Path.of("./images/" + apparelID + apparelNickname + ".png"))) {
            return new Image("file:./images/" + apparelID + apparelNickname + ".png");
        } else {
            // assign no image or assign "no image" placeholder
            //return new Image("file:/resources/no-image-icon.jpg");
            return new Image(this.getClass().getResourceAsStream("/resources/no-image-icon.jpg"));
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