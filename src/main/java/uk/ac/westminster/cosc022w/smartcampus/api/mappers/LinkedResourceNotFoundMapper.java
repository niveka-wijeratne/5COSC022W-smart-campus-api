package uk.ac.westminster.cosc022w.smartcampus.api.mappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;
import uk.ac.westminster.cosc022w.smartcampus.api.exceptions.LinkedResourceNotFoundException;
import uk.ac.westminster.cosc022w.smartcampus.api.models.ErrorMessage;

@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    private static final Logger LOGGER = Logger.getLogger(LinkedResourceNotFoundMapper.class.getName());

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        LOGGER.info(() -> "Mapping LinkedResourceNotFoundException to 422 for field=" + exception.getField());
        ErrorMessage error = new ErrorMessage(
                422,
                "LINKED_RESOURCE_NOT_FOUND",
                exception.getMessage()
        );
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
