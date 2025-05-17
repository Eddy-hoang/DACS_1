package com.example.demo;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {
    public static Connection connectDB() {
        try {
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee", "root", "");
            return connect;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
