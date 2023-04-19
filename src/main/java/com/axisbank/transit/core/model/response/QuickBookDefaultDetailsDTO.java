package com.axisbank.transit.core.model.response;

public class QuickBookDefaultDetailsDTO {
    private String source;
    private String destination;
    private String type;

    public QuickBookDefaultDetailsDTO() {
    }

    public QuickBookDefaultDetailsDTO(String source, String destination, String type) {
        this.source = source;
        this.destination = destination;
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
