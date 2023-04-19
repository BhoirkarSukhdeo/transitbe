package com.axisbank.transit.journey.services;

import com.axisbank.transit.bus.model.DTO.BusSrcDestRouteDTO;
import com.axisbank.transit.journey.model.DTO.JourneyPlannerRouteDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.text.ParseException;
import java.util.List;


public interface JourneyService {
    List<JourneyPlannerRouteDTO> getRoutes(String source, String destination, String journeyType) throws ParseException;
    List<JourneyPlannerRouteDTO> getRoutes(String source, String destination, String startTime, String journeyType) throws ParseException;
    String setBusRoutesGraph() throws JsonProcessingException;
    String setMetroRoutesGraph() throws JsonProcessingException;
    String setSuggestedGraph() throws JsonProcessingException;
    List<BusSrcDestRouteDTO> getSuggestedRoutesGraph(String sourceStationId, String destinationStationId) throws JsonProcessingException;
    Double getNearByStationRadiusGC();
    Double getMaxWalkDistGC();
}
