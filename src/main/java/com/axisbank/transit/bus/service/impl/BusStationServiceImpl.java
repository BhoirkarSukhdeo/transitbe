package com.axisbank.transit.bus.service.impl;

import com.axisbank.transit.bus.model.DAO.BusRoute;
import com.axisbank.transit.bus.model.DAO.BusStation;
import com.axisbank.transit.bus.model.DTO.GetBusStationDTO;
import com.axisbank.transit.bus.model.DTO.NearByBusStationsDTO;
import com.axisbank.transit.bus.model.DTO.NearByBusStationsDTOInterface;
import com.axisbank.transit.bus.repository.BusRouteRepository;
import com.axisbank.transit.bus.repository.BusStationRepository;
import com.axisbank.transit.bus.service.BusStationService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.journey.model.DTO.CoordinatesDto;
import com.axisbank.transit.journey.services.JourneyService;
import com.axisbank.transit.journey.utils.OSRMUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.axisbank.transit.journey.utils.JourneyUtils.getDurationFromDistance;

@Slf4j
@Service
public class BusStationServiceImpl implements BusStationService {
    private final ModelMapper modelMapper = new ModelMapper();
    @Autowired
    BusStationRepository busStationRepository;
    @Autowired
    BusRouteRepository busRouteRepository;
    @Autowired
    OSRMUtils osrmUtils;
    @Autowired
    JourneyService journeyService;

    @Override
    public List<NearByBusStationsDTO> getNearbyStations(double latitude, double longitude, long radius) {
        String distanceParam = "distance ="+radius;
        List<NearByBusStationsDTOInterface> nearByStationsDTOS =  busStationRepository.findAllStationsByLatLong(latitude,
                longitude, distanceParam);
        List<NearByBusStationsDTO> listNbs = new ArrayList<>();
        CoordinatesDto source = new CoordinatesDto();
        source.setLatitude(latitude);
        source.setLongitude(longitude);
        List<CoordinatesDto> destinations = new ArrayList<>();
        for(NearByBusStationsDTOInterface ns:nearByStationsDTOS){
            NearByBusStationsDTO nbs = modelMapper.map(ns, NearByBusStationsDTO.class);
            nbs.setStationId(ns.getStation_Id());
            nbs.setDisplayName(ns.getDisplay_Name());
            listNbs.add(nbs);
            CoordinatesDto destination = new CoordinatesDto();
            destination.setLongitude(ns.getLongitude());
            destination.setLatitude(ns.getLatitude());
            destinations.add(destination);
        }
        List<Map<String,Double>> distances = osrmUtils.getOSRMDistance(source,destinations);
        if (distances.size()<1){
            return listNbs;
        }
        int i = 0;
        for (NearByBusStationsDTO ns: listNbs){
            ns.setOsrmDistance(distances.get(i).get("distance"));
            ns.setOsrmDuration(distances.get(i).get("duration"));
            ns.setDuration(getDurationFromDistance(distances.get(i).get("distance")));
            i++;
        }
        return listNbs;
    }


    @Override
    public List<NearByBusStationsDTO> getNearbyStations(double latitude, double longitude) {
        return getNearbyStations(latitude, longitude, journeyService.getNearByStationRadiusGC().longValue());
    }

    @Override
    public List<GetBusStationDTO> getStations(String sourceId, String busType) {
        log.info("Request received inside getStations: ");
        List<BusRoute> routes = busRouteRepository.findAllByBusType(busType);
        Set<BusStation> busStations = new HashSet<>();
        for (BusRoute route: routes){
            busStations.addAll(route.getBusStationSet());
        }
        List<GetBusStationDTO> busStationDTOS = new ArrayList<>();
        for (BusStation busStation: busStations) {
            GetBusStationDTO busStationDTO = modelMapper.map(busStation, GetBusStationDTO.class);
            if (!CommonUtils.isNullOrEmpty(sourceId) && busStationDTO.getStationId().equals(sourceId)){
                busStationDTO.setIsSource(true);
            }
            busStationDTOS.add(busStationDTO);
        }
        return busStationDTOS;
    }

    @Override
    public List<NearByBusStationsDTO> getNearByBusStationSQL(double latitude, double longitude, double radius){
        String distanceParam = "distance ="+radius;
        List<NearByBusStationsDTOInterface> nearByStationsDTOS =  busStationRepository.findAllStationsByLatLong(latitude,
                longitude, distanceParam);
        List<NearByBusStationsDTO> listNbs = new ArrayList<>();
        for(NearByBusStationsDTOInterface ns:nearByStationsDTOS){
            NearByBusStationsDTO nbs = modelMapper.map(ns, NearByBusStationsDTO.class);
            nbs.setStationId(ns.getStation_Id());
            nbs.setDisplayName(ns.getDisplay_Name());
            nbs.setDuration(getDurationFromDistance(ns.getSqlDist()));
            listNbs.add(nbs);
        }
        return listNbs;
    }
}
