package org.wilczewski.retentionbasin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.rmi.NotBoundException;

public class RetentionBasinController {
    @FXML
    public Label valueTxtLabel;
    @FXML
    public Button configurationButton;
    @FXML
    public TextField maxVolumeTxtField;
    @FXML
    public ProgressBar volumeProgressBar;
    @FXML
    public Label outflowTxtLabel;
    @FXML
    public Label inflowTxtLabel;
    @FXML
    public Label fillingPercentageTxtLabel;

    RetentionBasinService retentionBasinService;

    public void setRetentionBasinService(RetentionBasinService retentionBasinService) {
        this.retentionBasinService = retentionBasinService;
    }

    @FXML
    private void setNetworkConfig(ActionEvent actionEvent) throws IOException {
        try {
            String maxVolumeStr = maxVolumeTxtField.getText();
            if(maxVolumeStr.isEmpty()) throw new IllegalArgumentException("Max volume cannot be empty");
            int maxVolume = Integer.parseInt(maxVolumeStr);
            if(maxVolume < 0) throw new IllegalArgumentException("Max volume cannot be negative");

            configurationButton.setDisable(true);
            this.retentionBasinService.configuration(maxVolume);
            showMaxVolume(maxVolume);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InterruptedException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void showMaxVolume(int maxVolume) {
        valueTxtLabel.setText(String.valueOf(maxVolume));
    }

    @FXML
    private void startWorking(ActionEvent actionEvent) throws IOException, InterruptedException {
        retentionBasinService.run();
    }

    public void updateVolume(double fillPercentage) {
        volumeProgressBar.setProgress(fillPercentage);
        fillPercentage *= 100;
        fillingPercentageTxtLabel.setText(String.valueOf(fillPercentage)+"%");
    }

    public void updateFlow(int inflow, int outflow) {
        outflowTxtLabel.setText(String.valueOf(outflow));
        inflowTxtLabel.setText(String.valueOf(inflow));
    }
}