package com.axisbank.transit.kmrl.service;

import com.axisbank.transit.kmrl.model.DTO.MetroLineAdminDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroStationAdminDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroTimeTableAdminDTO;

import java.util.List;

public interface KmrlAdminService {
    List<MetroStationAdminDTO> getAllStations(int page, int size, String displayName) throws Exception;

    List<MetroLineAdminDTO> getAllRoutes(int page, int size, String searchParam) throws Exception;

    List<MetroTimeTableAdminDTO> getAllTimetable(int page, int size, String searchParam) throws Exception;

    void updateStation(MetroStationAdminDTO metroStationAdminDTO) throws Exception;

    void updateRoute(MetroLineAdminDTO metroLineAdminDTO) throws Exception;
}
