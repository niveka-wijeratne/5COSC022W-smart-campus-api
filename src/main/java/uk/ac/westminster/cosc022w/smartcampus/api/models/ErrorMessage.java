package uk.ac.westminster.cosc022w.smartcampus.api.models;

import java.util.logging.Logger;

public class ErrorMessage {
    private static final Logger LOGGER = Logger.getLogger(ErrorMessage.class.getName());

    private int status;
    private String error;
    private String message;

    public ErrorMessage() {
        // JSON-B needs a no-arg constructor
    }

    public ErrorMessage(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        LOGGER.fine(() -> "Created ErrorMessage status=" + status + " error=" + error);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
