package uk.ac.westminster.cosc022w.smartcampus.api.models;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DiscoveryResponse {
    private static final Logger LOGGER = Logger.getLogger(DiscoveryResponse.class.getName());

    private String apiName;
    private String apiVersion;
    private String contactName;
    private String contactEmail;
    private Map<String, String> resources = new LinkedHashMap<>();

    public DiscoveryResponse() {
        // JSON-B needs a no-arg constructor
    }

    public DiscoveryResponse(String apiName, String apiVersion, String contactName, String contactEmail) {
        this.apiName = apiName;
        this.apiVersion = apiVersion;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        LOGGER.fine("Created DiscoveryResponse");
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Map<String, String> getResources() {
        return resources;
    }

    public void setResources(Map<String, String> resources) {
        this.resources = (resources == null) ? new LinkedHashMap<>() : resources;
    }
}
