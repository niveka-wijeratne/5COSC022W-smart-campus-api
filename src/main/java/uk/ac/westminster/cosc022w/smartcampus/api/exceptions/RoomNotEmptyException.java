package uk.ac.westminster.cosc022w.smartcampus.api.exceptions;

import java.util.logging.Logger;

public class RoomNotEmptyException extends RuntimeException {
    private static final Logger LOGGER = Logger.getLogger(RoomNotEmptyException.class.getName());

    private final String roomId;
    private final int sensorCount;

    public RoomNotEmptyException(String roomId, int sensorCount) {
        super("Room '" + roomId + "' cannot be deleted because it still has " + sensorCount + " sensor(s) assigned.");
        this.roomId = roomId;
        this.sensorCount = sensorCount;
        LOGGER.warning(() -> "RoomNotEmptyException roomId=" + roomId + " sensorCount=" + sensorCount);
    }

    public String getRoomId() {
        return roomId;
    }

    public int getSensorCount() {
        return sensorCount;
    }
}
