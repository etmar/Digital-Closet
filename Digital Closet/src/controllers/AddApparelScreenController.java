package controllers;

import model.Controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddApparelScreenController implements Initializable {

    public ChoiceBox<String> clothingTypeChoiceBox;
    public TextField clothingNicknameTextField;
    public Button cancelButton, addApparelConfirmationButton, selectFileButton;
    public ImageView fileDragDropImageView;

    File chosenImage = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Adds the choices for the Choice Box
        clothingTypeChoiceBox.getItems().addAll("Outer Top", "Inner Top", "Bottom", "Footwear", "Accessory");

        // ImageView Drag Drop Handlers
        fileDragDropImageView.setOnDragOver(event -> {
            if (event.getGestureSource() != fileDragDropImageView && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
                fileDragDropImageView.setEffect(new InnerShadow());
            }
        });

        fileDragDropImageView.setOnDragExited(dragEvent -> fileDragDropImageView.setEffect(null));

        fileDragDropImageView.setOnDragDropped(event -> {

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
                        chosenImage = tempChosenFile;
                        fileDragDropImageView.setImage(new Image("file:" + chosenImage.getPath()));
                    } else {
                        fileDragDropImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/grey-background-2.png")));
                    }
                } else {
                    fileDragDropImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/grey-background-2.png")));
                }

            }

        });

        // "Select Files" Button Handler
        selectFileButton.setOnAction(actionEvent -> {

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
                        chosenImage = tempChosenFile;
                        fileDragDropImageView.setImage(new Image("file:" + chosenImage.getPath()));                    } else {
                        fileDragDropImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/grey-background-2.png")));
                    }
                } else {
                    fileDragDropImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/grey-background-2.png")));
                }


            }

        });

        // "Add" Button Handler
        addApparelConfirmationButton.setOnAction(actionEvent -> {
            if ((chosenImage != null) && (clothingTypeChoiceBox.getValue() != null) && (!clothingNicknameTextField.getText().equals(""))) {
                Controller.addApparel(Controller.selectedProfile.getNickname(), clothingNicknameTextField.getText(), clothingTypeChoiceBox.getValue(), chosenImage);
                ProfileMenuController.addApparelPopup.close();
            }
        });

        // "Cancel" Button Handler
        cancelButton.setOnAction(actionEvent -> ProfileMenuController.addApparelPopup.close());

        // Listener checks for adding Apparel
        clothingTypeChoiceBox.valueProperty().addListener((observableValue, s, t1) -> addApparelConfirmationButton.setDisable(clothingTypeChoiceBox.getValue() == null || clothingNicknameTextField.getText().equals("") || chosenImage == null));

        clothingNicknameTextField.textProperty().addListener((observableValue, s, t1) -> addApparelConfirmationButton.setDisable(clothingNicknameTextField.getText().equals("") || clothingTypeChoiceBox.getValue() == null || chosenImage == null));

        fileDragDropImageView.imageProperty().addListener((observableValue, image, t1) -> {

            Image resource1 = new Image(this.getClass().getResourceAsStream("/resources/grey-background.png"));
            Image resource2 = new Image(this.getClass().getResourceAsStream("/resources/grey-background-2.png"));
            boolean imageChanged;
            String currImage = fileDragDropImageView.getImage().getUrl();

            imageChanged = !currImage.equals(resource1.getUrl()) && !currImage.equals(resource2.getUrl());

            addApparelConfirmationButton.setDisable(clothingNicknameTextField.getText().equals("") || clothingTypeChoiceBox.getValue() == null || !imageChanged);

        });

    }

}
