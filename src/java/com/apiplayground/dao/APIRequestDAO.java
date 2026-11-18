
package com.apiplayground.dao;

import com.apiplayground.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class APIRequestDAO {

    public int addRequestForUserAndReturnId(
            int userId,
            String requestName,
            String method,
            String url,
            String body) {

        String insertRequest =
                "INSERT INTO api_requests "
                + "(user_id, request_name, method, url, body) "
                + "VALUES (?, ?, ?, ?, ?)";

        String insertSaved =
                "INSERT INTO saved_requests "
                + "(user_id, request_id, saved_name, saved_at) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int requestId;

                try (PreparedStatement ps = con.prepareStatement(
                        insertRequest, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, userId);
                    ps.setString(2, requestName);
                    ps.setString(3, method);
                    ps.setString(4, url);
                    ps.setString(5, body);

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            con.rollback();
                            return -1;
                        }

                        requestId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps =
                        con.prepareStatement(insertSaved)) {

                    ps.setInt(1, userId);
                    ps.setInt(2, requestId);
                    ps.setString(3, requestName);
                    ps.executeUpdate();
                }

                con.commit();
                return requestId;

            } catch (SQLException e) {
                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            return -1;
        }
    }

    public boolean updateRequest(
            int requestId,
            int userId,
            String requestName,
            String method,
            String url,
            String body) {

        String updateRequest =
                "UPDATE api_requests SET request_name=?, "
                + "method=?, url=?, body=? "
                + "WHERE request_id=? AND user_id=?";

        String updateSaved =
                "UPDATE saved_requests SET saved_name=? "
                + "WHERE request_id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int rows;

                try (PreparedStatement ps =
                        con.prepareStatement(updateRequest)) {

                    ps.setString(1, requestName);
                    ps.setString(2, method);
                    ps.setString(3, url);
                    ps.setString(4, body);
                    ps.setInt(5, requestId);
                    ps.setInt(6, userId);

                    rows = ps.executeUpdate();
                }

                if (rows > 0) {
                    try (PreparedStatement ps =
                            con.prepareStatement(updateSaved)) {

                        ps.setString(1, requestName);
                        ps.setInt(2, requestId);
                        ps.setInt(3, userId);
                        ps.executeUpdate();
                    }
                }

                con.commit();
                return rows > 0;

            } catch (SQLException e) {
                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteRequest(int requestId, int userId) {

        String deleteSaved =
                "DELETE FROM saved_requests "
                + "WHERE request_id=? AND user_id=?";

        String deleteHeaders =
                "DELETE FROM request_headers WHERE request_id=?";

        String deleteParameters =
                "DELETE FROM request_parameters WHERE request_id=?";

        String deleteRequest =
                "DELETE FROM api_requests "
                + "WHERE request_id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                        con.prepareStatement(deleteSaved)) {
                    ps.setInt(1, requestId);
                    ps.setInt(2, userId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                        con.prepareStatement(deleteHeaders)) {
                    ps.setInt(1, requestId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                        con.prepareStatement(deleteParameters)) {
                    ps.setInt(1, requestId);
                    ps.executeUpdate();
                }

                int rows;

                try (PreparedStatement ps =
                        con.prepareStatement(deleteRequest)) {
                    ps.setInt(1, requestId);
                    ps.setInt(2, userId);
                    rows = ps.executeUpdate();
                }

                con.commit();
                return rows > 0;

            } catch (SQLException e) {
                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            return false;
        }
    }

    public List<String[]> getRequestsByUser(int userId) {

        List<String[]> requests = new ArrayList<>();

        String sql =
                "SELECT a.request_id, s.saved_name, a.method, "
                + "a.url, a.body "
                + "FROM api_requests a "
                + "INNER JOIN saved_requests s "
                + "ON a.request_id=s.request_id "
                + "AND a.user_id=s.user_id "
                + "WHERE a.user_id=? "
                + "ORDER BY s.saved_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(new String[]{
                        String.valueOf(rs.getInt("request_id")),
                        rs.getString("saved_name"),
                        rs.getString("method"),
                        rs.getString("url"),
                        rs.getString("body")
                    });
                }
            }

        } catch (SQLException e) {
        }

        return requests;
    }

    public String[] getRequestById(int requestId, int userId) {

        String sql =
                "SELECT a.request_id, s.saved_name, a.method, "
                + "a.url, a.body "
                + "FROM api_requests a "
                + "INNER JOIN saved_requests s "
                + "ON a.request_id=s.request_id "
                + "AND a.user_id=s.user_id "
                + "WHERE a.request_id=? AND a.user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        String.valueOf(rs.getInt("request_id")),
                        rs.getString("saved_name"),
                        rs.getString("method"),
                        rs.getString("url"),
                        rs.getString("body")
                    };
                }
            }

        } catch (SQLException e) {
        }

        return null;
    }
}
