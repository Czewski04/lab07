package org.wilczewski.tailor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.rmi.RemoteException;

public class TailorController {
    @FXML
    public Button connectButton;
    TailorApp tailorApp;

    public void setTailorApp (TailorApp tailorApp) {
        this.tailorApp = tailorApp;
    }

    public void connect(ActionEvent actionEvent) throws RemoteException {
        tailorApp.connect();
        connectButton.setDisable(true);
    }
}
