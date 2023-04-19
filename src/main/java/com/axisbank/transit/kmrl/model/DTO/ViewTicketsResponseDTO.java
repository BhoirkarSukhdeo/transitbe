package com.axisbank.transit.kmrl.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ViewTicketsResponseDTO {

    @JsonProperty("live")
    private LiveTicketsDTO liveTicketsDTO;

    @JsonProperty("upcoming")
    private List<TicketDetailsDTO> upcomingTicketsList = new ArrayList<>();

    @JsonProperty("completed")
    private List<TicketDetailsDTO> completedTicketsList = new ArrayList<>();

    @JsonProperty("cancelled")
    private List<TicketDetailsDTO> cancelledTicketsList = new ArrayList<>();

    public LiveTicketsDTO getLiveTicketsDTO() {
        return liveTicketsDTO;
    }

    public void setLiveTicketsDTO(LiveTicketsDTO liveTicketsDTO) {
        this.liveTicketsDTO = liveTicketsDTO;
    }

    public List<TicketDetailsDTO> getUpcomingTicketsList() {
        return upcomingTicketsList;
    }

    public void setUpcomingTicketsList(List<TicketDetailsDTO> upcomingTicketsList) {
        this.upcomingTicketsList = upcomingTicketsList;
    }

    public List<TicketDetailsDTO> getCompletedTicketsList() {
        return completedTicketsList;
    }

    public void setCompletedTicketsList(List<TicketDetailsDTO> completedTicketsList) {
        this.completedTicketsList = completedTicketsList;
    }

    public List<TicketDetailsDTO> getCancelledTicketsList() {
        return cancelledTicketsList;
    }

    public void setCancelledTicketsList(List<TicketDetailsDTO> cancelledTicketsList) {
        this.cancelledTicketsList = cancelledTicketsList;
    }
}
