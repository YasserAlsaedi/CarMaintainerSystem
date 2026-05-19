package org.example.dao;

import org.example.model.Car;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {

    // CREATE
    public boolean addCar(Car car) {
        String sql = "INSERT INTO Cars (customer_id, make, model, plate_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, car.getCustomerId());
            stmt.setString(2, car.getMake());
            stmt.setString(3, car.getModel());
            stmt.setString(4, car.getPlateNumber());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[DAO] Error adding car: " + e.getMessage());
            return false;
        }
    }

    // READ ALL
    public List<Car> getAllCars() {
        List<Car> list = new ArrayList<>();
        String sql = "SELECT * FROM Cars";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Car(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getString("plate_number")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error fetching cars: " + e.getMessage());
        }
        return list;
    }

    // READ BY CUSTOMER
    public List<Car> getCarsByCustomerId(int customerId) {
        List<Car> list = new ArrayList<>();
        String sql = "SELECT * FROM Cars WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Car(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getString("plate_number")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error fetching cars by customer: " + e.getMessage());
        }
        return list;
    }

}
