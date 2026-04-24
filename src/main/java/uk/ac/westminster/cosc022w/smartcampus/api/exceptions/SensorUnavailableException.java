package uk.ac.westminster.cosc022w.smartcampus.api.exceptions;

import java.util.logging.Logger;

public class SensorUnavailableException extends RuntimeException {
    private static final Logger LOGGER = Logger.getLogger(SensorUnavailableException.class.getName());

    private final String sensorId;
    private final String status;

    public SensorUnavailableException(String sensorId, String status) {
        super("Sensor '" + sensorId + "' cannot accept readings while status is '" + status + "'.");
        this.sensorId = sensorId;
        this.status = status;
        LOGGER.warning(() -> "SensorUnavailableException sensorId=" + sensorId + " status=" + status);
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getStatus() {
        return status;
    }
}
