package org.wilczewski.controlcenter;

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

public class ControlCenterController {
    @FXML
    public VBox basinsVbox;
    @FXML
    public Button startWorkingButton;

    private ControlCenterService controlCenterService;

    public void setControlCenterService(ControlCenterService controlCenterService) {
        this.controlCenterService = controlCenterService;
    }

    @FXML
    public void startWorking(ActionEvent actionEvent) throws IOException, InterruptedException {
        controlCenterService.run();
        startWorkingButton.setDisable(true);
    }

    public void showBasins(ConcurrentHashMap<String, RetentionBasinMapItem> retentionBasinsMap) {
        basinsVbox.getChildren().clear();
        basinsVbox.setSpacing(10);
        for (var entry : retentionBasinsMap.entrySet()) {
            String name = entry.getKey();
            double basinFillingPercentage = entry.getValue().getFillingPercentage();
            int basinWaterDischarge = entry.getValue().getWaterDischargeValve();

            Label nameLabel = new Label("Name: " + name);
            Label fillingPercentageLabel = new Label("Filling: " + String.format("%.2f", basinFillingPercentage*100) + "%");
            Label waterDischargeLabel = new Label("Water Discharge: " + basinWaterDischarge);

            TextField waterDischargeTextField = new TextField();
            waterDischargeTextField.setPromptText("New Water Discharge");

            Button setButton = new Button("Set");
            setButton.setOnAction(event -> {
                try {
                    int discharge = Integer.parseInt(waterDischargeTextField.getText());
                    controlCenterService.setRetentionBasinWaterDischarge(name, discharge);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            HBox hbox = new HBox(10, nameLabel, fillingPercentageLabel, waterDischargeLabel, waterDischargeTextField, setButton);
            hbox.setAlignment(Pos.CENTER_RIGHT);

            basinsVbox.getChildren().add(hbox);
        }
    }
}
