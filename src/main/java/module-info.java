module lab07_pop {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;
    requires floodlib;
    requires java.rmi;

    opens org.wilczewski.retentionbasin to javafx.fxml;
    opens org.wilczewski.riversection to javafx.fxml;
    opens org.wilczewski.controlcenter to javafx.fxml;
    opens org.wilczewski.environment to javafx.fxml;
    opens org.wilczewski.tailor to javafx.fxml;

    exports org.wilczewski.controlcenter to javafx.fxml, javafx.graphics;
    exports org.wilczewski.environment to javafx.fxml, javafx.graphics;
    exports org.wilczewski.retentionbasin to javafx.fxml, javafx.graphics;
    exports org.wilczewski.riversection to javafx.fxml, javafx.graphics;
    exports org.wilczewski.tailor to javafx.fxml, javafx.graphics;
    exports org.wilczewski.myrmiinterface to java.rmi;
}