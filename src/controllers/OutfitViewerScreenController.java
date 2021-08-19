package controllers;

import javafx.scene.image.Image;
import model.Apparel;
import model.Controller;
import model.Outfit;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.ResourceBundle;

public class OutfitViewerScreenController implements Initializable {

    public static Outfit chosenOutfit = null;

    public Button editButton;
    public Label outfitNameLabel;
    public TilePane apparelDisplayTilePane;
    public Label emptyOutfitWarningLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        outfitNameLabel.setText(chosenOutfit.getNickname());

        // Edit Button Handler
        editButton.setOnAction(actionEvent ->  {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/OutfitEditorScreen.fxml"));
            try {
                Parent root = loader.load();
                ProfileMenuController.outfitViewPopup.setScene(new Scene(root));
                ProfileMenuController.outfitViewPopup.setTitle("Digital Closet - Editing " + chosenOutfit.getNickname());
            } catch (IOException e) {e.printStackTrace();}

        });

        loadApparelDisplay();

    }

    public void loadApparelDisplay() {

        // Clear the tile pane
        apparelDisplayTilePane.getChildren().clear();
        HashMap<Integer, Apparel> apparelList = Controller.getAllApparelByProfile(Controller.selectedProfile.getNickname());

        boolean empty = true;

        // Displays an image for each Apparel in the Outfit
        for (Integer i : chosenOutfit.getApparelIDList()) {

            if (i != 0){

                empty = false;

                Apparel currentApparel = apparelList.get(i);

                Button apparelSelector = new Button(currentApparel.getNickname() + " | " + currentApparel.getBodyPart());
                ImageView apparelImage = new ImageView(getApparelImage(currentApparel.getId(), currentApparel.getNickname()));
                apparelSelector.setGraphic(apparelImage);

                // Button Styling
                apparelSelector.setGraphicTextGap(0.0);
                apparelSelector.setContentDisplay(ContentDisplay.TOP);
                apparelSelector.setPrefSize(141.0, 154.0);
                apparelImage.setPreserveRatio(false);
                apparelImage.setFitHeight(125.0);
                apparelImage.setFitWidth(125.0);

                apparelDisplayTilePane.getChildren().add(apparelSelector);

            }

        }

        emptyOutfitWarningLabel.setVisible(empty);

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
}
