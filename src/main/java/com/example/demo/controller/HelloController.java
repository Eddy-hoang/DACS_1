package com.example.demo.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import com.example.demo.model.HashUtils;
import com.example.demo.model.database;
import com.example.demo.model.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HelloController implements Initializable {

    @FXML
    private AnchorPane main_form;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginBtn;

    @FXML
    private Button close;

    private double x = 0;
    private double y = 0;

    public void loginAdmin() {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try (Connection connect = database.connectDB();
             PreparedStatement prepare = connect.prepareStatement(sql)) {

            String usernameInput = username.getText();
            String passwordInput = password.getText();

            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
                return;
            }

            String hashedPassword = HashUtils.hashSHA256(passwordInput);

            prepare.setString(1, usernameInput);
            prepare.setString(2, hashedPassword);

            try (ResultSet result = prepare.executeQuery()) {
                if (result.next()) {
                    Session.currentUser = usernameInput;
                    Session.currentRoler = result.getString("role");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Login as " + Session.currentRoler);
                    alert.showAndWait();

                    Stage loginStage = (Stage) loginBtn.getScene().getWindow();
                    loginStage.close();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/dashboard.fxml"));
                    Parent root = loader.load();


                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.setScene(new Scene(root));

                    root.setOnMousePressed((MouseEvent event) -> {
                        x = event.getSceneX();
                        y = event.getSceneY();
                    });

                    root.setOnMouseDragged((MouseEvent event) -> {
                        stage.setX(event.getScreenX() - x);
                        stage.setY(event.getScreenY() - y);
                    });

                    stage.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid username or password");
                    alert.showAndWait();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void close() {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
