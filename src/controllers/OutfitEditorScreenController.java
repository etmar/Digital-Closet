package controllers;

import model.Apparel;
import model.Controller;
import model.Outfit;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import static controllers.OutfitViewerScreenController.chosenOutfit;

public class OutfitEditorScreenController implements Initializable {

    public Outfit editingOutfit = chosenOutfit;

    public Button innerTopButton, outerTopButton, bottomButton, footwearButton, accessoryOneButton, accessoryTwoButton, accessoryThreeButton, accessoryFourButton, accessoryFiveButton;
    public ImageView innerTopImageView, outerTopImageView, bottomImageView, footwearImageView, accessoryOneImageView, accessoryTwoImageView, accessoryThreeImageView, accessoryFourImageView, accessoryFiveImageView;
    public Button innerTopRemover, outerTopRemover, bottomRemover, footwearRemover, accessoryOneRemover, accessoryTwoRemover, accessoryThreeRemover, accessoryFourRemover, accessoryFiveRemover;

    public Button saveButton, cancelButton, deleteButton;
    public Label selectedSlotLabel;
    public TextField outfitNameTextField;
    public ScrollPane apparelSelectorScrollPane;
    public TilePane apparelSelectorTilePane, outfitSlotsDisplayTilePane;

    public String selectedSlot =  null;

    public int[] newApparelIDList = new int[9];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Loads the existing List of associated Apparel IDs to be using during saving changes later
        for (int i = 0; i < editingOutfit.getApparelIDList().size(); i++) {
            newApparelIDList[i] = editingOutfit.getApparelIDList().get(i);
        }

        outfitNameTextField.setText(editingOutfit.getNickname());

        assignSlotSelectHandlers();
        assignSlotImages();

        // Cancel Button Handler
        cancelButton.setOnAction(actionEvent ->  {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../screens/OutfitViewerScreen.fxml"));
            try {
                Parent root = loader.load();
                ProfileMenuController.outfitViewPopup.setScene(new Scene(root));
                ProfileMenuController.outfitViewPopup.setTitle("Digital Closet - Viewing "
                        + chosenOutfit.getNickname());
            } catch (IOException e) {e.printStackTrace();}
        });

        // Save Button Handler
        saveButton.setOnAction(actionEvent ->  {

            if (newApparelIDList[0] == 0) {
                Controller.removeSlotFromOutfit(editingOutfit.getId(), "outer_top_apparel_id");
            } else {
                Controller.editOutfitSlot(editingOutfit.getId(), "outer_top_apparel_id", newApparelIDList[0]);
            }

            if (newApparelIDList[1] == 0) {
                Controller.removeSlotFromOutfit(editingOutfit.getId(), "inner_top_apparel_id");
            } else {
                Controller.editOutfitSlot(editingOutfit.getId(), "inner_top_apparel_id", newApparelIDList[1]);
            }

            if (newApparelIDList[2] == 0) {
                Controller.removeSlotFromOutfit(editingOutfit.getId(), "bottom_apparel_id");
            } else {
                Controller.editOutfitSlot(editingOutfit.getId(), "bottom_apparel_id", newApparelIDList[2]);
            }

            if (newApparelIDList[3] == 0) {
                Controller.removeSlotFromOutfit(editingOutfit.getId(), "footwear_apparel_id");
            } else {
                Controller.editOutfitSlot(editingOutfit.getId(), "footwear_apparel_id", newApparelIDList[3]);
            }

            if (newApparelIDList[4] == 0) {
                Controller.removeSlotFromOutfit(editingOutfit.getId(), "accessory_apparel_id_1");
            } else {
                Controller.editOutfitSlot(editingOutfit.getId(), "accessory_apparel_id_1", newApparelIDList[4]);
            }

            if (newApparelIDList[5] == 0) {
                Controller.removeSlotFromOutfit(editingOutfit.getId(), "accessory_apparel_id_2");
            } else {
                Controller.editOutfitSlot(editingOutfit.getId(), "accessory_apparel_id_2", newApparelIDList[5]);
            }

            if (newApparelIDList[6] == 0) {
                Controller.removeSlotFromOutfit(editingOutfit.getId(), "accessory_apparel_id_3");
            } else {
                Controller.editOutfitSlot(editingOutfit.getId(), "accessory_apparel_id_3", newApparelIDList[6]);
            }

            if (newApparelIDList[7] == 0) {
                Controller.removeSlotFromOutfit(editingOutfit.getId(), "accessory_apparel_id_4");
            } else {
                Controller.editOutfitSlot(editingOutfit.getId(), "accessory_apparel_id_4", newApparelIDList[7]);
            }

            if (newApparelIDList[8] == 0) {
                Controller.removeSlotFromOutfit(editingOutfit.getId(), "accessory_apparel_id_5");
            } else {
                Controller.editOutfitSlot(editingOutfit.getId(), "accessory_apparel_id_5", newApparelIDList[8]);
            }

            chosenOutfit.setNickname(outfitNameTextField.getText());
            chosenOutfit.setApparelIDList(newApparelIDList);

            Controller.editOutfitNickname(editingOutfit.getId(), outfitNameTextField.getText());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/OutfitViewerScreen.fxml"));
            try {
                Parent root = loader.load();
                ProfileMenuController.outfitViewPopup.setScene(new Scene(root));
                ProfileMenuController.outfitViewPopup.setTitle("Digital Closet - Viewing "
                        + chosenOutfit.getNickname());
            } catch (IOException e) {e.printStackTrace();}

        });

        // Delete Button Handler
        deleteButton.setOnAction(actionEvent -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Digital Closet - Delete " + chosenOutfit.getNickname() + "?");
            alert.setHeaderText("Warning!");
            alert.setContentText("Deleting an Outfit is permanent!");

            ButtonType confirmDelete = new ButtonType("Delete anyway", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(confirmDelete, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == confirmDelete) {

                    Controller.deleteOutfit(chosenOutfit.getId());
                    ProfileMenuController.outfitViewPopup.close();
                }
            }

        });

    }



    public void assignSlotSelectHandlers() {

        // Handler for selecting an Outfit Slots
        EventHandler<ActionEvent> selectSlot = actionEvent -> {
            Button target = (Button) actionEvent.getTarget();
            selectedSlot = target.getId();

            changeSelectedSlot();
        };

        // Handler for emptying Outfit Slots
        EventHandler<ActionEvent> removeSlot = actionEvent -> {

            Button target = (Button) actionEvent.getTarget();

            switch (target.getId()) {

                case "outerTopRemover" -> {
                    outerTopImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/plus-icon.jpg")));
                    newApparelIDList[0] = 0;
                }
                case "innerTopRemover" -> {
                    innerTopImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/plus-icon.jpg")));
                    newApparelIDList[1] = 0;
                }
                case "bottomRemover" -> {
                    bottomImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/plus-icon.jpg")));
                    newApparelIDList[2] = 0;
                }
                case "footwearRemover" -> {
                    footwearImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/plus-icon.jpg")));
                    newApparelIDList[3] = 0;
                }
                case "accessoryOneRemover" -> {
                    accessoryOneImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/plus-icon.jpg")));
                    newApparelIDList[4] = 0;
                }
                case "accessoryTwoRemover" -> {
                    accessoryTwoImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/plus-icon.jpg")));
                    newApparelIDList[5] = 0;
                }
                case "accessoryThreeRemover" -> {
                    accessoryThreeImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/plus-icon.jpg")));
                    newApparelIDList[6] = 0;
                }
                case "accessoryFourRemover" -> {
                    accessoryFourImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/plus-icon.jpg")));
                    newApparelIDList[7] = 0;
                }
                case "accessoryFiveRemover" -> {
                    accessoryFiveImageView.setImage(new Image(this.getClass().getResourceAsStream("/resources/plus-icon.jpg")));
                    newApparelIDList[8] = 0;
                }

            }

        };

        // Assigns correct Handler to each Selector and Remover
        for (int i = 0; i < outfitSlotsDisplayTilePane.getChildren().size(); i++) {
            AnchorPane ap = (AnchorPane) outfitSlotsDisplayTilePane.getChildren().get(i);
            for (int j = 0; j < ap.getChildren().size(); j++) {

                Button target = (Button) ap.getChildren().get(j);

                if (target.getId().endsWith("Button")) {
                    target.setOnAction(selectSlot);
                } else {
                    target.setOnAction(removeSlot);
                }

            }
        }

    }

    public void assignSlotImages() {

        // Assigns an image to each Outfit slot selector for each Apparel already in the Outfit
        HashMap<Integer, Apparel> apparelList = Controller.getAllApparelByProfile(Controller.selectedProfile.getNickname());
        for (int i = 0; i < editingOutfit.getApparelIDList().size(); i++) {

            if (editingOutfit.getApparelIDList().get(i) != 0) {

                int x = editingOutfit.getApparelIDList().get(i);
                apparelList.get(x);

                switch (i) {
                    case 0 -> outerTopImageView.setImage(getApparelImage(x, apparelList.get(x).getNickname()));
                    case 1 -> innerTopImageView.setImage(getApparelImage(x, apparelList.get(x).getNickname()));
                    case 2 -> bottomImageView.setImage(getApparelImage(x, apparelList.get(x).getNickname()));
                    case 3 -> footwearImageView.setImage(getApparelImage(x, apparelList.get(x).getNickname()));
                    case 4 -> accessoryOneImageView.setImage(getApparelImage(x, apparelList.get(x).getNickname()));
                    case 5 -> accessoryTwoImageView.setImage(getApparelImage(x, apparelList.get(x).getNickname()));
                    case 6 -> accessoryThreeImageView.setImage(getApparelImage(x, apparelList.get(x).getNickname()));
                    case 7 -> accessoryFourImageView.setImage(getApparelImage(x, apparelList.get(x).getNickname()));
                    case 8 -> accessoryFiveImageView.setImage(getApparelImage(x, apparelList.get(x).getNickname()));
                }

            }

        }

    }

    public void changeSelectedSlot() {

        // Assigns the correct slot based on which Selector Button was clicked
        String slotConvert = null;
        switch(selectedSlot) {
            case "outerTopButton" -> {
                selectedSlotLabel.setText("Outer Top");
                slotConvert = "Outer Top";
            }
            case "innerTopButton" -> {
                selectedSlotLabel.setText("Inner Top");
                slotConvert = "Inner Top";
            }
            case "bottomButton" -> {
                selectedSlotLabel.setText("Bottom");
                slotConvert = "Bottom";
            }
            case "footwearButton" -> {
                selectedSlotLabel.setText("Footwear");
                slotConvert = "Footwear";
            }
            case "accessoryOneButton" -> {
                selectedSlotLabel.setText("Accessory One");
                slotConvert = "Accessory";
            }
            case "accessoryTwoButton" -> {
                selectedSlotLabel.setText("Accessory Two");
                slotConvert = "Accessory";
            }
            case "accessoryThreeButton" -> {
                selectedSlotLabel.setText("Accessory Three");
                slotConvert = "Accessory";
            }
            case "accessoryFourButton" -> {
                selectedSlotLabel.setText("Accessory Four");
                slotConvert = "Accessory";
            }
            case "accessoryFiveButton" -> {
                selectedSlotLabel.setText("Accessory Five");
                slotConvert = "Accessory";
            }

        }

        apparelSelectorTilePane.getChildren().clear();

        // Loads a list of Apparel matching the Type of the Slot Selector clicked
        HashMap<Integer, Apparel> apparelList = Controller.getAllApparelByProfile(Controller.selectedProfile.getNickname());

        for (Apparel a : apparelList.values()) {
            if (a.getBodyPart().equals(slotConvert)) {

                Button apparelSelectorButton = new Button(a.getNickname());
                ImageView apparelImageView = new ImageView();
                Image apparelImage = getApparelImage(a.getId(), a.getNickname());

                // Button Styling
                apparelImageView.setImage(apparelImage);
                apparelImageView.setFitHeight(75);
                apparelImageView.setFitWidth(75);
                apparelImageView.setPreserveRatio(false);
                apparelSelectorButton.setGraphic(apparelImageView);
                apparelSelectorButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                apparelSelectorTilePane.getChildren().add(apparelSelectorButton);

                apparelSelectorButton.setOnAction(actionEvent -> {

                    switch(selectedSlot) {
                        case "outerTopButton" -> {
                            outerTopImageView.setImage(getApparelImage(a.getId(), a.getNickname()));
                            newApparelIDList[0] = a.getId();
                        }
                        case "innerTopButton" -> {
                            innerTopImageView.setImage(getApparelImage(a.getId(), a.getNickname()));
                            newApparelIDList[1] = a.getId();
                        }
                        case "bottomButton" -> {
                            bottomImageView.setImage(getApparelImage(a.getId(), a.getNickname()));
                            newApparelIDList[2] = a.getId();
                        }
                        case "footwearButton" -> {
                            footwearImageView.setImage(getApparelImage(a.getId(), a.getNickname()));
                            newApparelIDList[3] = a.getId();
                        }
                        case "accessoryOneButton" -> {
                            accessoryOneImageView.setImage(getApparelImage(a.getId(), a.getNickname()));
                            newApparelIDList[4] = a.getId();
                        }
                        case "accessoryTwoButton" -> {
                            accessoryTwoImageView.setImage(getApparelImage(a.getId(), a.getNickname()));
                            newApparelIDList[5] = a.getId();
                        }
                        case "accessoryThreeButton" -> {
                            accessoryThreeImageView.setImage(getApparelImage(a.getId(), a.getNickname()));
                            newApparelIDList[6] = a.getId();
                        }
                        case "accessoryFourButton" -> {
                            accessoryFourImageView.setImage(getApparelImage(a.getId(), a.getNickname()));
                            newApparelIDList[7] = a.getId();
                        }
                        case "accessoryFiveButton" -> {
                            accessoryFiveImageView.setImage(getApparelImage(a.getId(), a.getNickname()));
                            newApparelIDList[8] = a.getId();
                        }
                        default -> {

                        }
                    }

                });

            }
        }

        selectedSlotLabel.setVisible(true);
        apparelSelectorScrollPane.setVisible(true);

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
