module com.example.demo {
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires de.jensd.fx.glyphs.fontawesome;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.controller;
    opens com.example.demo.controller to javafx.fxml;
    exports com.example.demo.model;
    opens com.example.demo.model to javafx.fxml;
}