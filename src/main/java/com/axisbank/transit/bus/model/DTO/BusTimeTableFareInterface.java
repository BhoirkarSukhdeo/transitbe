package com.axisbank.transit.bus.model.DTO;

public interface BusTimeTableFareInterface {
    long getTrip_Number();
    String getTrip_Type();
    String getSource_Display_Name();
    double getSource_Latitude();
    double getSource_Longitude();
    String getSource_Arival();
    String getDestination_Display_Name();
    double getDestination_Latitude();
    double getDestination_Longitude();
    String getDestination_Arival();
    double getfare();
    String getRoute_Name();
    String getBus_Type();
}
