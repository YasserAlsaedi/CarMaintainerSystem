package org.example.model;

import java.io.Serializable;

public class Appointment implements Serializable {

    private int id;
    private int carId;
    private String appointmentDate;  // stored as String for simplicity
    private String status;           // Pending | In Progress | Done
    private String description;

    public Appointment() {}

    public Appointment(int id, int carId, String appointmentDate, String status, String description) {
        this.id = id;
        this.carId = carId;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String d) { this.appointmentDate = d; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String desc) { this.description = desc; }

    @Override
    public String toString() {
        return String.format("Appointment[id=%d, carId=%d, date=%s, status=%s, desc=%s]",
                id, carId, appointmentDate, status, description);
    }

}
