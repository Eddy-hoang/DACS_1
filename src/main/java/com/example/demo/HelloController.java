package com.example.demo;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import com.example.demo.model.Session;
import javafx.application.Platform;
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

        // Kết nối DB
        try (Connection connect = database.connectDB();
             PreparedStatement prepare = connect.prepareStatement(sql)) {

            String usernameInput = username.getText();
            String passwordInput = password.getText();

            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                showAlert(AlertType.ERROR, "Error Message", null, "Please fill all blank fields");
                return;
            }

            String hashedPassword = HashUtils.hashSHA256(passwordInput);

            prepare.setString(1, usernameInput);
            prepare.setString(2, hashedPassword);

            try (ResultSet result = prepare.executeQuery()) {
                if (result.next()) {
                    // Lưu session
                    Session.currentUser = usernameInput;
                    Session.currentRoler = result.getString("role");

                    showAlert(AlertType.INFORMATION, "Information Message", null,
                            "Successfully Login as " + Session.currentRoler);

                    // Đóng cửa sổ login hiện tại
                    Stage loginStage = (Stage) loginBtn.getScene().getWindow();
                    loginStage.close();

                    // Mở cửa sổ Dashboard mới trên JavaFX Application Thread
                    Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                            Parent root = loader.load();

                            // Truyền thông tin session nếu cần
                            dashboardController controller = loader.getController();
//                            controller.setUserSession(Session.currentUser, Session.currentRoler);

                            Stage stage = new Stage();
                            stage.initStyle(StageStyle.TRANSPARENT);
                            stage.setScene(new Scene(root));

                            // Xử lý di chuyển cửa sổ không viền
                            root.setOnMousePressed((MouseEvent event) -> {
                                x = event.getSceneX();
                                y = event.getSceneY();
                            });

                            root.setOnMouseDragged((MouseEvent event) -> {
                                stage.setX(event.getScreenX() - x);
                                stage.setY(event.getScreenY() - y);
                            });

                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            showAlert(AlertType.ERROR, "Error", null, "Cannot open dashboard window.");
                        }
                    });

                } else {
                    showAlert(AlertType.ERROR, "Error Message", null, "Wrong Username/Password");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error Message", null, "Database error: " + e.getMessage());
        }
    }

    // Hàm tiện ích hiển thị alert
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void close(){
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}