package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutScreenController implements Initializable {

    public Hyperlink gitHubLink, linkedInLink;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Link for GitHub
        gitHubLink.setOnAction(actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://github.com/etmar").toURI());
            } catch (IOException | URISyntaxException ignored) {
            }
        });

        // Link for LinkedIn
        linkedInLink.setOnAction(actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://www.linkedin.com/in/marc-olivier-etienne/").toURI());
            } catch (IOException | URISyntaxException ignored) {}
        });
    }

}