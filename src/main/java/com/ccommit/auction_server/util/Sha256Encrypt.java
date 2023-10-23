package com.ccommit.auction_server.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Encrypt {
    public static String encrypt(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // md를 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = String.format("%02x", b);
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("암호화 Error", e);
        }
    }
}
