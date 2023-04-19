package com.axisbank.transit.bus.model.DTO;

public interface NearByBusStationsDTOInterface {
    String getDisplay_Name();
    String getStation_Id();
    double getLatitude();
    double getLongitude();
    Double getSqlDist();
}
