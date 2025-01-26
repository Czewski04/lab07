package org.wilczewski.controlcenter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControlCenterApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ControlCenterAppView.fxml"));
        Parent root = loader.load();

        ControlCenterController controller = loader.getController();
        ControlCenterService controlCenterService = new ControlCenterService(controller);
        controller.setControlCenterService(controlCenterService);

        stage.setTitle("Control Center");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
