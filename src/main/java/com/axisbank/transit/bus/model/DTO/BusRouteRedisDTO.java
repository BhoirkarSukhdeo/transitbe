package com.axisbank.transit.bus.model.DTO;

import java.util.List;

public class BusRouteRedisDTO {
    String source;
    String destination;
    List<BusSrcDestRouteDTO> busSrcDestRouteDTOList;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<BusSrcDestRouteDTO> getBusSrcDestRouteDTOList() {
        return busSrcDestRouteDTOList;
    }

    public void setBusSrcDestRouteDTOList(List<BusSrcDestRouteDTO> busSrcDestRouteDTOList) {
        this.busSrcDestRouteDTOList = busSrcDestRouteDTOList;
    }
}
