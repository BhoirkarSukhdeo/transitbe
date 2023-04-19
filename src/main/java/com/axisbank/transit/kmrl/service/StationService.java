package com.axisbank.transit.kmrl.service;

import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DTO.GetMetroStationDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroStationDTO;
import com.axisbank.transit.kmrl.model.DTO.NearByStationsDTO;

import java.util.List;
import java.util.Set;

public interface StationService {
    public void saveStation(List<MetroStationDTO> stations);
    public void saveStation(MetroStationDTO station);
    public List<GetMetroStationDTO> getStations();
    public List<GetMetroStationDTO> getStations(String SourceId);
    public MetroStationDTO getStation(String StationId);
    public MetroStation getStationById(String stationId);
    public MetroStation getStationByStationCode(String stationCode);
    public void  saveMetroLine(Set<MetroStation> metroLine);
    List<NearByStationsDTO> getNearbyStations(double latitude, double longitude);
    List<NearByStationsDTO> getNearbyStations(double latitude, double longitude, long radius);
    MetroStation getMetroStationByKMRLCode(String code);
}
