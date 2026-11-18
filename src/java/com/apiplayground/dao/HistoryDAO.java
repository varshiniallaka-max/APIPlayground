
package com.apiplayground.dao;

import com.apiplayground.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {

    public boolean saveHistory(
            int userId,
            int requestId,
            String method,
            String url,
            int statusCode,
            long responseTime,
            String responseBody) {

        String sql = "INSERT INTO query_history "
                + "(user_id, request_id, method, url, "
                + "status_code, response_time, response_body, "
                + "executed_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            if (requestId > 0) {
                ps.setInt(2, requestId);
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, method);
            ps.setString(4, url);

            if (statusCode > 0) {
                ps.setInt(5, statusCode);
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setLong(6, responseTime);
            ps.setString(7, responseBody);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public List<String[]> getHistoryByUser(int userId) {

        List<String[]> history = new ArrayList<>();

        String sql = "SELECT history_id, request_id, method, "
                + "url, status_code, response_time, executed_at "
                + "FROM query_history WHERE user_id=? "
                + "ORDER BY executed_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int requestId = rs.getInt("request_id");
                    String requestIdText = rs.wasNull()
                            ? "-" : String.valueOf(requestId);

                    int statusCode = rs.getInt("status_code");
                    String statusText = rs.wasNull()
                            ? "-" : String.valueOf(statusCode);

                    history.add(new String[]{
                        String.valueOf(rs.getInt("history_id")),
                        requestIdText,
                        rs.getString("method"),
                        rs.getString("url"),
                        statusText,
                        String.valueOf(rs.getLong("response_time")),
                        String.valueOf(rs.getTimestamp("executed_at"))
                    });
                }
            }

        } catch (SQLException e) {
        }

        return history;
    }

    public boolean deleteHistory(int historyId, int userId) {

        String sql = "DELETE FROM query_history "
                + "WHERE history_id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, historyId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public void saveHistory(int userId, int requestId, String method, String url, int statusCode, String responseBody) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
