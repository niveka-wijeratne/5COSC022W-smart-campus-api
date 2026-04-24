package uk.ac.westminster.cosc022w.smartcampus.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import uk.ac.westminster.cosc022w.smartcampus.api.models.Room;
import uk.ac.westminster.cosc022w.smartcampus.api.store.InMemoryStore;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
public class RoomResource {
    private static final Logger LOGGER = Logger.getLogger(RoomResource.class.getName());
    private final InMemoryStore store = InMemoryStore.getInstance();

    @GET
    public List<Room> listRooms() {
        LOGGER.fine("Listing rooms");
        return store.listRooms();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null || room.getId() == null || room.getId().isBlank()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorMessage(400, "VALIDATION_ERROR", "Room 'id' is required."))
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        if (store.getRoom(room.getId()) != null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.CONFLICT)
                            .entity(new ErrorMessage(409, "ROOM_ALREADY_EXISTS", "Room with id '" + room.getId() + "' already exists."))
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }

        Room created = store.createRoom(room);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        LOGGER.info(() -> "Room created id=" + created.getId());
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("/{roomId}")
    public Room getRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(new ErrorMessage(404, "ROOM_NOT_FOUND", "Room not found: " + roomId))
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        return room;
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        boolean deleted = store.deleteRoom(roomId);
        LOGGER.info(() -> "Delete room requested id=" + roomId + " deleted=" + deleted);
        return Response.noContent().build();
    }
}
