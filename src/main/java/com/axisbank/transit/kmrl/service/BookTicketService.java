package com.axisbank.transit.kmrl.service;

import com.axisbank.transit.kmrl.model.DTO.*;

public interface BookTicketService {
    public GetFareResponseDTO getFare(String sourceStation, String destinationStation, String ticketType) throws Exception;
    public BookTicketResponseDTO bookTicket(BookTicketRequestDTO bookTicketRequestDTO) throws Exception;
    ViewTicketsResponseDTO getTickets(int pageNo,int pageSize, String status) throws Exception;
    TicketDetailsDTO getTicketDetails(String ticketId) throws Exception;
    String getTicketFare(String sourceStationCode, String destinationStationCode) throws Exception;
    String getTicketFare(String sourceStationCode, String destinationStationCode, boolean fromCache) throws Exception;
    String getTicketFare(String sourceStationCode, String destinationStationCode, String ticketType, boolean fromCache) throws Exception;
    String getRefundValue(String ticketId) throws Exception;
    String refundTicket(String ticketId) throws Exception;
}
