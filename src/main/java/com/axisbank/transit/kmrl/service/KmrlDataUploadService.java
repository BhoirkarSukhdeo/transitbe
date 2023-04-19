package com.axisbank.transit.kmrl.service;

import com.axisbank.transit.core.model.DTO.UploadDataStatus;
import org.apache.poi.ss.usermodel.Workbook;

import java.text.ParseException;

public interface KmrlDataUploadService {
    void uploadStations(Workbook workbook, String sheetName) throws Exception;

    void uploadRoutes(Workbook workbook, String sheetName)  throws Exception;

    void uploadStationRoutes(Workbook workbook, String sheetName)  throws Exception;

    UploadDataStatus getUploadStatus();

    void processXmlTimetable (String xmlString, String timeTableName, String activeDays) throws ParseException;

    void enableTimetable(String timetableTypeId) throws Exception;
}
