package com.axisbank.transit.kmrl.service.impl;

import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.kmrl.model.DAO.MetroLine;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DTO.MetroLineDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroStationDTO;
import com.axisbank.transit.kmrl.repository.MetroLineRepository;
import com.axisbank.transit.kmrl.service.MetroLineService;
import com.axisbank.transit.kmrl.service.StationService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class MetroLineServiceImpl implements MetroLineService {
    private final ModelMapper modelMapper = new ModelMapper();
    @Autowired
    MetroLineRepository metroLineRepository;

    @Autowired
    StationService stationService;

    @Transactional
    @Override
    public void saveLine(MetroLineDTO metroLineDTO) {
        Set<MetroStationDTO> stations = metroLineDTO.getStation();
        MetroLine metroLine = modelMapper.map(metroLineDTO, MetroLine.class);
        Set<MetroStation> metroStations = metroLine.getStations();
        metroLine.setLineId(CommonUtils.generateRandomString(30));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        for(MetroStationDTO station: stations){
            MetroStation metroStation = modelMapper.map(station, MetroStation.class);
            metroStation.setStationId(CommonUtils.generateRandomString(30));
            metroStation.getMetroLine().add(metroLine);
            metroStations.add(metroStation);
        }
        metroLine.setStations(metroStations);
        stationService.saveMetroLine(metroStations);
    }
}
