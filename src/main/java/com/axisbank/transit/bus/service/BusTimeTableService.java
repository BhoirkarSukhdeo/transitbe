package com.axisbank.transit.bus.service;

import com.axisbank.transit.bus.model.DTO.BusDeparturesDTO;
import com.axisbank.transit.bus.model.DTO.BusSrcDestRouteDTO;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.Time;
import java.util.List;
import java.util.SortedSet;

public interface BusTimeTableService {
    List<BusDeparturesDTO> getUpcomingDepartures(String sourceStationId);
    List<BusSrcDestRouteDTO> getBusRoutesGraph(String sourceStationId, String destinationStationId) throws JsonProcessingException;
    SortedSet<JourneyModeDetails> getRouteDijktras(String sourceStationId, String destinationStationId);
    SortedSet<JourneyModeDetails> getRouteDijktras(String sourceStationId, String destinationStationId, String startTime);
    List<Time> getUpcomingTimings(String sourceStationId, String destinationStationId);
    List<Time> getUpcomingTimings(String sourceStation, String destinationStation, String startTime);
    JourneyModeDetails getJourneyDetailsByRouteCode(String sourceStationId, String destinationStationId,
                                                    String routeCode, String startTime);
}
