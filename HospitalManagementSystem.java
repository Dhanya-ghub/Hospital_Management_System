package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HospitalManagementSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hospital_mang_sys";
    private static final String username = "root";
    private static final String password = "dr@111";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, username, password);

            // 🚀 Launch UI
            new HospitalUI(connection);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
