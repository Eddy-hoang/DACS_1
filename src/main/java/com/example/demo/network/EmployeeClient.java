package com.example.demo.network;

import java.io.*;
import java.net.*;

public class EmployeeClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String sendRequest(String request) throws IOException {
        out.println(request);
        return in.readLine();
    }

    public void disconnect() throws IOException {
        if (socket != null) socket.close();
    }
}