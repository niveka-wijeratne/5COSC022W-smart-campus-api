package uk.ac.westminster.cosc022w.smartcampus.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;
import uk.ac.westminster.cosc022w.smartcampus.api.models.DiscoveryResponse;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {
    private static final Logger LOGGER = Logger.getLogger(DiscoveryResource.class.getName());

    @GET
    public DiscoveryResponse getDiscovery(@Context UriInfo uriInfo) {
        LOGGER.info("Discovery endpoint called");

        String base = uriInfo.getBaseUri().toString();
        DiscoveryResponse response = new DiscoveryResponse(
                "Smart Campus Sensor & Room Management API",
                "v1",
                "Module Leader",
                "module.leader@example.invalid"
        );
        response.getResources().put("rooms", base + "rooms");
        response.getResources().put("sensors", base + "sensors");
        return response;
    }
}
