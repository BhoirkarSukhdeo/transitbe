package com.axisbank.transit.bus.service.impl;

import com.axisbank.transit.bus.model.DAO.BusFare;
import com.axisbank.transit.bus.model.DAO.BusRoute;
import com.axisbank.transit.bus.model.DAO.BusStation;
import com.axisbank.transit.bus.model.DTO.BusFareChartDTO;
import com.axisbank.transit.bus.model.DTO.StationFareDTO;
import com.axisbank.transit.bus.model.DTO.StationFareListDTO;
import com.axisbank.transit.bus.repository.BusFareRepository;
import com.axisbank.transit.bus.repository.BusRouteRepository;
import com.axisbank.transit.bus.service.BusFareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class BusFareServiceImpl implements BusFareService {

    @Autowired
    private BusRouteRepository busRouteRepository;

    @Autowired
    private BusFareRepository busFareRepository;

    @Override
    public List<BusFareChartDTO> getAllRouteFares(int page, int size) throws Exception {
        Pageable sortedByName = PageRequest.of(page, size);
        List<Long> ids = busFareRepository.findDistinctRouteIds(sortedByName);
        List<BusRoute> busRoutes = busRouteRepository.findByIdIn(ids);
        List<BusFareChartDTO> busFareChartDTOList = new ArrayList<>();
        for (BusRoute busRoute: busRoutes) {
            Map<String, SortedSet<StationFareDTO>> busFareChartMap = new TreeMap<>();
            List<StationFareListDTO> stationFareListDTOList = new ArrayList<>();
            BusFareChartDTO busFareChartDTO = new BusFareChartDTO();
            busFareChartDTO.setRouteName(busRoute.getRouteName());
            Set<BusFare> busFareSet = busRoute.getBusFareSet();
            for (BusFare busFare : busFareSet) {
                BusStation fromStation = busFare.getFromBusStation();
                BusStation toStation = busFare.getToBusStation();
                addInHashedMap(fromStation.getStationCode(), toStation.getStationCode(), busFare.getFare(), busFareChartMap);
            }
            for (Map.Entry<String, SortedSet<StationFareDTO>> set : busFareChartMap.entrySet()) {
                StationFareListDTO stationFareListDTO = new StationFareListDTO();
                stationFareListDTO.setPrimaryStationName(set.getKey());
                stationFareListDTO.setFareChart(new ArrayList<>(set.getValue()));
                stationFareListDTOList.add(stationFareListDTO);
            }
            if(stationFareListDTOList.isEmpty())
                continue;
            busFareChartDTO.setStationList(stationFareListDTOList);
            busFareChartDTOList.add(busFareChartDTO);
        }
        return busFareChartDTOList;
    }

    private void addInHashedMap(String fromStation, String toSatation, double fare, Map<String, SortedSet<StationFareDTO>> busFareChartMap) {
        SortedSet<StationFareDTO> stationFareDTOList = null;
        if (busFareChartMap.containsKey(fromStation)) {
            stationFareDTOList = busFareChartMap.get(fromStation);
        } else {
            stationFareDTOList = new TreeSet<>(Comparator.comparing(StationFareDTO::getStationName));
        }
        StationFareDTO stationFareDTO = new StationFareDTO();
        stationFareDTO.setStationName(toSatation);
        stationFareDTO.setFare(fare);
        stationFareDTOList.add(stationFareDTO);
        busFareChartMap.put(fromStation, stationFareDTOList);
    }
}
