package uk.ac.westminster.cosc022w.smartcampus.api.mappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;
import uk.ac.westminster.cosc022w.smartcampus.api.exceptions.RoomNotEmptyException;
import uk.ac.westminster.cosc022w.smartcampus.api.models.ErrorMessage;

@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {
    private static final Logger LOGGER = Logger.getLogger(RoomNotEmptyMapper.class.getName());

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        LOGGER.info(() -> "Mapping RoomNotEmptyException to 409 for roomId=" + exception.getRoomId());
        ErrorMessage error = new ErrorMessage(
                Response.Status.CONFLICT.getStatusCode(),
                "ROOM_NOT_EMPTY",
                exception.getMessage()
        );
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
