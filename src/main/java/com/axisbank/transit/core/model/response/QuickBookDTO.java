package com.axisbank.transit.core.model.response;

public class QuickBookDTO {
    private String type;
    private String amount;
    private String typeOfPayment;
    private String methodOfPayment;
    private JourneyLocationDTO source;
    private JourneyLocationDTO destination;
    private String ticketType;
    private String ticketTypeDispName;

    public QuickBookDTO() {
    }

    public QuickBookDTO(String type, String amount, String typeOfPayment, String methodOfPayment, JourneyLocationDTO source, JourneyLocationDTO destination) {
        this.type = type;
        this.amount = amount;
        this.typeOfPayment = typeOfPayment;
        this.methodOfPayment = methodOfPayment;
        this.source = source;
        this.destination = destination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTypeOfPayment() {
        return typeOfPayment;
    }

    public void setTypeOfPayment(String typeOfPayment) {
        this.typeOfPayment = typeOfPayment;
    }

    public String getMethodOfPayment() {
        return methodOfPayment;
    }

    public void setMethodOfPayment(String methodOfPayment) {
        this.methodOfPayment = methodOfPayment;
    }

    public JourneyLocationDTO getSource() {
        return source;
    }

    public void setSource(JourneyLocationDTO source) {
        this.source = source;
    }

    public JourneyLocationDTO getDestination() {
        return destination;
    }

    public void setDestination(JourneyLocationDTO destination) {
        this.destination = destination;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketTypeDispName() {
        return ticketTypeDispName;
    }

    public void setTicketTypeDispName(String ticketTypeDispName) {
        this.ticketTypeDispName = ticketTypeDispName;
    }
}
