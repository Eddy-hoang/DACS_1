package com.example.demo.network;


import java.net.*;


public class EmployeeServer {
    private static final int PORT = 12345;
    public void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server đang chạy trên cổng 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Kết nối mới từ: " + clientSocket.getInetAddress() + " ,port : " + clientSocket.getPort());

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

