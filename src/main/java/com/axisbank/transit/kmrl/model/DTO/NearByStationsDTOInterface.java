package com.axisbank.transit.kmrl.model.DTO;

public interface NearByStationsDTOInterface {
    String getDisplay_Name();
    String getStation_Id();
    double getLatitude();
    double getLongitude();
    Double getDistance();
    Double getSqlDist();
}
