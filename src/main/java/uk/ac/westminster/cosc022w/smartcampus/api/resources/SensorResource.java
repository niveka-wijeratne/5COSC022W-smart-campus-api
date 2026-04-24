package uk.ac.westminster.cosc022w.smartcampus.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;
import uk.ac.westminster.cosc022w.smartcampus.api.models.ErrorMessage;
import uk.ac.westminster.cosc022w.smartcampus.api.models.Sensor;
import uk.ac.westminster.cosc022w.smartcampus.api.store.InMemoryStore;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {
    private static final Logger LOGGER = Logger.getLogger(SensorResource.class.getName());
    private final InMemoryStore store = InMemoryStore.getInstance();

    @GET
    public List<Sensor> listSensors(@QueryParam("type") String type) {
        LOGGER.fine(() -> "Listing sensors type=" + type);
        return store.listSensors(type);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null || sensor.getId() == null || sensor.getId().isBlank()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorMessage(400, "VALIDATION_ERROR", "Sensor 'id' is required."))
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        if (sensor.getRoomId() == null || sensor.getRoomId().isBlank()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorMessage(400, "VALIDATION_ERROR", "Sensor 'roomId' is required."))
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        if (store.getSensor(sensor.getId()) != null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.CONFLICT)
                            .entity(new ErrorMessage(409, "SENSOR_ALREADY_EXISTS", "Sensor with id '" + sensor.getId() + "' already exists."))
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }

        Sensor created = store.createSensor(sensor);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        LOGGER.info(() -> "Sensor created id=" + created.getId());
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("/{sensorId}")
    public Sensor getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(new ErrorMessage(404, "SENSOR_NOT_FOUND", "Sensor not found: " + sensorId))
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        return sensor;
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource readings(@PathParam("sensorId") String sensorId) {
        LOGGER.fine(() -> "Sub-resource locator for sensor readings sensorId=" + sensorId);
        return new SensorReadingResource(sensorId);
    }
}
