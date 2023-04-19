package com.axisbank.transit.kmrl.service;

import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DTO.MetroRouteDetailsDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroTimeTableAdminDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroTimeTableTypesDTO;

import java.sql.Time;
import java.text.ParseException;
import java.util.List;

public interface TimeTableService {
    List<MetroTimeTableTypesDTO> getAllMetroTimetables();
    List<MetroTimeTableAdminDTO> getMetroTimetableByType(String timetabletypeId, int page, int size, String searchParam) throws Exception;
    void updateTimeTableStatus(String timetabletypeId, String status) throws Exception;

    List<MetroRouteDetailsDTO> getMetroDetails(String sourceStation, String destinationStation) throws ParseException;
    JourneyModeDetails getMetroRouteDetails(String sourceStation, String destinationStation,
                                                   String startTime);

    JourneyModeDetails getMetroRouteDetails(String sourceStation,
                                                           String destinationStation);
    List<Time> getUpcomingTimings(String sourceStation, String destinationStation, String startTime);
    List<Time> getUpcomingTimings(String sourceStation, String destinationStation);
    List<Time> getUpcomingDepartures(String sourceStation);
    MetroStation getMetroStation(String stationCode);
}
