package uk.ac.westminster.cosc022w.smartcampus.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;
import uk.ac.westminster.cosc022w.smartcampus.api.models.ErrorMessage;
import uk.ac.westminster.cosc022w.smartcampus.api.models.SensorReading;
import uk.ac.westminster.cosc022w.smartcampus.api.store.InMemoryStore;

@Produces(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
    private static final Logger LOGGER = Logger.getLogger(SensorReadingResource.class.getName());

    private final String sensorId;
    private final InMemoryStore store = InMemoryStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> listReadings() {
        LOGGER.fine(() -> "Listing readings sensorId=" + sensorId);
        return store.listReadings(sensorId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading, @Context UriInfo uriInfo) {
        if (reading == null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorMessage(400, "VALIDATION_ERROR", "Reading payload is required."))
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }

        SensorReading created = store.addReading(sensorId, reading);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        LOGGER.info(() -> "Reading created sensorId=" + sensorId + " readingId=" + created.getId());
        return Response.created(location).entity(created).build();
    }
}
