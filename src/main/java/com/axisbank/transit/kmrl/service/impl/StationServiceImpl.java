package com.axisbank.transit.kmrl.service.impl;

import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.journey.model.DTO.CoordinatesDto;
import com.axisbank.transit.journey.services.JourneyService;
import com.axisbank.transit.journey.utils.OSRMUtils;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DTO.GetMetroStationDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroStationDTO;
import com.axisbank.transit.kmrl.model.DTO.NearByStationsDTO;
import com.axisbank.transit.kmrl.model.DTO.NearByStationsDTOInterface;
import com.axisbank.transit.kmrl.repository.MetroStationRepository;
import com.axisbank.transit.kmrl.service.StationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.axisbank.transit.journey.utils.JourneyUtils.getDurationFromDistance;

@Service
public class StationServiceImpl implements StationService {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    MetroStationRepository metroStationRepository;
    @Autowired
    JourneyService journeyService;

    @Autowired
    OSRMUtils osrmUtils;

    @Override
    public void saveStation(List<MetroStationDTO> stations) {
        List<MetroStation> metroStations = new ArrayList<>();
        for(MetroStationDTO stationDTO: stations){
            MetroStation metroStation = modelMapper.map(stationDTO, MetroStation.class);
            metroStation.setStationId(CommonUtils.generateRandomString(30));
            // TODO add metro line
            metroStations.add(metroStation);
        }
        metroStationRepository.saveAll(metroStations);
    }

    @Override
    public void saveStation(MetroStationDTO station) {
        MetroStation metroStation = modelMapper.map(station, MetroStation.class);
        metroStation.setStationId(CommonUtils.generateRandomString(30));
        // TODO add metro line
        metroStationRepository.save(metroStation);
    }

    @Override
    public List<GetMetroStationDTO> getStations() {
        return getStations("");
    }

    @Override
    public List<GetMetroStationDTO> getStations(String SourceId) {
        List<MetroStation> metroStations = metroStationRepository.findAll();
        List<GetMetroStationDTO> metroStationDTOS = new ArrayList<>();
        for (MetroStation metroStation: metroStations) {
            GetMetroStationDTO metroStationDTO = modelMapper.map(metroStation, GetMetroStationDTO.class);
            if (!CommonUtils.isNullOrEmpty(SourceId) && metroStationDTO.getStationId().equals(SourceId)){
                metroStationDTO.setIsSource(true);
            }
            metroStationDTOS.add(metroStationDTO);
        }
        return metroStationDTOS;
    }

    @Override
    public MetroStationDTO getStation(String stationId) {
        return modelMapper.map(getStationById(stationId), MetroStationDTO.class);
    }

    @Override
    public MetroStation getStationById(String stationId) {
        return metroStationRepository.findByStationId(stationId);
    }

    @Override
    public MetroStation getStationByStationCode(String stationCode) {
        return metroStationRepository.findByStationCodeUpOrStationCodeDn(stationCode, stationCode);
    }

    @Override
    public void saveMetroLine(Set<MetroStation> metroLine) {
        SortedSet <MetroStation> sortedStations = new TreeSet<>(
                Comparator.comparing(MetroStation::getDistance));
        sortedStations.addAll(metroLine);
        metroStationRepository.saveAll(sortedStations);
    }
    @Override
    public List<NearByStationsDTO> getNearbyStations(double latitude, double longitude, long radius) {
        String distanceParam = "distance ="+radius;
        List<NearByStationsDTOInterface> nearByStationsDTOS =  metroStationRepository.findAllStationsByLatLong(latitude,
                longitude, distanceParam);
        List<NearByStationsDTO> listNbs = new ArrayList<>();
        CoordinatesDto source = new CoordinatesDto();
        source.setLatitude(latitude);
        source.setLongitude(longitude);
        List<CoordinatesDto> destinations = new ArrayList<>();
        for(NearByStationsDTOInterface ns:nearByStationsDTOS){
            NearByStationsDTO nbs = modelMapper.map(ns, NearByStationsDTO.class);
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
        for (NearByStationsDTO ns: listNbs){
            Double dist = distances.get(i).get("distance");
            Double sqlDist = ns.getSqlDist();
            // +500 meters buffer from SQL distance
            if(sqlDist<500 && dist-sqlDist>1000){
                dist = sqlDist;
            }
            ns.setOsrmDistance(dist);
            ns.setOsrmDuration(distances.get(i).get("duration"));
            ns.setDuration(getDurationFromDistance(dist));
            i++;
        }
        return listNbs;
    }


    @Override
    public List<NearByStationsDTO> getNearbyStations(double latitude, double longitude) {
        return getNearbyStations(latitude, longitude, journeyService.getNearByStationRadiusGC().longValue());
    }

    @Override
    public MetroStation getMetroStationByKMRLCode(String code){
        return metroStationRepository.findByStationCode(code);
    }
}
