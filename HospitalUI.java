package HospitalManagementSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class HospitalUI {

    private Connection connection;

    // Modern Color Palette
    private final Color PRIMARY_COLOR = new Color(30, 60, 114);
    private final Color ACCENT_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color BG_COLOR = new Color(245, 247, 250);

    public HospitalUI(Connection connection) {
        this.connection = connection;

        JFrame frame = new JFrame("Hospital Management System");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // HEADER
        JLabel title = new JLabel("🏥 Hospital Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
        title.setOpaque(true);
        title.setBackground(PRIMARY_COLOR);
        title.setForeground(Color.WHITE);
        mainPanel.add(title, BorderLayout.NORTH);

        // BUTTON PANEL
        JPanel panel = new JPanel(new GridLayout(3, 2, 20, 20));
        panel.setBorder(new EmptyBorder(30, 40, 40, 40));
        panel.setBackground(BG_COLOR);

        JButton addPatient = createButton("Add Patient");
        JButton viewPatients = createButton("View Patients");
        JButton viewDoctors = createButton("View Doctors");
        JButton bookAppointment = createButton("Book Appointment");
        JButton viewAppointments = createButton("View Appointments");
        JButton cancelAppointment = createButton("Cancel Appointment");

        panel.add(addPatient);
        panel.add(viewPatients);
        panel.add(viewDoctors);
        panel.add(bookAppointment);
        panel.add(viewAppointments);
        panel.add(cancelAppointment);

        mainPanel.add(panel, BorderLayout.CENTER);
        frame.add(mainPanel);

        // ================= ACTIONS =================

        // 1. ADD PATIENT
        addPatient.addActionListener(e -> openAddPatientForm());

        // 2. VIEW PATIENTS
        viewPatients.addActionListener(e -> showTable(
                "SELECT * FROM patients",
                new String[]{"ID", "Name", "Age", "Gender"}
        ));

        // 3. VIEW DOCTORS
        viewDoctors.addActionListener(e -> showTable(
                "SELECT * FROM doctors",
                new String[]{"ID", "Name", "Specialization"}
        ));

        // 4. BOOK APPOINTMENT
        bookAppointment.addActionListener(e -> {
            try {
                JTextField pIdField = createStyledTextField("");
                JTextField dIdField = createStyledTextField("");
                JTextField dateField = createStyledTextField("");

                Object[] fields = {
                        "Patient ID:", pIdField,
                        "Doctor ID:", dIdField,
                        "Date (YYYY-MM-DD):", dateField
                };

                int option = JOptionPane.showConfirmDialog(null, fields, "Book Appointment", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    int pId = Integer.parseInt(pIdField.getText().trim());
                    int dId = Integer.parseInt(dIdField.getText().trim());
                    String appDate = dateField.getText().trim();

                    // Simple check to make sure the date parses
                    Date.valueOf(appDate);

                    // Check if Doctor is already booked on that day
                    PreparedStatement check = connection.prepareStatement(
                            "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appointment_date=?");
                    check.setInt(1, dId);
                    check.setString(2, appDate);
                    ResultSet rs = check.executeQuery();
                    rs.next();

                    if (rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "The requested doctor is already booked for this date!");
                        return;
                    }

                    // Insert appointment
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)");
                    ps.setInt(1, pId);
                    ps.setInt(2, dId);
                    ps.setString(3, appDate);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Appointment Booked Successfully!");
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, "Error: Invalid Date format! Please use YYYY-MM-DD.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error processing the appointment! Ensure the IDs exist.");
            }
        });

        // 5. VIEW APPOINTMENTS
        viewAppointments.addActionListener(e -> showTable(
                "SELECT a.id, p.name AS Patient, d.name AS Doctor, a.appointment_date " +
                        "FROM appointments a " +
                        "JOIN patients p ON a.patient_id = p.id " +
                        "JOIN doctors d ON a.doctor_id = d.id",
                new String[]{"ID", "Patient Name", "Doctor Name", "Date"}
        ));

        // 6. CANCEL APPOINTMENT
        cancelAppointment.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog("Enter Appointment ID to Cancel:");
            if (idStr != null && !idStr.trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr.trim());
                    PreparedStatement ps = connection.prepareStatement("DELETE FROM appointments WHERE id=?");
                    ps.setInt(1, id);
                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(null, "Appointment Cancelled Successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid ID: Appointment not found.");
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid numeric ID.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error processing cancellation!");
                }
            }
        });

        frame.setVisible(true);
    }

    // BUTTON STYLE HELPER
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR.darker(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // DYNAMIC ADD PATIENT DIALOG
    private void openAddPatientForm() {
        JDialog form = new JDialog((Frame) null, "Register New Patient", true);
        form.setSize(350, 400);
        form.setLocationRelativeTo(null);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form Title
        JLabel head = new JLabel("Patient Details");
        head.setFont(new Font("Segoe UI", Font.BOLD, 20));
        head.setBorder(new EmptyBorder(0, 0, 20, 0));
        container.add(head, BorderLayout.NORTH);

        // Fields Panel
        JPanel fieldsPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        fieldsPanel.setBackground(Color.WHITE);

        JTextField nameField = createStyledTextField("Name");
        JTextField ageField = createStyledTextField("Age");
        JTextField genderField = createStyledTextField("Gender (Male/Female/Other)");

        fieldsPanel.add(new JLabel("Full Name:"));
        fieldsPanel.add(nameField);
        fieldsPanel.add(new JLabel("Age:"));
        fieldsPanel.add(ageField);
        fieldsPanel.add(new JLabel("Gender:"));
        fieldsPanel.add(genderField);

        container.add(fieldsPanel, BorderLayout.CENTER);

        // Submit Button
        JButton submit = new JButton("Register Patient");
        submit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submit.setBackground(SUCCESS_COLOR);
        submit.setForeground(Color.WHITE);
        submit.setPreferredSize(new Dimension(0, 45));
        submit.setFocusPainted(false);

        submit.addActionListener(ev -> {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String gender = genderField.getText().trim();

                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO patients(name, age, gender) VALUES (?, ?, ?)");
                ps.setString(1, name);
                ps.setInt(2, age);
                ps.setString(3, gender);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(form, "Patient Registered Successfully!");
                form.dispose();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(form, "Error: Please enter a valid number for Age.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(form, "Database Error!");
            }
        });

        container.add(submit, BorderLayout.SOUTH);

        form.add(container);
        form.setVisible(true);
    }

    // TEXT FIELD STYLE HELPER
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    // TABLE VIEW METHOD
    private void showTable(String query, String[] columnNames) {
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            while (rs.next()) {
                Object[] row = new Object[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) row[i] = rs.getObject(i + 1);
                model.addRow(row);
            }
            JTable table = new JTable(model);
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

            JFrame tableFrame = new JFrame("View Data");
            tableFrame.setSize(600, 400);
            tableFrame.add(new JScrollPane(table));
            tableFrame.setLocationRelativeTo(null);
            tableFrame.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error querying database data!");
            e.printStackTrace();
        }
    }
}