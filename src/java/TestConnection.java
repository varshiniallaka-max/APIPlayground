
import com.apiplayground.db.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {

    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("DATABASE CONNECTION SUCCESSFUL");

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT user_id, username, password FROM users")) {

                while (rs.next()) {
                    System.out.println(
                        "ID: " + rs.getInt("user_id")
                        + " | Username: " + rs.getString("username")
                        + " | Password exists: "
                        + (rs.getString("password") != null)
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("DATABASE CONNECTION FAILED");
        }
    }
}
