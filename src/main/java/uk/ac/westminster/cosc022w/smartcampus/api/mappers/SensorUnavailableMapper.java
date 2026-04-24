package uk.ac.westminster.cosc022w.smartcampus.api.mappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;
import uk.ac.westminster.cosc022w.smartcampus.api.exceptions.SensorUnavailableException;
import uk.ac.westminster.cosc022w.smartcampus.api.models.ErrorMessage;

@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {
    private static final Logger LOGGER = Logger.getLogger(SensorUnavailableMapper.class.getName());

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        LOGGER.info(() -> "Mapping SensorUnavailableException to 403 for sensorId=" + exception.getSensorId());
        ErrorMessage error = new ErrorMessage(
                Response.Status.FORBIDDEN.getStatusCode(),
                "SENSOR_UNAVAILABLE",
                exception.getMessage()
        );
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
