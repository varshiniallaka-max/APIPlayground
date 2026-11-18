
package com.apiplayground.dao;

import com.apiplayground.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestDetailsDAO {

    public boolean saveHeaders(
            int requestId, Map<String, String> headers) {

        String deleteSql =
                "DELETE FROM request_headers WHERE request_id=?";
        String insertSql =
                "INSERT INTO request_headers "
                + "(request_id, header_name, header_value) "
                + "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement delete =
                         con.prepareStatement(deleteSql);
                 PreparedStatement insert =
                         con.prepareStatement(insertSql)) {

                delete.setInt(1, requestId);
                delete.executeUpdate();

                for (Map.Entry<String, String> entry
                        : headers.entrySet()) {

                    if (entry.getKey() == null
                            || entry.getKey().trim().isEmpty()) {
                        continue;
                    }

                    insert.setInt(1, requestId);
                    insert.setString(2, entry.getKey().trim());
                    insert.setString(3, entry.getValue());
                    insert.addBatch();
                }

                insert.executeBatch();
                con.commit();
                return true;

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

    public boolean saveParameters(
            int requestId, Map<String, String> parameters) {

        String deleteSql =
                "DELETE FROM request_parameters WHERE request_id=?";
        String insertSql =
                "INSERT INTO request_parameters "
                + "(request_id, parameter_name, parameter_value) "
                + "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement delete =
                         con.prepareStatement(deleteSql);
                 PreparedStatement insert =
                         con.prepareStatement(insertSql)) {

                delete.setInt(1, requestId);
                delete.executeUpdate();

                for (Map.Entry<String, String> entry
                        : parameters.entrySet()) {

                    if (entry.getKey() == null
                            || entry.getKey().trim().isEmpty()) {
                        continue;
                    }

                    insert.setInt(1, requestId);
                    insert.setString(2, entry.getKey().trim());
                    insert.setString(3, entry.getValue());
                    insert.addBatch();
                }

                insert.executeBatch();
                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, String> getHeaders(int requestId) {

        Map<String, String> headers = new LinkedHashMap<>();

        String sql = "SELECT header_name, header_value "
                + "FROM request_headers WHERE request_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requestId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    headers.put(
                            rs.getString("header_name"),
                            rs.getString("header_value")
                    );
                }
            }

        } catch (SQLException e) {
        }

        return headers;
    }

    public Map<String, String> getParameters(int requestId) {

        Map<String, String> parameters = new LinkedHashMap<>();

        String sql = "SELECT parameter_name, parameter_value "
                + "FROM request_parameters WHERE request_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requestId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    parameters.put(
                            rs.getString("parameter_name"),
                            rs.getString("parameter_value")
                    );
                }
            }

        } catch (SQLException e) {
        }

        return parameters;
    }
}
