package org.wilczewski.riversection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.rmi.NotBoundException;

public class RiverSectionController {
    @FXML
    public Button configurationButton;
    @FXML
    public TextField riverDelayTxtField;
    @FXML
    public Rectangle activeRiverSign;
    @FXML
    public Label rainfallTxtLabel;
    @FXML
    public Label inflowWaterTxtLabel;
    @FXML
    public Button startWorkingButton;


    RiverSectionService riverSectionService;

    public void setRiverSectionService(RiverSectionService riverSectionService) {
        this.riverSectionService = riverSectionService;
    }

    @FXML
    private void setNetworkConfig(ActionEvent actionEvent) throws IOException {
        try {
            String delayStr = riverDelayTxtField.getText();
            if(delayStr.isEmpty()) throw new IllegalArgumentException("Max volume cannot be empty");
            int riverDelay = Integer.parseInt(delayStr);
            if(riverDelay < 0) throw new IllegalArgumentException("Max volume cannot be negative");

            configurationButton.setDisable(true);
            this.riverSectionService.configuration(riverDelay);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void startWorking(ActionEvent actionEvent) throws IOException, InterruptedException {
        riverSectionService.run();
        startWorkingButton.setDisable(true);
    }

    public void showRainfall(int rainfall) {
        rainfallTxtLabel.setText(String.valueOf(rainfall));
    }

    public void showInflowWater(int inflowWater) {
        inflowWaterTxtLabel.setText(String.valueOf(inflowWater));
    }

    public void showActiveRiverSign() {
        activeRiverSign.setVisible(true);
    }
}
