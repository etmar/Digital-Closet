package run;

import controllers.HomeScreenController;
import controllers.ProfileMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {



        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/HomeScreen.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Digital Closet - Home");
        Scene primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("file:src/resources/dc-logo.png"));
        primaryStage.show();

        ProfileMenuController.addApparelPopup.initModality(Modality.APPLICATION_MODAL);
        ProfileMenuController.addApparelPopup.setResizable(false);
        ProfileMenuController.addApparelPopup.getIcons().add(new Image("file:src/resources/dc-logo.png"));

        ProfileMenuController.apparelViewPopup.initModality(Modality.APPLICATION_MODAL);
        ProfileMenuController.apparelViewPopup.setResizable(false);
        ProfileMenuController.apparelViewPopup.getIcons().add(new Image("file:src/resources/dc-logo.png"));

        ProfileMenuController.outfitViewPopup.initModality(Modality.APPLICATION_MODAL);
        ProfileMenuController.outfitViewPopup.setResizable(false);
        ProfileMenuController.outfitViewPopup.getIcons().add(new Image("file:src/resources/dc-logo.png"));

        HomeScreenController.aboutPopup.initModality(Modality.APPLICATION_MODAL);
        HomeScreenController.aboutPopup.setResizable(false);
        HomeScreenController.aboutPopup.getIcons().add(new Image("file:src/resources/dc-logo.png"));

    }

    public static void main(String[] args) {
        launch(args);
    }

}