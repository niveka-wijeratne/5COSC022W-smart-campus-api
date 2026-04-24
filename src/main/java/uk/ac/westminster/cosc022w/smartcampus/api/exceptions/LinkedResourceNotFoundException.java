package uk.ac.westminster.cosc022w.smartcampus.api.exceptions;

import java.util.logging.Logger;

public class LinkedResourceNotFoundException extends RuntimeException {
    private static final Logger LOGGER = Logger.getLogger(LinkedResourceNotFoundException.class.getName());

    private final String field;
    private final String referencedId;

    public LinkedResourceNotFoundException(String field, String referencedId) {
        super("Linked resource not found: " + field + "='" + referencedId + "'");
        this.field = field;
        this.referencedId = referencedId;
        LOGGER.warning(() -> "LinkedResourceNotFoundException field=" + field + " referencedId=" + referencedId);
    }

    public String getField() {
        return field;
    }

    public String getReferencedId() {
        return referencedId;
    }
}
