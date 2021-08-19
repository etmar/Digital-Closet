package controllers;

import model.Apparel;
import model.Controller;
import model.Outfit;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static controllers.ApparelViewerScreenController.chosenApparel;

public class ApparelEditorScreenController implements Initializable {

    public ImageView apparelImageView;
    public TextField changeNameTextField;
    public ChoiceBox<String> changeTypeChoiceBox;
    public Button cancelButton, selectNewImageButton, saveChangesButton, deleteButton;

    public Apparel editingApparel = chosenApparel;
    public String oldNickname = editingApparel.getNickname();
    public String oldType = editingApparel.getBodyPart();

    public String newNickname = null;
    public String newType = null;
    public File newImage = null;

    public boolean nameChanged = false;
    public boolean typeChanged = false;
    public boolean imageChanged = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        changeTypeChoiceBox.getItems().addAll("Outer Top", "Inner Top", "Bottom", "Footwear", "Accessory");
        changeNameTextField.setText(editingApparel.getNickname());
        changeTypeChoiceBox.setValue(editingApparel.getBodyPart());
        apparelImageView.setImage(getApparelImage(editingApparel.getId(), editingApparel.getNickname()));

        // Listener for Nickname change
        changeNameTextField.textProperty().addListener((observableValue, s, t1) -> {

            newNickname = t1;

            nameChanged = !t1.equals(oldNickname) && !t1.equals("");

            saveChangesButton.setDisable(!nameChanged && !typeChanged && !imageChanged);

        });

        // Listener for Type change
        changeTypeChoiceBox.valueProperty().addListener((observableValue, s, t1) -> {

            newType = t1;

            typeChanged = !t1.equals(oldType);

            saveChangesButton.setDisable(!nameChanged && !typeChanged && !imageChanged);

        });

        // Listener for Image change
        apparelImageView.imageProperty().addListener((observableValue, image, t1) -> {

            Image resource2 = new Image(this.getClass().getResourceAsStream("/resources/grey-background-2.png"));

            imageChanged = !apparelImageView.getImage().getUrl().equals(resource2.getUrl());

            saveChangesButton.setDisable(!nameChanged && !typeChanged && !imageChanged);

        });

        // SELECT IMAGE BUTTON HANDLER
        selectNewImageButton.setOnAction(actionEvent -> {

            FileChooser fc = new FileChooser();

            ArrayList<String> ext = new ArrayList<>();
            ext.add("*.png");
            ext.add("*.jpg");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Images (*.png) or (*.jpg)", ext);
            fc.getExtensionFilters().add(extFilter);

            File tempChosenFile = fc.showOpenDialog(ProfileMenuController.addApparelPopup);

            if (tempChosenFile != null) {

                String fileType = null;

                try {
                    fileType = Files.probeContentType(Paths.get(tempChosenFile.getAbsolutePath()));
                } catch (IOException ignored) {}

                if (fileType != null){
                    if (fileType.equals("image/jpeg") || fileType.equals("image/png")) {
                        newImage = tempChosenFile;
                        apparelImageView.setImage(new Image("file:" + newImage.getPath()));
                    } else {
                        apparelImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/grey-background-2.png")));
                    }
                } else {
                    apparelImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/grey-background-2.png")));
                }


            }

        });

        // DELETE BUTTON HANDLER
        deleteButton.setOnAction(actionEvent -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Digital Closet - Delete " + chosenApparel.getNickname() + "?");
            alert.setHeaderText("Warning!");
            alert.setContentText("Deleting Apparel is permanent!");

            ButtonType confirmDelete = new ButtonType("Delete anyway", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(confirmDelete, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == confirmDelete) {

                    Controller.deleteApparel(chosenApparel.getId(), chosenApparel.getNickname());
                    ProfileMenuController.apparelViewPopup.close();
                }
            }

        });

        // ImageView Drag Drop Functions
        apparelImageView.setOnDragOver(event -> {

            if (event.getGestureSource() != apparelImageView && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
                apparelImageView.setEffect(new InnerShadow());
            }

        });

        apparelImageView.setOnDragExited(dragEvent -> apparelImageView.setEffect(null));

        apparelImageView.setOnDragDropped(event -> {

            Dragboard db = event.getDragboard();
            File tempChosenFile;
            if (db.hasFiles()) {

                tempChosenFile = db.getFiles().get(0);
                String fileType = null;

                try {
                    fileType = Files.probeContentType(Paths.get(tempChosenFile.getAbsolutePath()));
                } catch (IOException ignored) {}

                if (fileType != null){
                    if (fileType.equals("image/jpeg") || fileType.equals("image/png")) {
                        newImage = tempChosenFile;
                        apparelImageView.setImage(new Image("file:" + newImage.getPath()));
                    } else {
                        apparelImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/grey-background-2.png")));
                    }
                } else {
                    apparelImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/grey-background-2.png")));
                }

            }

        });

        // SAVE CHANGES BUTTON HANDLER
        saveChangesButton.setOnAction(actionEvent ->  {

                if (newNickname != null) {
                    if (!newNickname.equals(oldNickname)) {
                        Controller.editApparelNickname(editingApparel.getId(), oldNickname, newNickname);
                        chosenApparel.setNickname(newNickname);
                    }
                }

                if (newType != null) {

                    if (!newType.equals(oldType)) {

                        boolean dontRemove = false;
                        String typeConvert = null;
                        switch (oldType) {
                            case "Outer Top" -> typeConvert = "outer_top_apparel_id";
                            case "Inner Top" -> typeConvert = "inner_top_apparel_id";
                            case "Bottom" -> typeConvert = "bottom_apparel_id";
                            case "Footwear" -> typeConvert = "footwear_apparel_id";
                            case "Accessory" -> dontRemove = true;
                        }

                        if (!dontRemove) {
                            ArrayList<Outfit> outfitList = Controller.getAllOutfitByProfile(Controller.selectedProfile.getNickname());
                            for (Outfit o : outfitList) {
                                if (o.apparelExistsInOutfit(chosenApparel.getId())) {
                                    Controller.removeSlotFromOutfit(o.getId(), typeConvert);
                                }
                            }
                        }

                        Controller.editApparelBodyPart(editingApparel.getId(), newType);
                        chosenApparel.setBodyPart(newType);

                    }

                }

                if (imageChanged) {
                    if (newNickname == null) {
                        Controller.editApparelImage(editingApparel.getId(), oldNickname, newImage);
                    } else {
                        Controller.editApparelImage(editingApparel.getId(), newNickname, newImage);
                    }
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/ApparelViewerScreen.fxml"));
                try {
                    Parent root = loader.load();
                    ProfileMenuController.apparelViewPopup.setScene(new Scene(root));
                    ProfileMenuController.apparelViewPopup.setTitle("Digital Closet - Viewing "
                            + chosenApparel.getNickname());
                } catch (IOException ignored) {
                }


        });

        // CANCEL BUTTON HANDLER
        cancelButton.setOnAction(actionEvent ->  {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/ApparelViewerScreen.fxml"));
            try {
                Parent root = loader.load();
                ProfileMenuController.apparelViewPopup.setScene(new Scene(root));
                ProfileMenuController.apparelViewPopup.setTitle("Digital Closet - Viewing "
                        + chosenApparel.getNickname());
            } catch (IOException ignored) {}

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
