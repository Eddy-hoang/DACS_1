package com.example.demo.network;

import com.example.demo.model.database;
import com.example.demo.model.employeeData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Received: " + request);
                String[] parts = request.split(":", 2);

                if (parts.length < 2) {
                    out.println("ERROR:Invalid request format");
                    continue;
                }

                String command = parts[0].trim().toUpperCase();
                String data = parts[1];

                switch (command) {
                    case "LOGIN_ADMIN" :
                        loginAdmin(data);
                        break;
                    case "ADD_EMPLOYEE":
                        if (addEmployee(data)) {
                            out.println("SUCCESS");
                        } else {
                            out.println("ERROR:Add employee failed");
                        }
                        break;
                    case "DELETE_EMPLOYEE":
                        if (deleteEmployee(data)) {
                            out.println("SUCCESS");
                        } else {
                            out.println("ERROR:Delete employee failed");
                        }
                        break;
                    case "UPDATE_EMPLOYEE":
                        if (updateEmployee(data)) {
                            out.println("SUCCESS");
                        } else {
                            out.println("ERROR:Update employee failed");
                        }
                        break;
                    case "UPDATE_SALARY":
                        if (updateSalary(data)) {
                            out.println("SUCCESS");
                        } else {
                            out.println("ERROR:Update salary failed");
                        }
                        break;
                    case "GET_CHART_DATA":
                        if (!chartData(out)) {
                            out.println("ERROR: chart failed!");
                            out.flush();
                        }
                        break;
                    case "GET_TOTAL_EMPLOYEES":
                        homeTotalEmployees(out);
                        break;
                    case "GET_TOTAL_PRESENT":
                        homeTotalPersent(out);
                        break;
                    case "GET_TOTAL_INACTIVE":
                        homeTotalInactive();
                        break;
                    case "GET_LIST_DATA":
                        getListData();
                        break;
                    case "GET_LIST_DATA_SALARY":
                        getListDataSalary();
                        break;
                    default:
                        out.println("ERROR:Unknown command");
                }
            }
        } catch (IOException e) {
            System.out.println("Client đã ngắt kết nối :" + clientSocket.getInetAddress() + " ,port : " + clientSocket.getPort());
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loginAdmin(String data) {
        try {
            if (data == null || !data.contains(",")) {
                out.println("ERROR:Invalid login format");
                return;
            }

            String[] credentials = data.split(",", 2);
            String username = credentials[0].trim();
            String hashedPassword = credentials[1].trim();

            try (Connection conn = database.connectDB();
                 PreparedStatement stmt = conn.prepareStatement("SELECT role FROM admin WHERE username = ? AND password = ?")) {

                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String role = rs.getString("role");
                        out.println("SUCCESS:" + role);
                    } else {
                        out.println("FAIL");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                out.println("ERROR:" + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("ERROR:Server-side login error");
        }
    }


    private void getListDataSalary() {
        String sql = "SELECT * FROM employee_info";
        try(Connection conn = database.connectDB();
            PreparedStatement prepare = conn.prepareStatement(sql);
            ResultSet result = prepare.executeQuery()){

            StringBuilder response = new StringBuilder();

            boolean first  = true;
            while(result.next()){
                if (!first) response.append(";");
                first = false;

                response.append(result.getInt("employee_id")).append(",")
                        .append(result.getString("firstName")).append(",")
                        .append(result.getString("lastName")).append(",")
                        .append(result.getString("position")).append(",")
                        .append(result.getString("salary"));
            }

            if(response.length() == 0){
                out.println("No employee data found");
            }else {
                out.println(response.toString());
            }
        } catch (SQLException e) {
            out.println("Get employee list failed");
            e.printStackTrace();
        }
    }

    private void getListData() {
        String sql = "SELECT * FROM employee";

        try(Connection conn = database.connectDB();
            PreparedStatement prepare = conn.prepareStatement(sql);
            ResultSet result = prepare.executeQuery()){

            StringBuilder response = new StringBuilder();

            boolean first  = true;
            while(result.next()){
                if (!first) response.append(";");
                first = false;

                response.append(result.getInt("employee_id")).append(",")
                        .append(result.getString("firstName")).append(",")
                        .append(result.getString("lastName")).append(",")
                        .append(result.getString("gender")).append(",")
                        .append(result.getString("phoneNum")).append(",")
                        .append(result.getString("position")).append(",")
                        .append(result.getString("image")).append(",")
                        .append(result.getDate("date"));
            }

            if(response.length() == 0){
                out.println("No employee data found");
            }else {
                out.println(response.toString());
            }
        } catch (SQLException e) {
            out.println("Get employee list failed");
            e.printStackTrace();
        }
    }

    private void homeTotalInactive() {
        String sql = "SELECT COUNT(id) FROM employee_info WHERE salary = '0.0'";
        int countData = 0;

        try(Connection con = database.connectDB();
            PreparedStatement prepare = con.prepareStatement(sql);
            ResultSet result = prepare.executeQuery()){
            if(result.next()){
                countData = result.getInt(1);
            }

            out.println("Total Inactive: " + countData);
        } catch (SQLException e) {
            out.println("ERROR: Failed to get total Inactive");
            e.printStackTrace();
        }
    }


    private void homeTotalPersent(PrintWriter out) {
        String sql = "SELECT COUNT(id) FROM employee_info";
        int countData = 0;
        try(Connection con = database.connectDB();
            PreparedStatement prepare = con.prepareStatement(sql);
            ResultSet result = prepare.executeQuery()){
            if(result.next()){
                countData = result.getInt(1);
            }

            out.println("Total Persent: "+countData);
        } catch (Exception e) {
            out.println("ERROR: Falied to get total Persent");
            e.printStackTrace();
        }
    }

    private void homeTotalEmployees(PrintWriter out) {
        String sql = "SELECT COUNT(id) FROM employee";
        int countData = 0;

        try (Connection con = database.connectDB();
            PreparedStatement prepare = con.prepareStatement(sql);
            ResultSet result = prepare.executeQuery()){
            if(result.next()){
                countData = result.getInt(1);
            }

            out.println("Total Employees: "+countData);
        } catch (SQLException e) {
            out.println("ERROR: Failed to get total employees");
            e.printStackTrace();
        }
    }


    private boolean chartData(PrintWriter out) {
        StringBuilder result = new StringBuilder();
        String sql = "SELECT date, COUNT(id) FROM employee GROUP BY date ORDER BY TIMESTAMP(date) ASC LIMIT 7";

        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.append(rs.getString("date")).append(",").append(rs.getInt(2)).append(";");
            }
            out.println(result.toString());
            out.flush();
            return true;

        } catch (SQLException e) {
            out.println("ERROR:Cannot get chart data");
            out.flush();
            e.printStackTrace();
            return false;
        }
    }

    private boolean addEmployee(String data) {
        String[] fields = data.split(",");
        if (fields.length < 8) return false;

        String sql1 = "INSERT INTO employee(employee_id, firstName, lastName, gender, phoneNum, position, image, date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sql2 = "INSERT INTO employee_info(employee_id, firstName, lastName, position, salary, date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = database.connectDB();
             PreparedStatement stmt1 = conn.prepareStatement(sql1);
             PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            conn.setAutoCommit(false);

            stmt1.setString(1, fields[0]);
            stmt1.setString(2, fields[1]);
            stmt1.setString(3, fields[2]);
            stmt1.setString(4, fields[3]);
            stmt1.setString(5, fields[4]);
            stmt1.setString(6, fields[5]);
            stmt1.setString(7, fields[6]);
            stmt1.setDate(8, java.sql.Date.valueOf(fields[7]));

            int rows1 = stmt1.executeUpdate();

            stmt2.setString(1, fields[0]);
            stmt2.setString(2, fields[1]);
            stmt2.setString(3, fields[2]);
            stmt2.setString(4, fields[5]);
            stmt2.setDouble(5, 0.0);
            stmt2.setDate(6, java.sql.Date.valueOf(fields[7]));

            int rows2 = stmt2.executeUpdate();

            if (rows1 > 0 && rows2 > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteEmployee(String employeeId) {
        String sql1 = "DELETE FROM employee_info WHERE employee_id = ?";
        String sql2 = "DELETE FROM employee WHERE employee_id = ?";

        try (Connection con = database.connectDB();
             PreparedStatement stmt1 = con.prepareStatement(sql1);
             PreparedStatement stmt2 = con.prepareStatement(sql2)) {
            con.setAutoCommit(false);

            stmt1.setString(1, employeeId);
            stmt1.executeUpdate();

            stmt2.setString(1, employeeId);
            int rows = stmt2.executeUpdate();

            if (rows > 0) {
                con.commit();
                return true;
            } else {
                con.rollback();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    private boolean updateEmployee(String data) {
        try(Connection conn = database.connectDB();) {
            String[] fields = data.split(",");
            if (fields.length < 8) return false;

            conn.setAutoCommit(false);

            String sql = "UPDATE employee SET firstName=?, lastName=?, gender=?, phoneNum=?, position=?, image=?, date=? WHERE employee_id=?";
            PreparedStatement stmtEmp = conn.prepareStatement(sql);
            stmtEmp.setString(1, fields[1]);
            stmtEmp.setString(2, fields[2]);
            stmtEmp.setString(3, fields[3]);
            stmtEmp.setString(4, fields[4]);
            stmtEmp.setString(5, fields[5]);
            stmtEmp.setString(6, fields[6]);
            stmtEmp.setDate(7, java.sql.Date.valueOf(fields[7]));
            stmtEmp.setString(8, fields[0]);
            int r1 = stmtEmp.executeUpdate();

            if (r1 > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateSalary(String data) {
        try (Connection conn = database.connectDB()) {
            String[] fields = data.split(",");
            if (fields.length < 2) return false;

            conn.setAutoCommit(false);

            String sql = "UPDATE employee_info SET salary = ? WHERE employee_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, Double.parseDouble(fields[1]));
            stmt.setString(2, fields[0]);

            int rows = stmt.executeUpdate();
            conn.commit();

            System.out.println("Rows affected: " + rows);
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}