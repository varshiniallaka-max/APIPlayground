package com.apiplayground.dao;

import com.apiplayground.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(String username, String email, String password) {
        return addUser(username, email, password, "USER");
    }

    public boolean addUser(String username, String email,
                           String password, String role) {

        String sql = "INSERT INTO users (username, email, password, role) "
                   + "VALUES (?, ?, ?, ?)";

        String hashedPassword = PasswordUtil.hashPassword(password);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hashedPassword);
            ps.setString(4, role);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int loginUser(String username, String password) {

        String sql = "SELECT user_id, password FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String storedPassword = rs.getString("password");

                    if (storedPassword == null) {
                        return -1;
                    }

                    boolean isHash = storedPassword.matches("[a-fA-F0-9]{64}");
                    boolean matches;

                    if (isHash) {
                        matches = PasswordUtil.verifyPassword(
                                password, storedPassword);
                    } else {
                        matches = storedPassword.equals(password);
                    }

                    if (matches) {
                        // Upgrade an existing plain-text password to a hash.
                        if (!isHash) {
                            updatePasswordHash(userId, password);
                        }
                        return userId;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    private void updatePasswordHash(int userId, String password) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, PasswordUtil.hashPassword(password));
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Password update error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int authenticateUser(String username, String password) {
        return loginUser(username, password);
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Username check failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void getUsers() {
        String sql = "SELECT user_id, username, email, role FROM users";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println(
                    rs.getInt("user_id") + " | "
                    + rs.getString("username") + " | "
                    + rs.getString("email") + " | "
                    + rs.getString("role")
                );
            }

        } catch (SQLException e) {
            System.err.println("Get users error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean updateUser(int userId, String email) {
        String sql = "UPDATE users SET email = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Update user error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Delete user error: " + e.getMessage());
            return false;
        }
    }
}