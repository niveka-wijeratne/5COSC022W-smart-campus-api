package uk.ac.westminster.cosc022w.smartcampus.api.mappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.westminster.cosc022w.smartcampus.api.models.ErrorMessage;

@Provider
public class GenericThrowableMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(GenericThrowableMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.log(Level.SEVERE, "Unhandled server error", exception);
        ErrorMessage error = new ErrorMessage(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred."
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
