package com.example.demo;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import com.example.demo.model.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

    //    DATABASE TOOLS
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private double x = 0;
    private double y = 0;
    public void loginAdmin(){

        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";

        connect = database.connectDB();

        try {
            String usernameInput = username.getText();
            String passwordInput = password.getText();
            String hashedPassword = HashUtils.hashSHA256(passwordInput);

            Alert alert;

            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
                return;
            }

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, usernameInput);
            prepare.setString(2, hashedPassword);
            result = prepare.executeQuery();

            if (result.next()) {
                // ✅ Gán thông tin người dùng đăng nhập
                Session.currentUser = usernameInput;
                Session.currentRoler = result.getString("role"); // Lấy quyền từ DB

                alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Login as " + Session.currentRoler);
                alert.showAndWait();

                loginBtn.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                root.setOnMousePressed((MouseEvent event) -> {
                    x = event.getSceneX();
                    y = event.getSceneY();
                });

                root.setOnMouseDragged((MouseEvent event) -> {
                    stage.setX(event.getScreenX() - x);
                    stage.setY(event.getScreenY() - y);
                });

                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();

            } else {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Wrong Username/Password");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void close(){
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}