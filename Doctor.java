package HospitalManagementSystem;

import java.sql.*;

public class Doctor {

    private Connection connection;

    public Doctor(Connection connection){
        this.connection = connection;
    }

    public void viewDoctor(){
        String query = "SELECT * FROM doctors";

        try{
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("+------------+----------------+--------------------+");
            System.out.println("| Doctor ID  | Name           | Specialization     |");
            System.out.println("+------------+----------------+--------------------+");

            while(rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String specialization = rs.getString("specialization");

                System.out.printf("| %-10d | %-14s | %-18s |\n", id, name, specialization);
            }

            System.out.println("+------------+----------------+--------------------+");

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean getDoctorById(int id){
        String query = "SELECT * FROM doctors WHERE id=?";

        try{
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return true;
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
