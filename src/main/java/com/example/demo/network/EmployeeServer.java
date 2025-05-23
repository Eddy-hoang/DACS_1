package com.example.demo.network;

import com.example.demo.database;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmployeeServer {
    private static final int PORT = 12345;
    private static final int MAX_THREADS = 10;
    private ExecutorService executorService;

    public EmployeeServer() {
        executorService = Executors.newFixedThreadPool(MAX_THREADS);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                executorService.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {
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

                        case "GET_EMPLOYEE_DATA":
                            String allData = getAllEmployees();
                            out.println(allData);
                            break;
                        default:
                            out.println("ERROR:Unknown command");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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

        private boolean addEmployee(String data) {
            try {
                // nhận: employee_id,firstName,lastName,gender,phoneNum,position,image,date
                String[] fields = data.split(",");
                if (fields.length < 8) return false;

                Connection conn = database.connectDB();
                conn.setAutoCommit(false); // Đảm bảo cả hai INSERT cùng thành công hoặc cùng rollback

                // INSERT vào bảng employee
                String sql1 = "INSERT INTO employee(employee_id, firstName, lastName, gender, phoneNum, position, image, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt1 = conn.prepareStatement(sql1);
                for (int i = 0; i < 8; i++) {
                    if (i == 7) {
                        stmt1.setDate(8, java.sql.Date.valueOf(fields[7]));
                    } else {
                        stmt1.setString(i + 1, fields[i]);
                    }
                }

                // INSERT vào bảng employee_info
                String sql2 = "INSERT INTO employee_info(employee_id, firstName, lastName, position, salary, date) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt2 = conn.prepareStatement(sql2);
                stmt2.setString(1, fields[0]);
                stmt2.setString(2, fields[1]);
                stmt2.setString(3, fields[2]);
                stmt2.setString(4, fields[5]);
                stmt2.setDouble(5, 0.0);
                stmt2.setDate(6, java.sql.Date.valueOf(fields[7]));

                int result1 = stmt1.executeUpdate();
                int result2 = stmt2.executeUpdate();

                if (result1 > 0 && result2 > 0) {
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

        private boolean deleteEmployee(String employeeId) {
            try {
                String sql = "DELETE FROM employee WHERE employee_id = ?";
                PreparedStatement stmt = database.connectDB().prepareStatement(sql);
                stmt.setString(1, employeeId);
                return stmt.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private boolean updateEmployee(String data) {
            try {
                // nhận:  employee_id,firstName,lastName,gender,phoneNum,position,image,date
                String[] fields = data.split(",");
                if (fields.length < 8) return false;

                Connection conn = database.connectDB();
                conn.setAutoCommit(false);

                String sqlEmp = "UPDATE employee SET firstName=?, lastName=?, gender=?, phoneNum=?, position=?, image=?, date=? WHERE employee_id=?";
                PreparedStatement stmtEmp = conn.prepareStatement(sqlEmp);
                stmtEmp.setString(1, fields[1]);
                stmtEmp.setString(2, fields[2]);
                stmtEmp.setString(3, fields[3]);
                stmtEmp.setString(4, fields[4]);
                stmtEmp.setString(5, fields[5]);
                stmtEmp.setString(6, fields[6]);
                stmtEmp.setDate(7, java.sql.Date.valueOf(fields[7]));
                stmtEmp.setString(8, fields[0]);
                int r1 = stmtEmp.executeUpdate();

                String sqlInfo = "UPDATE employee_info SET firstName=?, lastName=?, position=?, salary=? WHERE employee_id=?";
                PreparedStatement stmtInfo = conn.prepareStatement(sqlInfo);
                stmtInfo.setString(1, fields[1]);
                stmtInfo.setString(2, fields[2]);
                stmtInfo.setString(3, fields[5]);
                stmtInfo.setDouble(4, Double.parseDouble(fields[8]));
                stmtInfo.setString(5, fields[0]);
                int r2 = stmtInfo.executeUpdate();

                if (r1 > 0 && r2 > 0) {
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
    public static void main(String[] args) {
        EmployeeServer server = new EmployeeServer();
        server.start();
    }
}
