package uk.ac.westminster.cosc022w.smartcampus.api.mappers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;
import uk.ac.westminster.cosc022w.smartcampus.api.models.ErrorMessage;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    private static final Logger LOGGER = Logger.getLogger(WebApplicationExceptionMapper.class.getName());

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response original = exception.getResponse();
        int status = (original == null) ? 500 : original.getStatus();

        Object entity = (original == null) ? null : original.getEntity();
        if (entity instanceof ErrorMessage) {
            return Response.status(status)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(entity)
                    .build();
        }

        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = Response.Status.fromStatusCode(status) != null
                    ? Response.Status.fromStatusCode(status).getReasonPhrase()
                    : "Request failed.";
        }

        LOGGER.info(() -> "Mapping WebApplicationException to JSON status=" + status);
        ErrorMessage error = new ErrorMessage(status, "HTTP_" + status, message);
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
