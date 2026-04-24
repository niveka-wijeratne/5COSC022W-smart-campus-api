package uk.ac.westminster.cosc022w.smartcampus.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import uk.ac.westminster.cosc022w.smartcampus.api.filters.ApiLoggingFilter;
import uk.ac.westminster.cosc022w.smartcampus.api.mappers.GenericThrowableMapper;
import uk.ac.westminster.cosc022w.smartcampus.api.mappers.LinkedResourceNotFoundMapper;
import uk.ac.westminster.cosc022w.smartcampus.api.mappers.RoomNotEmptyMapper;
import uk.ac.westminster.cosc022w.smartcampus.api.mappers.SensorUnavailableMapper;
import uk.ac.westminster.cosc022w.smartcampus.api.mappers.WebApplicationExceptionMapper;
import uk.ac.westminster.cosc022w.smartcampus.api.resources.DiscoveryResource;
import uk.ac.westminster.cosc022w.smartcampus.api.resources.RoomResource;
import uk.ac.westminster.cosc022w.smartcampus.api.resources.SensorResource;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(SmartCampusApplication.class.getName());

    @Override
    public Set<Class<?>> getClasses() {
        LOGGER.info("Registering Smart Campus API resources/providers");
        Set<Class<?>> classes = new HashSet<>();

        // Resources
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);

        // Providers (Exception mappers + filters)
        classes.add(RoomNotEmptyMapper.class);
        classes.add(LinkedResourceNotFoundMapper.class);
        classes.add(SensorUnavailableMapper.class);
        classes.add(WebApplicationExceptionMapper.class);
        classes.add(GenericThrowableMapper.class);
        classes.add(ApiLoggingFilter.class);

        return classes;
    }
}
