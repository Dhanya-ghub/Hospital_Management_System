package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Patient {

    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    // ADD PATIENT
    public void addPatient(){
        System.out.print("Enter Patient Name : ");
        String name = scanner.nextLine();

        System.out.print("Enter Patient Age : ");
        int age = scanner.nextInt();
        scanner.nextLine(); // clear buffer

        System.out.print("Enter Patient Gender : ");
        String gender = scanner.nextLine();

        try{
            String query = "INSERT INTO patients(name, age, gender) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);

            int rows = ps.executeUpdate();

            if(rows > 0){
                System.out.println("Patient Added Successfully!!");
            } else {
                System.out.println("Failed to add Patient");
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    // VIEW PATIENTS
    public void viewPatient(){
        String query = "SELECT * FROM patients";

        try{
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("+------------+----------------+-----+--------+");
            System.out.println("| Patient ID | Name           | Age | Gender |");
            System.out.println("+------------+----------------+-----+--------+");

            while(rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");

                System.out.printf("| %-10d | %-14s | %-3d | %-6s |\n", id, name, age, gender);
            }

            System.out.println("+------------+----------------+-----+--------+");

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    // CHECK PATIENT
    public boolean getPatientById(int id){
        String query = "SELECT * FROM patients WHERE id=?";

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
