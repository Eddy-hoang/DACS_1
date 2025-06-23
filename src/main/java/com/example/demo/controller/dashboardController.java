package com.example.demo.controller;

import com.example.demo.model.database;
import com.example.demo.model.employeeData;
import com.example.demo.model.getData;
import com.example.demo.model.Session;
import com.example.demo.network.EmployeeClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class dashboardController implements Initializable {

    @FXML
    private Button addEmployee_addBtn;

    @FXML
    private Button addEmployee_btn;

    @FXML
    private Button addEmployee_clearBtn;

    @FXML
    private TableColumn<employeeData, String> addEmployee_col_date;

    @FXML
    private TableColumn<employeeData, String> addEmployee_col_position;

    @FXML
    private TableColumn<employeeData, String> addEmployee_col_employeeID;

    @FXML
    private TableColumn<employeeData, String> addEmployee_col_firstName;

    @FXML
    private TableColumn<employeeData, String> addEmployee_col_gender;

    @FXML
    private TableColumn<employeeData, String> addEmployee_col_lastName;

    @FXML
    private TableColumn<employeeData, String> addEmployee_col_phoneNum;

    @FXML
    private Button addEmployee_deleteBtn;

    @FXML
    private TextField addEmployee_employeeID;

    @FXML
    private TextField addEmployee_firstName;

    @FXML
    private AnchorPane addEmployee_form;

    @FXML
    private ComboBox<String> addEmployee_gender;

    @FXML
    private ImageView addEmployee_image;

    @FXML
    private Button addEmployee_importBtn;

    @FXML
    private TextField addEmployee_lastName;

    @FXML
    private TextField addEmployee_phoneNum;

    @FXML
    private ComboBox<String> addEmployee_position;

    @FXML
    private TextField addEmployee_search;

    @FXML
    private TableView<employeeData> addEmployee_tableView;

    @FXML
    private Button addEmployee_updateBtn;

    @FXML
    private Button close;

    @FXML
    private Button home_btn;

    @FXML
    private BarChart<String, Number> home_chart;

    @FXML
    private AnchorPane home_form;

    @FXML
    private Label home_totalEmployees;

    @FXML
    private Label home_totalInactiveEm;

    @FXML
    private Label home_totalPresents;

    @FXML
    private Button logout;

    @FXML
    private Button minimize;

    @FXML
    private Button salary_ClearBtn;

    @FXML
    private Button salary_btn;

    @FXML
    private TableColumn<employeeData, String> salary_col_employeeID;

    @FXML
    private TableColumn<employeeData, String> salary_col_firstName;

    @FXML
    private TableColumn<employeeData, String> salary_col_lastName;

    @FXML
    private TableColumn<employeeData, String> salary_col_position;

    @FXML
    private TableColumn<employeeData, String> salary_col_salary;

    @FXML
    private TextField salary_employeeID;

    @FXML
    private Label salary_firstName;

    @FXML
    private AnchorPane salary_form;

    @FXML
    private Label salary_lastName;

    @FXML
    private Label salary_position;

    @FXML
    private TextField salary_salary;

    @FXML
    private TableView<employeeData> salary_tableView;

    @FXML
    private Button salary_updateBtn;

    @FXML
    private Label username;

    @FXML
    private AnchorPane main_form;

    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;

    private Image image;
    private EmployeeClient client;

    public void homeChart() {
        home_chart.getData().clear();
        try{
            String response = client.sendRequest("GET_CHART_DATA:");

            XYChart.Series<String,Number> chart = new XYChart.Series<>();

            if(response == null || response.startsWith("ERROR") ){
                System.out.println("Lấy dữ liệu thất bại: "+response);
            }

            String [] entries = response.split(";");

            for(String entry : entries){
                if(entry.trim().isEmpty()) continue;
                String [] parts = entry.split(",");
                if(parts.length == 2){
                    String date = parts[0];
                    int count = Integer.parseInt(parts[1]);
                    chart.getData().add(new XYChart.Data<>(date,count));
                }
            }

            home_chart.getData().add(chart);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void homeTotalEmployees() {
        try{
            String respons = client.sendRequest("GET_TOTAL_EMPLOYEES:");

            if(respons != null){
                String[] parts = respons.split(":");
                if(parts.length > 1) {
                    String totalEmployees = parts[1].trim();
                    home_totalEmployees.setText(totalEmployees);
                }else{
                    System.out.println("Server return invalid data: "+respons);
                }
            }else{
                System.out.println("Error: " + respons);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void homeEmployeeTotalPresent() {
        try{
            String response = client.sendRequest("GET_TOTAL_PRESENT:");
            if(response != null){
                String[] parts = response.split(":");
                if(parts.length > 1){
                    String totalPresent = parts[1].trim();
                    home_totalPresents.setText(totalPresent);
                }else{
                    System.out.println("Server return invalid data: "+response);
                }
            }else{
                System.out.println("Error: "+response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void homeTotalInactive() {
        try{
            String respons = client.sendRequest("GET_TOTAL_INACTIVE:");
            if(respons != null){
                String[] parts = respons.split(":");
                if(parts.length>1){
                    String totalInactive = parts[1].trim();
                    home_totalInactiveEm.setText(totalInactive);
                }else{
                    System.out.println("Server return invalid data: "+respons);
                }
            }else {
                System.out.println("ERROR: "+respons);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addEmployeeSearch() {
        if (addEmployeeList == null) {
            addEmployeeList = addEmployeeListData();
        }

        FilteredList<employeeData> filteredList = new FilteredList<>(addEmployeeList, e -> true);

        addEmployee_search.textProperty().addListener((observable, oldValue, newValue) -> {
            String lowerCaseFilter = (newValue == null) ? "" : newValue.toLowerCase().trim();

            filteredList.setPredicate(employee -> {
                if (lowerCaseFilter.isEmpty()) {
                    return true;
                }
                if (String.valueOf(employee.getEmployeeId()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (employee.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (employee.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (employee.getGender().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (employee.getPhoneNum().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (employee.getPosition().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (employee.getDate().toString().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });

            SortedList<employeeData> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(addEmployee_tableView.comparatorProperty());
            addEmployee_tableView.setItems(sortedList);
        });
    }

    public void addEmployeeAdd() {
        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        Alert alert;

        // Kiểm tra trường dữ liệu bắt buộc
        if (addEmployee_employeeID.getText().isEmpty()
                || addEmployee_firstName.getText().isEmpty()
                || addEmployee_lastName.getText().isEmpty()
                || addEmployee_gender.getSelectionModel().getSelectedItem() == null
                || addEmployee_phoneNum.getText().isEmpty()
                || addEmployee_position.getSelectionModel().getSelectedItem() == null
                || getData.path == null || getData.path.isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            return;
        }

        try {
            if (client == null) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText(null);
                alert.setContentText("Client is not connected to server.");
                alert.showAndWait();
                return;
            }

            // Xây dựng dữ liệu gửi cho server (theo định dạng bạn muốn)
            String uri = getData.path.replace("\\", "\\\\");
            String data = String.join(",",
                    addEmployee_employeeID.getText(),
                    addEmployee_firstName.getText(),
                    addEmployee_lastName.getText(),
                    addEmployee_gender.getSelectionModel().getSelectedItem(),
                    addEmployee_phoneNum.getText(),
                    addEmployee_position.getSelectionModel().getSelectedItem(),
                    uri,
                    sqlDate.toString()
            );

            String request = "ADD_EMPLOYEE:" + data;

            // Gửi yêu cầu và nhận phản hồi
            String response = client.sendRequest(request);

            if ("SUCCESS".equalsIgnoreCase(response)) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Added!");
                alert.showAndWait();

                addEmployeeShowListData();
                addEmployeeResset();

            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Failed to add employee: " + response);
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Network error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void addEmployeeUpdate() {
        String uri = getData.path;
        if (uri != null) {
            uri = uri.replace("\\", "\\\\");
        }

        if (addEmployee_employeeID.getText().isEmpty()
                || addEmployee_firstName.getText().isEmpty()
                || addEmployee_lastName.getText().isEmpty()
                || addEmployee_gender.getSelectionModel().getSelectedItem() == null
                || addEmployee_phoneNum.getText().isEmpty()
                || addEmployee_position.getSelectionModel().getSelectedItem() == null
                || uri == null || uri.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to UPDATE Employee ID: " + addEmployee_employeeID.getText() + "?");
        Optional<ButtonType> option = alert.showAndWait();

        if (option.isPresent() && option.get() == ButtonType.OK) {
            try {
                if (client == null) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Connection Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Client is not connected to server.");
                    errorAlert.showAndWait();
                    return;
                }

                String gender = addEmployee_gender.getSelectionModel().getSelectedItem();
                String position = addEmployee_position.getSelectionModel().getSelectedItem();

                if (gender == null) gender = "";
                if (position == null) position = "";

                String data = String.join(",",
                        addEmployee_employeeID.getText(),
                        addEmployee_firstName.getText(),
                        addEmployee_lastName.getText(),
                        gender,
                        addEmployee_phoneNum.getText(),
                        position,
                        uri,
                        java.time.LocalDate.now().toString()
                );

                String request = "UPDATE_EMPLOYEE:" + data;

                String response = client.sendRequest(request);

                if ("SUCCESS".equalsIgnoreCase(response)) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Information Message");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Successfully Updated!");
                    successAlert.showAndWait();

                    addEmployeeShowListData();
                    addEmployeeResset();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error Message");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Update failed: " + response);
                    errorAlert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error Message");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Network error: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    public void addEmployeeDelete() {
        if (addEmployee_employeeID.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter employee ID");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMATION MESSAGE");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to DELETE Employee ID: " + addEmployee_employeeID.getText() + "?");
        Optional<ButtonType> option = alert.showAndWait();

        if (option.isPresent() && option.get() == ButtonType.OK) {
            try {
                if (client == null) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Connection Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Client is not connected to server.");
                    errorAlert.showAndWait();
                    return;
                }

                String request = "DELETE_EMPLOYEE:" + addEmployee_employeeID.getText();
                String response = client.sendRequest(request);

                if ("SUCCESS".equalsIgnoreCase(response)) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Information Message");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Successfully Deleted!");
                    successAlert.showAndWait();

                    addEmployeeShowListData();
                    addEmployeeResset();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error Message");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Delete failed: " + response);
                    errorAlert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error Message");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Network error: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    public void addEmployeeResset() {
        addEmployee_employeeID.setText("");
        addEmployee_firstName.setText("");
        addEmployee_lastName.setText("");
        addEmployee_gender.getSelectionModel().clearSelection();
        addEmployee_position.getSelectionModel().clearSelection();
        addEmployee_phoneNum.setText("");
        addEmployee_image.setImage(null);
        getData.path = "";
    }

    public void addEmployeeInsertImage() {
        FileChooser open = new FileChooser();
        File file = open.showOpenDialog(main_form.getScene().getWindow());

        if (file != null) {
            getData.path = file.getAbsolutePath();

            image = new Image(file.toURI().toString(), 134, 164, false, true);
            addEmployee_image.setImage(image);
        }
    }

    private String[] postionList = {"Marketer Coordinator", "Web Developer(Back End)", "Web Developer(Font End)", "App Developer", "Data Analysis"};

    public void addEmployeePositionList() {
        List<String> listP = new ArrayList<>();
        Collections.addAll(listP, postionList);
        ObservableList listData = FXCollections.observableArrayList(listP);
        addEmployee_position.setItems(listData);
    }

    private String[] listGender = {"Male", "Female", "Others"};

    public void addEmployeeGenderList() {
        List<String> listG = new ArrayList<>();
        Collections.addAll(listG, listGender);
        ObservableList listData = FXCollections.observableArrayList(listG);
        addEmployee_gender.setItems(listData);
    }

    public ObservableList<employeeData> addEmployeeListData() {
        ObservableList<employeeData> listData = FXCollections.observableArrayList();
        try {
            String reponse = client.sendRequest("GET_LIST_DATA:");

            if(reponse == null || reponse.startsWith("ERROR")){
                System.out.println("ERROR: "+ reponse);
                return listData;
            }

            String[] records = reponse.split(";");

            for(String record : records){
                if (record.trim().isEmpty()) continue;
                String[] fields = record.split(",");
                if (fields.length >= 8) {
                    employeeData employeeD = new employeeData(
                            Integer.parseInt(fields[0]), fields[1], fields[2], fields[3], fields[4], fields[5], fields[6],
                            java.sql.Date.valueOf(fields[7])
                    );
                    listData.add(employeeD);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  listData;
    }

    private ObservableList<employeeData> addEmployeeList;

    public void addEmployeeShowListData() {
        addEmployeeList = addEmployeeListData();

        addEmployee_col_employeeID.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        addEmployee_col_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        addEmployee_col_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        addEmployee_col_gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        addEmployee_col_phoneNum.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
        addEmployee_col_position.setCellValueFactory(new PropertyValueFactory<>("position"));
        addEmployee_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));

        addEmployee_tableView.setItems(addEmployeeList);

        addEmployeeSearch();
    }
    public void addEmployeeSelect() {
        employeeData employeeD = addEmployee_tableView.getSelectionModel().getSelectedItem();
        int num = addEmployee_tableView.getSelectionModel().getSelectedIndex();

        if ((num - 1) < -1) {
            return;
        }
        addEmployee_employeeID.setText(String.valueOf(employeeD.getEmployeeId()));
        addEmployee_firstName.setText(employeeD.getFirstName());
        addEmployee_lastName.setText(employeeD.getLastName());
        addEmployee_phoneNum.setText(employeeD.getPhoneNum());

        String uri = "file:" + employeeD.getImage();

        image = new Image(uri, 134, 164, false, true);
        addEmployee_image.setImage(image);
    }
    public void salaryUpdate() {
        if (salary_employeeID.getText().isEmpty()
                || salary_firstName.getText().isEmpty()
                || salary_lastName.getText().isEmpty()
                || salary_position.getText().isEmpty()
                || salary_salary.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all required fields and select an employee first");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation Message");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to UPDATE salary for Employee ID: " + salary_employeeID.getText() + "?");
        Optional<ButtonType> option = confirmAlert.showAndWait();

        if (option.isPresent() && option.get() == ButtonType.OK) {
            try {
                if (client == null) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Connection Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Client is not connected to server.");
                    errorAlert.showAndWait();
                    return;
                }

                String data = salary_employeeID.getText() + "," + salary_salary.getText();
                String request = "UPDATE_SALARY:" + data;

                String response = client.sendRequest(request);

                if ("SUCCESS".equalsIgnoreCase(response)) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Information Message");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Successfully Updated!");
                    successAlert.showAndWait();

                    salaryShowListData();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error Message");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Update failed: " + response);
                    errorAlert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error Message");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Network error: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }


    public void salaryReset() {
        salary_employeeID.setText("");
        salary_firstName.setText("");
        salary_lastName.setText("");
        salary_position.setText("");
        salary_salary.setText("");
    }

    public ObservableList<employeeData> salaryListDataa(){
        ObservableList<employeeData> listData = FXCollections.observableArrayList();

        try{
            String reponse = client.sendRequest("GET_lIST_DATA_SALARY:");

            if(reponse == null || reponse.startsWith("ERROR")){
                System.out.println("ERROR DATA SALARY:" +reponse);
                return  listData;
            }

            String [] parts = reponse.split(";");
            for(String part : parts){
                if (part.trim().isEmpty()) continue;
                String [] filed = part.split(",");
                if(filed.length >= 5){
                    employeeData employeeD = new employeeData(
                            Integer.parseInt(filed[0]),filed[1],filed[2],filed[3],Double.parseDouble(filed[4]));
                    listData.add(employeeD);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  listData;
    }

    public ObservableList<employeeData> salaryListData() {
        ObservableList<employeeData> listData = FXCollections.observableArrayList();

        String sql = "SELECT * FROM employee_info";

        connect = database.connectDB();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            employeeData employeeD;

            while (result.next()) {
                employeeD = new employeeData(result.getInt("employee_id"),
                        result.getString("firstName"),
                        result.getString("lastName"),
                        result.getString("position"),
                        result.getDouble("salary"));

                listData.add(employeeD);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }
    public void salarySelect() {

        employeeData employeeD = salary_tableView.getSelectionModel().getSelectedItem();
        int num = salary_tableView.getSelectionModel().getSelectedIndex();

        if ((num - 1) < -1) {
            return;
        }

        salary_employeeID.setText(String.valueOf(employeeD.getEmployeeId()));
        salary_firstName.setText(employeeD.getFirstName());
        salary_lastName.setText(employeeD.getLastName());
        salary_position.setText(employeeD.getPosition());
        salary_salary.setText(String.valueOf(employeeD.getSalary()));

    }


    public ObservableList<employeeData> salaryList;

    public void salaryShowListData() {
        salaryList = salaryListDataa();

        salary_col_employeeID.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        salary_col_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        salary_col_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        salary_col_position.setCellValueFactory(new PropertyValueFactory<>("position"));
        salary_col_salary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        salary_tableView.setItems(salaryList);
    }

    public void displayUsername() {
        username.setText(Session.currentUser);
    }
    public void defaultNav() {
        home_btn.setStyle("-fx-background-color: linear-gradient(to bottom right, #23278f96, #2d645f);");
    }
    private void applyAccessControl() {
        if (!"manager".equals(Session.currentRoler)) {
            addEmployee_addBtn.setDisable(true);
            addEmployee_updateBtn.setDisable(true);
            addEmployee_deleteBtn.setDisable(true);
            salary_updateBtn.setDisable(true);
        }
    }

    public void switchForm(ActionEvent event) {
        if (event.getSource() == home_btn) {
            home_form.setVisible(true);
            addEmployee_form.setVisible(false);
            salary_form.setVisible(false);

            home_btn.setStyle("-fx-background-color: linear-gradient(to bottom right, #23278f96, #2d645f);");
            addEmployee_btn.setStyle("-fx-background-color: transparent;");
            salary_btn.setStyle("-fx-background-color: transparent;");

            homeTotalEmployees();
            homeEmployeeTotalPresent();
            homeTotalInactive();
            homeChart();
        } else if (event.getSource() == addEmployee_btn) {
            home_form.setVisible(false);
            addEmployee_form.setVisible(true);
            salary_form.setVisible(false);

            addEmployee_btn.setStyle("-fx-background-color: linear-gradient(to bottom right, #23278f96, #2d645f);");
            home_btn.setStyle("-fx-background-color: transparent;");
            salary_btn.setStyle("-fx-background-color: transparent;");

            addEmployeeGenderList();
            addEmployeePositionList();

            addEmployeeShowListData();
        } else if (event.getSource() == salary_btn) {
            home_form.setVisible(false);
            addEmployee_form.setVisible(false);
            salary_form.setVisible(true);

            salary_btn.setStyle("-fx-background-color: linear-gradient(to bottom right, #23278f96, #2d645f);");
            home_btn.setStyle("-fx-background-color: transparent;");
            addEmployee_btn.setStyle("-fx-background-color: transparent;");

            salaryShowListData();
        }
    }

    private double x = 0;
    private double y = 0;

    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");
        Optional<ButtonType> option = alert.showAndWait();
        try {
            if (option.get().equals(ButtonType.OK)) {

                logout.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/demo/hello-view.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                root.setOnMousePressed((MouseEvent event) -> {
                    x = event.getSceneX();
                    y = event.getSceneY();
                });
                root.setOnMouseDragged((MouseEvent event) -> {
                    x = event.getSceneX();
                    y = event.getSceneY();

                    stage.setOpacity(.8);
                });
                root.setOnMouseReleased((MouseEvent event) -> {
                    stage.setOpacity(1);
                });

                stage.initStyle(StageStyle.TRANSPARENT);

                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void minimizi() {
        Stage stage = (Stage) main_form.getScene().getWindow();
        stage.setIconified(true);
    }

    public void close() {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayUsername();
        defaultNav();

        addEmployeePositionList();
        addEmployeeGenderList();

        client = new EmployeeClient();
        try {
            client.connect("localhost", 12345);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
            alert.setContentText("Cannot connect to server!");
            alert.showAndWait();
        }

        homeTotalEmployees();
        homeEmployeeTotalPresent();
        homeTotalInactive();
        homeChart();

        addEmployeeShowListData();
        salaryShowListData();

        applyAccessControl();
    }


}