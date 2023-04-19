package com.axisbank.transit.bus.service.impl;

import com.axisbank.transit.bus.model.DAO.*;
import com.axisbank.transit.bus.model.DTO.*;
import com.axisbank.transit.bus.repository.*;
import com.axisbank.transit.bus.service.BusAdminService;
import com.axisbank.transit.core.shared.utils.WorkFlowUtil;
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
public class BusAdminServiceImpl implements BusAdminService {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    BusRouteRepository busRouteRepository;

    @Autowired
    BusStationRepository busStationRepository;

    @Autowired
    BusFareRepository busFareRepository;

    @Autowired
    BusTimeTableRepository busTimeTableRepository;
    @Autowired
    BusTimetableTypeRepository busTimetableTypeRepository;
    @Autowired
    BusFareTypeRepository busFareTypeRepository;
    @Autowired
    BusTimeTableVerRepository busTimeTableVerRepository;
    @Autowired
    BusFareVerRepository busFareVerRepository;

    @Override
    public List<BusStationAdminDTO> getAllStations(int page, int size, String searchParam) throws Exception {
        log.info("Request received inside getAllStations: ");
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        try {
            List<BusStation> busStations = busStationRepository.findAllByDisplayNameContainingIgnoreCase(searchParam, paging);

            List<BusStationAdminDTO> busStationDTOS = new ArrayList<>();
            for (BusStation busStation: busStations) {
                BusStationAdminDTO busStationDTO = modelMapper.map(busStation, BusStationAdminDTO.class);
                busStationDTO.setRoutes(busStation.getRouteSet().stream().map(BusRoute::getRouteCode).collect(Collectors.toList()));
                busStationDTOS.add(busStationDTO);
            }
            return busStationDTOS;
        } catch (Exception exception) {
            log.error("Error in getting Stations: {}", exception.getMessage());
            throw new Exception("Error in getting Stations, Please try again later.");
        }
    }

    @Override
    public List<BusRouteAdminDTO> getAllRoutes(int page, int size, String searchParam) throws Exception {
        log.info("Request received inside getAllRoutes: ");
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        try {
            List<BusRoute> busRoutes = busRouteRepository.findAllByRouteCodeContainingIgnoreCaseOrVehicleNumberContainingIgnoreCaseOrBusTypeContainingIgnoreCase(searchParam, searchParam, searchParam, paging);
            List<BusRouteAdminDTO> busRouteAdminDTOS = new ArrayList<>();
            for (BusRoute busRoute: busRoutes) {
                List<BusStationMappedDTO> mappedStations = new ArrayList<>();
                BusRouteAdminDTO busRouteAdminDTO = modelMapper.map(busRoute, BusRouteAdminDTO.class);
                for (BusStation busStation: busRoute.getBusStationSet()) {
                    BusStationMappedDTO busStationMappedDTO = modelMapper.map(busStation, BusStationMappedDTO.class);
                    mappedStations.add(busStationMappedDTO);
                }
                busRouteAdminDTO.setStations(mappedStations);
                busRouteAdminDTOS.add(busRouteAdminDTO);
            }
            return busRouteAdminDTOS;
        } catch (Exception exception) {
            log.error("Error in getting routes: {}", exception.getMessage());
            throw new Exception("Error in getting Routes, Please try again later.");
        }
    }

    @Override
    public List<BusFareAdminDTO> getAllFares(int page, int size, String searchParam) throws Exception{
        log.info("Request received inside getAllFares: ");
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        try {
            List<BusFare> busFares = busFareRepository.findAllByBusRoute_RouteCodeContainingIgnoreCaseOrFromBusStation_DisplayNameContainingIgnoreCaseOrToBusStation_DisplayNameContainingIgnoreCase(searchParam, searchParam, searchParam, paging);
            List<BusFareAdminDTO> busFareAdminDTOS = new ArrayList<>();
            for (BusFare busFare: busFares) {
                BusFareAdminDTO busFareAdminDTO = modelMapper.map(busFare, BusFareAdminDTO.class);
                busFareAdminDTO.setRouteCode(busFare.getBusRoute().getRouteCode());
                busFareAdminDTO.setFromBusStationName(busFare.getFromBusStation().getDisplayName());
                busFareAdminDTO.setToBusStationName(busFare.getToBusStation().getDisplayName());
                busFareAdminDTOS.add(busFareAdminDTO);
            }
            return busFareAdminDTOS;
        } catch (Exception exception) {
            log.error("Error in getting Fare list: {}", exception.getMessage());
            throw new Exception("Error in getting Fare List, Please try again later.");
        }
    }

    @Override
    public List<BusTimetableAdminDTO> getAllTimetable(int page, int size, String searchParam) throws Exception {
        log.info("Request received inside getAllTimetable: ");
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        try {
            List<BusTimeTable> busTimeTables = busTimeTableRepository.findAllByBusRoute_RouteCodeContainingIgnoreCaseOrBusStation_DisplayNameContainingIgnoreCase(searchParam, searchParam, paging);;
            List<BusTimetableAdminDTO> busTimetableAdminDTOS = new ArrayList<>();
            for (BusTimeTable busTimeTable: busTimeTables) {
                BusTimetableAdminDTO busTimetableAdminDTO = modelMapper.map(busTimeTable, BusTimetableAdminDTO.class);
                busTimetableAdminDTO.setRouteCode(busTimeTable.getBusRoute().getRouteCode());
                busTimetableAdminDTO.setBusStationName(busTimeTable.getBusStation().getDisplayName());
                busTimetableAdminDTOS.add(busTimetableAdminDTO);
            }
            return busTimetableAdminDTOS;
        } catch (Exception exception) {
            log.error("Error in getting Timetable: {}", exception.getMessage());
            throw new Exception("Error in getting Timetable, Please try again later.");
        }
    }

    @Override
    public void updateStation(BusStationDTO busStationDTO) throws Exception {
        log.info("Request received inside updateStation: ");
        try {
            BusStation busStation = busStationRepository.findByStationId(busStationDTO.getStationId());
            if (busStation == null) {
                log.error("Station Id not exists.");
                throw new Exception("Bus Station not present, Please provide valid station Id");
            }
            busStation.setDisplayName(busStationDTO.getDisplayName());
            busStation.setLatitude(busStationDTO.getLatitude());
            busStation.setLongitude(busStationDTO.getLongitude());
            busStationRepository.save(busStation);
        } catch (Exception exception) {
            log.error("Error in updating station coordinates: {}", exception.getMessage());
            throw new Exception("Error in updating station coordinates, Please try again later");
        }
    }

    @Override
    public void updateFare(BusFareUpdateDTO busFareUpdateDTO) throws Exception {
        log.info("Request received inside updateFare: ");
        try {
            BusFare busFare = busFareRepository.findByFareId(busFareUpdateDTO.getFareId());
            if (busFare == null) {
                log.error("Fare Id not exists.");
                throw new Exception("Bus Fare not present, Please provide valid Fare Id");
            }
            busFare.setFare(busFareUpdateDTO.getFare());
            busFareRepository.save(busFare);
        } catch (Exception exception) {
            log.error("Error in updating bus fare: {}", exception.getMessage());
            throw new Exception("Error in updating Bus Fare, Please try again later");
        }
    }

    @Override
    public void updateTimetable(BusTimeTableUpdateDTO busTimeTableUpdateDTO) throws Exception {
        log.info("Request received inside updateTimetable: ");
        try {
            BusTimeTable busTimeTable = busTimeTableRepository.findByTimeTableId(busTimeTableUpdateDTO.getTimeTableId());
            if (busTimeTable == null) {
                log.error("Timetable Id not exists.");
                throw new Exception("Bus Timetable not present, Please provide valid timetable Id");
            }
            busTimeTable.setArivalTime(busTimeTableUpdateDTO.getArivalTime());
            busTimeTable.setDepartureTime(busTimeTableUpdateDTO.getDepartureTime());
            busTimeTableRepository.save(busTimeTable);
        } catch (Exception exception) {
            log.error("Error in updating bus timetable: {}", exception.getMessage());
            throw new Exception("Error in updating Bus Timetable, Please try again later");
        }
    }



    @Override
    public List<BusTimeTableTypesDTO> getAllBusTimetables() {
        List<BusTimeTableType> timeTableTypes = busTimetableTypeRepository.findAll();
        List<BusTimeTableTypesDTO> timeTableTypesDTOS = new ArrayList<>();
        for(BusTimeTableType busTimeTableType: timeTableTypes){
            timeTableTypesDTOS.add(modelMapper.map(busTimeTableType,BusTimeTableTypesDTO.class));
        }
        return timeTableTypesDTOS;
    }

    @Override
    public List<BusTimetableAdminDTO> getBusTimetableByType(String timetabletypeId, int page, int size, String searchParam) throws Exception {
        log.info("Request received inside getAllTimetable: ");
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        try {
            List<BusTimeTableVer> busTimeTableVers = busTimeTableVerRepository.findAllByTimeTableType_BusTimetableIdAndBusRoute_RouteCodeContainingIgnoreCaseOrBusStation_DisplayNameContainingIgnoreCase(timetabletypeId, searchParam, searchParam, paging);
            List<BusTimetableAdminDTO> busTimetableAdminDTOS = new ArrayList<>();
            for (BusTimeTableVer busTimeTable: busTimeTableVers) {
                BusTimetableAdminDTO busTimetableAdminDTO = modelMapper.map(busTimeTable, BusTimetableAdminDTO.class);
                busTimetableAdminDTO.setRouteCode(busTimeTable.getBusRoute().getRouteCode());
                busTimetableAdminDTO.setBusStationName(busTimeTable.getBusStation().getDisplayName());
                busTimetableAdminDTOS.add(busTimetableAdminDTO);
            }
            return busTimetableAdminDTOS;
        } catch (Exception exception) {
            log.error("Error in getting Timetable: {}", exception.getMessage());
            throw new Exception("Error in getting Timetable, Please try again later.");
        }
    }

    @Override
    public void updateBusTimeTableStatus(String timetabletypeId, String status) throws Exception {
        BusTimeTableType timeTableType = busTimetableTypeRepository.findByBusTimetableId(timetabletypeId);
        if (timeTableType==null)
            throw new Exception("Failed to fetch timetable got given TypeId");
        String currStatus= timeTableType.getCurrentStatus();
        if(WorkFlowUtil.verifyWorkFlow(currStatus, status)){
            timeTableType.setCurrentStatus(status);
            busTimetableTypeRepository.save(timeTableType);
            return;
        }
        log.info("Failed to change state of timetable as give status doesn't match workflow");
        throw new Exception("Invalid State change request");
    }

    @Override
    public List<BusFareTypesDTO> getAllBusFares() {
        List<BusFareType> busFareTypes = busFareTypeRepository.findAll();
        List<BusFareTypesDTO> busFareTypesDTOS = new ArrayList<>();
        for(BusFareType busFareType: busFareTypes){
            busFareTypesDTOS.add(modelMapper.map(busFareType,BusFareTypesDTO.class));
        }
        return busFareTypesDTOS;
    }

    @Override
    public List<BusFareAdminDTO> getBusFareByType(String fareTypeId, int page, int size, String searchParam) throws Exception {
        log.info("Request received inside getAllFares: ");
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        try {
            List<BusFareVer> busFares = busFareVerRepository.findAllByBusFareType_BusFareTypeIdAndBusRoute_RouteCodeContainingIgnoreCaseOrFromBusStation_DisplayNameContainingIgnoreCaseOrToBusStation_DisplayNameContainingIgnoreCase(fareTypeId, searchParam, searchParam, searchParam, paging);
            List<BusFareAdminDTO> busFareAdminDTOS = new ArrayList<>();
            for (BusFareVer busFare: busFares) {
                BusFareAdminDTO busFareAdminDTO = modelMapper.map(busFare, BusFareAdminDTO.class);
                busFareAdminDTO.setRouteCode(busFare.getBusRoute().getRouteCode());
                busFareAdminDTO.setFromBusStationName(busFare.getFromBusStation().getDisplayName());
                busFareAdminDTO.setToBusStationName(busFare.getToBusStation().getDisplayName());
                busFareAdminDTOS.add(busFareAdminDTO);
            }
            return busFareAdminDTOS;
        } catch (Exception exception) {
            log.error("Error in getting Fare list: {}", exception.getMessage());
            throw new Exception("Error in getting Fare List, Please try again later.");
        }
    }

    @Override
    public void updateFareStatus(String fareTypeId, String status) throws Exception {
        BusFareType busFareType = busFareTypeRepository.findByBusFareTypeId(fareTypeId);
        if (busFareType==null)
            throw new Exception("Failed to fetch busFare Type from given TypeId");
        String currStatus= busFareType.getCurrentStatus();
        if(WorkFlowUtil.verifyWorkFlow(currStatus, status)){
            busFareType.setCurrentStatus(status);
            busFareTypeRepository.save(busFareType);
            return;
        }
        log.info("Failed to change state of busFareType as give status doesn't match workflow");
        throw new Exception("Invalid State change request");
    }

}
