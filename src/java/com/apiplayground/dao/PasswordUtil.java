
package com.apiplayground.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(
                    password.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b & 0xff));
            }

            return result.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "Unable to hash password", e
            );
        }
    }

    public static boolean verifyPassword(
            String password, String storedHash) {

        if (password == null || storedHash == null) {
            return false;
        }

        return hashPassword(password).equals(storedHash);
    }
}
