package com.example.demo.network;

import com.example.demo.model.database;

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
                    // Nếu yêu cầu không có đủ thông tin (command: data)
                    out.println("ERROR:Invalid request format");
                    continue;
                }

                String command = parts[0].trim().toUpperCase();
                String data = parts[1];

                switch (command) {
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
            out.println(result.toString());  // Trả về dữ liệu dạng chuỗi cho client
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

        try (Connection conn = database.connectDB()) {
            conn.setAutoCommit(false);

            String sql1 = "INSERT INTO employee(employee_id, firstName, lastName, gender, phoneNum, position, image, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            String sql2 = "INSERT INTO employee_info(employee_id, firstName, lastName, position, salary, date) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt1 = conn.prepareStatement(sql1);
                 PreparedStatement stmt2 = conn.prepareStatement(sql2)) {

                for (int i = 0; i < 7; i++) {
                    stmt1.setString(i + 1, fields[i]);
                }
                stmt1.setDate(8, java.sql.Date.valueOf(fields[7]));
                stmt1.addBatch();

                stmt2.setString(1, fields[0]);
                stmt2.setString(2, fields[1]);
                stmt2.setString(3, fields[2]);
                stmt2.setString(4, fields[5]);
                stmt2.setDouble(5, 0.0);
                stmt2.setDate(6, java.sql.Date.valueOf(fields[7]));
                stmt2.addBatch();

                int[] results1 = stmt1.executeBatch();
                int[] results2 = stmt2.executeBatch();

                if (results1.length > 0 && results1[0] > 0 && results2.length > 0 && results2[0] > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(String employeeId) {
        Connection con = null;
        try {
            con = database.connectDB();
            con.setAutoCommit(false);

            String sqlDeleteInfo = "DELETE FROM employee_info WHERE employee_id = ?";
            try (PreparedStatement stmtInfo = con.prepareStatement(sqlDeleteInfo)) {
                stmtInfo.setString(1, employeeId);
                stmtInfo.executeUpdate();
            }

            String sqlDeleteEmp = "DELETE FROM employee WHERE employee_id = ?";
            int affectedRows;
            try (PreparedStatement stmtEmp = con.prepareStatement(sqlDeleteEmp)) {
                stmtEmp.setString(1, employeeId);
                affectedRows = stmtEmp.executeUpdate();
            }

            con.commit();
            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean updateEmployee(String data) {
        try {
            String[] fields = data.split(",");
            if (fields.length < 8) return false;

            Connection conn = database.connectDB();
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

    private String getAllEmployees() {
        try {
            StringBuilder result = new StringBuilder();
            String sql = "SELECT employee_id, firstName, lastName, gender, phoneNum, position, image, date FROM employee";
            PreparedStatement stmt = database.connectDB().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.append(rs.getString("employee_id")).append(",");
                result.append(rs.getString("firstName")).append(",");
                result.append(rs.getString("lastName")).append(",");
                result.append(rs.getString("gender")).append(",");
                result.append(rs.getString("phoneNum")).append(",");
                result.append(rs.getString("position")).append(",");
                result.append(rs.getString("image")).append(",");
                result.append(rs.getDate("date")).append(";");
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR:Get employee data failed";
        }
    }

}