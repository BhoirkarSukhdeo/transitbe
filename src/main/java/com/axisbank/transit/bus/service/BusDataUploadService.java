package com.axisbank.transit.bus.service;

import com.axisbank.transit.core.model.DTO.UploadDataStatus;
import org.apache.poi.ss.usermodel.Workbook;

public interface BusDataUploadService {
    void uploadStations(Workbook workbook, String sheetName) throws Exception;

    void uploadBusRoutes(Workbook workbook, String sheetName) throws Exception;

    void uploadBusStationRoutes(Workbook workbook, String sheetName) throws Exception;

    void uploadBusFares(Workbook workbook, String sheetName, String name, String activeDays) throws Exception;

    void uploadBusTimetable(Workbook workbook, String sheetName, String name, String activeDays) throws Exception;
    UploadDataStatus getUploadStatus();
    void enableBusTimetable(String timetableTypeId) throws Exception;
    void enableFare(String fareTypeId) throws Exception;
}
