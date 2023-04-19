package com.axisbank.transit.bus.service;

import com.axisbank.transit.bus.model.DTO.*;

import java.util.List;

public interface BusAdminService {
    List<BusStationAdminDTO> getAllStations(int page, int size, String searchParam) throws Exception;
    List<BusRouteAdminDTO> getAllRoutes(int page, int size, String searchParam) throws Exception;
    List<BusFareAdminDTO> getAllFares(int page, int size, String searchParam) throws Exception;
    List<BusTimetableAdminDTO> getAllTimetable(int page, int size, String searchParam) throws Exception;

    void updateStation(BusStationDTO busStationDTO) throws Exception;
    void updateFare(BusFareUpdateDTO busFareUpdateDTO) throws Exception;
    void updateTimetable(BusTimeTableUpdateDTO busTimeTableUpdateDTO) throws Exception;

    List<BusTimeTableTypesDTO> getAllBusTimetables();
    List<BusTimetableAdminDTO> getBusTimetableByType(String timetableTypeId, int page, int size, String searchParam) throws Exception;
    void updateBusTimeTableStatus(String timetableTypeId, String status) throws Exception;

    List<BusFareTypesDTO> getAllBusFares();
    List<BusFareAdminDTO> getBusFareByType(String fareTypeId, int page, int size, String searchParam) throws Exception;
    void updateFareStatus(String fareTypeId, String status) throws Exception;
}
