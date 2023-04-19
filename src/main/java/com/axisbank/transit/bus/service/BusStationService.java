package com.axisbank.transit.bus.service;

import com.axisbank.transit.bus.model.DTO.GetBusStationDTO;
import com.axisbank.transit.bus.model.DTO.NearByBusStationsDTO;

import java.util.List;

public interface BusStationService {
    List<NearByBusStationsDTO> getNearbyStations(double latitude, double longitude, long radius);
    List<NearByBusStationsDTO> getNearbyStations(double latitude, double longitude);
    List<NearByBusStationsDTO> getNearByBusStationSQL(double latitude, double longitude, double radius);
    List<GetBusStationDTO> getStations(String sourceId, String busType);
}
