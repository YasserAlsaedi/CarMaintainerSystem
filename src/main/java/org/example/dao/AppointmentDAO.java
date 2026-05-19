package org.example.dao;

import org.example.model.Appointment;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // CREATE
    public boolean addAppointment(Appointment appt) {
        String sql = "INSERT INTO Appointments (car_id, appointment_date, status, description) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appt.getCarId());
            stmt.setString(2, appt.getAppointmentDate());
            stmt.setString(3, appt.getStatus());
            stmt.setString(4, appt.getDescription());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[DAO] Error adding appointment: " + e.getMessage());
            return false;
        }
    }

    // READ ALL
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.id, a.car_id, a.appointment_date, a.status, a.description, " +
                "c.make, c.model, c.plate_number, cu.name " +
                "FROM Appointments a " +
                "JOIN Cars c ON a.car_id = c.id " +
                "JOIN Customers cu ON c.customer_id = cu.id " +
                "ORDER BY a.appointment_date";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Appointment appt = new Appointment(
                        rs.getInt("id"),
                        rs.getInt("car_id"),
                        rs.getString("appointment_date"),
                        rs.getString("status"),
                        rs.getString("description")
                );
                // We attach extra info in description for display purposes
                appt.setDescription(
                        String.format("[%s %s - %s] Owner: %s | %s",
                                rs.getString("make"),
                                rs.getString("model"),
                                rs.getString("plate_number"),
                                rs.getString("name"),
                                rs.getString("description")
                        )
                );
                list.add(appt);
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error fetching appointments: " + e.getMessage());
        }
        return list;
    }

    // UPDATE STATUS
    public boolean updateStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE Appointments SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, appointmentId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] Error updating status: " + e.getMessage());
            return false;
        }
    }

    // INSERT NOTIFICATION
    public void addNotification(int appointmentId, String message) {
        String sql = "INSERT INTO Notifications (appointment_id, message) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);
            stmt.setString(2, message);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DAO] Error adding notification: " + e.getMessage());
        }
    }
}

