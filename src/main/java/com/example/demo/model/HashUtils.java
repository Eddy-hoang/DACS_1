package com.example.demo.model;

import java.security.MessageDigest;

public class HashUtils {
    public static void main(String[] args) {
        String password = "1";
        String hashed = hashSHA256(password);
        System.out.println("SHA-256 if 1: "+hashed);
    }
    public static String hashSHA256(String input){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
