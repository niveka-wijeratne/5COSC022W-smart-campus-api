package uk.ac.westminster.cosc022w.smartcampus.api.models;

import java.util.logging.Logger;

public class Sensor {
    private static final Logger LOGGER = Logger.getLogger(Sensor.class.getName());

    private String id;
    private String type;
    private String status; // "ACTIVE", "MAINTENANCE", "OFFLINE"
    private double currentValue;
    private String roomId;

    public Sensor() {
        // JSON-B needs a no-arg constructor
    }

    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
        LOGGER.fine(() -> "Created Sensor id=" + id + " roomId=" + roomId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
