package uk.ac.westminster.cosc022w.smartcampus.api.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import uk.ac.westminster.cosc022w.smartcampus.api.exceptions.LinkedResourceNotFoundException;
import uk.ac.westminster.cosc022w.smartcampus.api.exceptions.RoomNotEmptyException;
import uk.ac.westminster.cosc022w.smartcampus.api.exceptions.SensorUnavailableException;
import uk.ac.westminster.cosc022w.smartcampus.api.models.Room;
import uk.ac.westminster.cosc022w.smartcampus.api.models.Sensor;
import uk.ac.westminster.cosc022w.smartcampus.api.models.SensorReading;

public class InMemoryStore {
    private static final Logger LOGGER = Logger.getLogger(InMemoryStore.class.getName());
    private static final InMemoryStore INSTANCE = new InMemoryStore();

    private final Map<String, Room> roomsById = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensorsById = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readingsBySensorId = new ConcurrentHashMap<>();

    private InMemoryStore() {
        LOGGER.info("InMemoryStore initialised");
        seedData();
    }

    private void seedData() {
        // At least 2 rooms and 1 sensor, per rubric/testing convenience.
        Room lib301 = new Room("LIB-301", "Library Quiet Study", 60);
        Room sci200 = new Room("SCI-200", "Science Lab 200", 30);
        roomsById.put(lib301.getId(), lib301);
        roomsById.put(sci200.getId(), sci200);

        Sensor co2 = new Sensor("CO2-001", "CO2", "ACTIVE", 0, lib301.getId());
        sensorsById.put(co2.getId(), co2);
        synchronized (lib301) {
            lib301.getSensorIds().add(co2.getId());
        }

        LOGGER.info("Seed data created: 2 rooms, 1 sensor");
    }

    public static InMemoryStore getInstance() {
        return INSTANCE;
    }

    // Rooms
    public List<Room> listRooms() {
        return new ArrayList<>(roomsById.values());
    }

    public Room getRoom(String roomId) {
        return roomsById.get(roomId);
    }

    public Room createRoom(Room room) {
        roomsById.put(room.getId(), room);
        LOGGER.info(() -> "Room created id=" + room.getId());
        return room;
    }

    public boolean deleteRoom(String roomId) {
        Room room = roomsById.get(roomId);
        if (room == null) {
            return false;
        }
        synchronized (room) {
            if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
                throw new RoomNotEmptyException(roomId, room.getSensorIds().size());
            }
        }
        roomsById.remove(roomId);
        LOGGER.info(() -> "Room deleted id=" + roomId);
        return true;
    }

    // Sensors
    public List<Sensor> listSensors(String typeFilter) {
        List<Sensor> result = new ArrayList<>();
        for (Sensor s : sensorsById.values()) {
            if (typeFilter == null || typeFilter.isBlank()) {
                result.add(s);
            } else if (s.getType() != null && s.getType().equalsIgnoreCase(typeFilter)) {
                result.add(s);
            }
        }
        return result;
    }

    public Sensor getSensor(String sensorId) {
        return sensorsById.get(sensorId);
    }

    public Sensor createSensor(Sensor sensor) {
        Room room = roomsById.get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException("roomId", sensor.getRoomId());
        }

        sensorsById.put(sensor.getId(), sensor);
        synchronized (room) {
            room.getSensorIds().add(sensor.getId());
        }
        LOGGER.info(() -> "Sensor created id=" + sensor.getId() + " roomId=" + sensor.getRoomId());
        return sensor;
    }

    // Readings
    public List<SensorReading> listReadings(String sensorId) {
        List<SensorReading> readings = readingsBySensorId.get(sensorId);
        if (readings == null) {
            return List.of();
        }
        synchronized (readings) {
            return new ArrayList<>(readings);
        }
    }

    public SensorReading addReading(String sensorId, SensorReading reading) {
        Sensor sensor = sensorsById.get(sensorId);
        if (sensor == null) {
            throw new LinkedResourceNotFoundException("sensorId", sensorId);
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        if (reading.getId() == null || reading.getId().isBlank()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() <= 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        List<SensorReading> readings = readingsBySensorId.computeIfAbsent(
                sensorId,
                ignored -> Collections.synchronizedList(new ArrayList<>())
        );
        readings.add(reading);

        sensor.setCurrentValue(reading.getValue());
        LOGGER.info(() -> "Reading added sensorId=" + sensorId + " readingId=" + reading.getId());
        return reading;
    }
}
