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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ApparelViewerScreenController implements Initializable {

    public static Apparel chosenApparel = null;

    public ImageView apparelImageView;
    public ListView<String> outfitsListView;
    public Label nicknameLabel, typeLabel;
    public Button editButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nicknameLabel.setText(chosenApparel.getNickname());
        typeLabel.setText(chosenApparel.getBodyPart());
        apparelImageView.setImage(getApparelImage(chosenApparel.getId(), chosenApparel.getNickname()));

        ArrayList<Outfit> outfitList = Controller.getAllOutfitByProfile(Controller.selectedProfile.getNickname());

        // Displays every Outfit of which the chosen Apparel is a part
        for (Outfit o : outfitList) {
            if (o.apparelExistsInOutfit(chosenApparel.getId())) {
                outfitsListView.getItems().add(o.getNickname());
            }
        }

        // Edit Button Handler
        editButton.setOnAction(actionEvent ->  {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/ApparelEditorScreen.fxml"));
                try {
                    Parent root = loader.load();
                    ProfileMenuController.apparelViewPopup.setScene(new Scene(root));
                    ProfileMenuController.apparelViewPopup.setTitle("Digital Closet - Editing " + chosenApparel.getNickname());
                } catch (IOException e) {e.printStackTrace();}

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

}
