package com.axisbank.transit.kmrl.service.impl;

import com.axisbank.transit.kmrl.model.DAO.MetroLine;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DAO.MetroTimeTable;
import com.axisbank.transit.kmrl.model.DTO.MetroLineAdminDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroStationAdminDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroStationMappedDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroTimeTableAdminDTO;
import com.axisbank.transit.kmrl.repository.MetroLineRepository;
import com.axisbank.transit.kmrl.repository.MetroStationRepository;
import com.axisbank.transit.kmrl.repository.MetroTimeTableRepository;
import com.axisbank.transit.kmrl.service.KmrlAdminService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KmrlAdminServiceImpl implements KmrlAdminService {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    MetroStationRepository metroStationRepository;

    @Autowired
    MetroLineRepository metroLineRepository;

    @Autowired
    MetroTimeTableRepository metroTimeTableRepository;

    @Override
    public List<MetroStationAdminDTO> getAllStations(int page, int size, String displayName) throws Exception {
        log.info("Request received inside getAllStations: ");
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        try {
            List<MetroStation> metroStations = metroStationRepository.findAllByDisplayNameContainingIgnoreCase(displayName, paging);

            List<MetroStationAdminDTO> metroStationAdminDTOS = new ArrayList<>();
            for (MetroStation metroStation: metroStations) {
                MetroStationAdminDTO metroStationAdminDTO = modelMapper.map(metroStation, MetroStationAdminDTO.class);
                metroStationAdminDTO.setMetroLine(metroStation.getMetroLine().stream().map(MetroLine::getLineCode).collect(Collectors.toList()));
                metroStationAdminDTOS.add(metroStationAdminDTO);
            }
            return metroStationAdminDTOS;
        } catch (Exception exception) {
            log.error("Error in getting Stations: {}", exception.getMessage());
            throw new Exception("Error in getting Stations, Please try again later.");
        }
    }

    @Override
    public List<MetroLineAdminDTO> getAllRoutes(int page, int size, String searchParam) throws Exception {
        log.info("Request received inside getAllRoutes: ");
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        try {
            List<MetroLine> metroLines = metroLineRepository.findAllByDisplayNameContainingIgnoreCaseOrLineCodeContainingIgnoreCase(searchParam, searchParam, paging);
            List<MetroLineAdminDTO> metroLineAdminDTOS = new ArrayList<>();
            for (MetroLine metroLine: metroLines) {
                List<MetroStationMappedDTO> mappedStations = new ArrayList<>();
                MetroLineAdminDTO metroLineAdminDTO = modelMapper.map(metroLine, MetroLineAdminDTO.class);
                for (MetroStation metroStation: metroLine.getStations()) {
                    MetroStationMappedDTO metroStationMappedDTO = modelMapper.map(metroStation, MetroStationMappedDTO.class);
                    mappedStations.add(metroStationMappedDTO);
                }
                metroLineAdminDTO.setStations(mappedStations);
                metroLineAdminDTOS.add(metroLineAdminDTO);
            }
            return metroLineAdminDTOS;
        } catch (Exception exception) {
            log.error("Error in getting routes: {}", exception.getMessage());
            throw new Exception("Error in getting Routes, Please try again later.");
        }
    }

    @Override
    public List<MetroTimeTableAdminDTO> getAllTimetable(int page, int size, String searchParam) throws Exception {
        log.info("Request received inside getAllTimetable: ");
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        try {
            List<MetroTimeTable> metroTimeTables = metroTimeTableRepository.findAllByStationNameContainingIgnoreCaseOrMetroLineNameContainingIgnoreCase(searchParam, searchParam, paging);
            List<MetroTimeTableAdminDTO> metroTimeTableAdminDTOS = new ArrayList<>();
            for (MetroTimeTable metroTimeTable: metroTimeTables) {
                MetroTimeTableAdminDTO metroTimeTableAdminDTO = modelMapper.map(metroTimeTable, MetroTimeTableAdminDTO.class);
                metroTimeTableAdminDTO.setDirection(metroTimeTable.getTrip().getDirection());
                metroTimeTableAdminDTO.setMetroTripNum(metroTimeTable.getTrip().getTripNumber());
                metroTimeTableAdminDTO.setTotalDistance(metroTimeTable.getTrip().getTotalDistance());
                metroTimeTableAdminDTOS.add(metroTimeTableAdminDTO);
            }
            return metroTimeTableAdminDTOS;
        } catch (Exception exception) {
            log.error("Error in getting Timetable: {}", exception.getMessage());
            throw new Exception("Error in getting Timetable, Please try again later.");
        }
    }

    @Override
    public void updateStation(MetroStationAdminDTO metroStationAdminDTO) throws Exception {
        log.info("Request received inside updateStation: ");
        try {
            MetroStation metroStation = metroStationRepository.findByStationId(metroStationAdminDTO.getStationId());
            if (metroStation == null) {
                log.error("Station Id not exists.");
                throw new Exception("Metro Station not present, Please provide valid station Id");
            }
            metroStation.setDisplayName(metroStationAdminDTO.getDisplayName());
            metroStation.setLatitude(metroStationAdminDTO.getLatitude());
            metroStation.setLongitude(metroStationAdminDTO.getLongitude());
            metroStationRepository.save(metroStation);
        } catch (Exception exception) {
            log.error("Error in updating station coordinates: {}", exception.getMessage());
            throw new Exception("Error in updating station coordinates, Please try again later");
        }
    }

    @Override
    public void updateRoute(MetroLineAdminDTO metroLineAdminDTO) throws Exception {
        log.info("Request received inside updateRoute: ");
        try {
            MetroLine metroLine = metroLineRepository.findByLineId(metroLineAdminDTO.getLineId());
            if (metroLine == null) {
                log.error("Line Id not exists.");
                throw new Exception("Metro Line not present, Please provide valid Line Id");
            }
            metroLine.setDisplayName(metroLineAdminDTO.getDisplayName());
            metroLineRepository.save(metroLine);
        } catch (Exception exception) {
            log.error("Error in updating metro Line: {}", exception.getMessage());
            throw new Exception("Error in metro Line, Please try again later");
        }
    }
}
