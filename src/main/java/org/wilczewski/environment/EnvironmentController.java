package org.wilczewski.environment;

import interfaces.IRiverSection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class EnvironmentController {
    @FXML
    public VBox riversVbox;

    private EnvironmentService environmentService;

    public void setEnvironmentService(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @FXML
    public void startWorking(ActionEvent actionEvent) throws IOException, InterruptedException {
        environmentService.run();
    }

    public void showRivers(ConcurrentHashMap<String, IRiverSection> riverSectionsMap) {
        riversVbox.getChildren().clear();
        riversVbox.setSpacing(10);
        for (var entry : riverSectionsMap.entrySet()) {
            String riverName = entry.getKey();

            Label nameLabel = new Label("Name: " + riverName);

            TextField rainfallTxtField = new TextField();
            rainfallTxtField.setPromptText("Generate rainfall");

            Button setButton = new Button("Set");
            setButton.setOnAction(event -> {
                try {
                    int rainfall = Integer.parseInt(rainfallTxtField.getText());
                    environmentService.setRainfall(riverName, rainfall);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            HBox hbox = new HBox(10, nameLabel, rainfallTxtField, setButton);
            hbox.setAlignment(Pos.CENTER_RIGHT);

            riversVbox.getChildren().add(hbox);
        }
    }
}
