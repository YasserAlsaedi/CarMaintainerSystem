package org.example;

import java.io.Serializable;

public class Car implements Serializable{

    private int id;
    private int customerId;
    private String make;
    private String model;
    private String plateNumber;

    public Car() {}

    public Car(int id, int customerId, String make, String model, String plateNumber) {
        this.id = id;
        this.customerId = customerId;
        this.make = make;
        this.model = model;
        this.plateNumber = plateNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int cid) { this.customerId = cid; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plate) { this.plateNumber = plate; }

    @Override
    public String toString() {
        return String.format("Car[id=%d, customerId=%d, %s %s, plate=%s]",
                id, customerId, make, model, plateNumber);
    }

}
