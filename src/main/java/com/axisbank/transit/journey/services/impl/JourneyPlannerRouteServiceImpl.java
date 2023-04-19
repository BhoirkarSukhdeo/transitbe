package com.axisbank.transit.journey.services.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.bus.model.DTO.BusDeparturesDTO;
import com.axisbank.transit.bus.service.BusTimeTableService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.journey.model.DAO.JourneyModeDetailsDAO;
import com.axisbank.transit.journey.model.DAO.JourneyPlannerRouteDAO;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetailsDTO;
import com.axisbank.transit.journey.model.DTO.JourneyPlannerConfirmedRouteDTO;
import com.axisbank.transit.journey.model.DTO.JourneyPlannerRouteDTO;
import com.axisbank.transit.journey.repository.JourneyModeDetailsRepository;
import com.axisbank.transit.journey.repository.JourneyPlannerRouteRepository;
import com.axisbank.transit.journey.services.JourneyPlannerRouteService;
import com.axisbank.transit.kmrl.service.TimeTableService;
import com.axisbank.transit.userDetails.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.*;

import static com.axisbank.transit.journey.constants.JourneyTypes.BUS;
import static com.axisbank.transit.journey.constants.JourneyTypes.METRO;

@Slf4j
@Service
@Transactional
public class JourneyPlannerRouteServiceImpl implements JourneyPlannerRouteService {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    UserUtil userUtil;

    @Autowired
    JourneyPlannerRouteRepository journeyPlannerRouteRepository;

    @Autowired
    TimeTableService timeTableService;

    @Autowired
    BusTimeTableService busTimeTableService;

    public void saveJourneyPlannerRoute(JourneyPlannerRouteDTO journeyPlannerRouteDTO) throws Exception{
        log.info("Request received in saveJourneyPlannerRoute method: "+journeyPlannerRouteDTO);
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            if (journeyPlannerRouteRepository.findByAuthenticationDAOAndIsActive(authenticationDAO, true) != null) {
                throw new Exception("There is already an Ongoing journey");
            }
            JourneyPlannerRouteDAO journeyPlannerRouteDAO = modelMapper.map(journeyPlannerRouteDTO, JourneyPlannerRouteDAO.class);
            journeyPlannerRouteDAO.setJourneyPlannerId(CommonUtils.generateRandomString(30));
            journeyPlannerRouteDAO.setAuthenticationDAO(authenticationDAO);
            List<JourneyModeDetailsDAO> journeyModeDetailsDAOList = new ArrayList<>();
            Set<JourneyPlannerRouteDAO> journeyPlannerRouteDAOSet = new HashSet<>();
            for (JourneyModeDetails journeyModeDetailsDTO : journeyPlannerRouteDTO.getJourneyModeDetails()) {
                JourneyModeDetailsDAO journeyModeDetailsDAO = new JourneyModeDetailsDAO();
                journeyModeDetailsDAO.setDistance(journeyModeDetailsDTO.getDistance());
                journeyModeDetailsDAO.setTime(journeyModeDetailsDTO.getTime());
                journeyModeDetailsDAO.setType(journeyModeDetailsDTO.getType());
                journeyModeDetailsDAO.setSource(journeyModeDetailsDTO.getSource());
                journeyModeDetailsDAO.setDestination(journeyModeDetailsDTO.getDestination());
                journeyModeDetailsDAO.setRoute(journeyModeDetailsDTO.getRoute());
                journeyModeDetailsDAO.setTravelTime(journeyModeDetailsDTO.getTravelTime());
                journeyModeDetailsDAO.setFare(journeyModeDetailsDTO.getFare());
                journeyModeDetailsDAO.setIntermediateStops(journeyModeDetailsDTO.getIntermediateStops());
                journeyModeDetailsDAO.setNoOfIntermediateStops(journeyModeDetailsDTO.getNoOfIntermediateStops());
                journeyModeDetailsDAO.setSourceLatitude(journeyModeDetailsDTO.getSourceLatitude());
                journeyModeDetailsDAO.setSourceLongitude(journeyModeDetailsDTO.getSourceLongitude());
                journeyModeDetailsDAO.setDestinationLatitude(journeyModeDetailsDTO.getDestinationLatitude());
                journeyModeDetailsDAO.setDestinationLongitude(journeyModeDetailsDTO.getDestinationLongitude());
                journeyModeDetailsDAO.setEstimatedArrivalTime(journeyModeDetailsDTO.getEstimatedArrivalTime());
                journeyModeDetailsDAO.setSourceId(journeyModeDetailsDTO.getSourceId());
                journeyModeDetailsDAO.setDestinationId(journeyModeDetailsDTO.getDestinationId());
                journeyModeDetailsDAO.setJourneyPlannerRouteDAO(journeyPlannerRouteDAO);
                journeyModeDetailsDAOList.add(journeyModeDetailsDAO);
            }
            journeyPlannerRouteDAO.setJourneyModeDetailsDAOList(journeyModeDetailsDAOList);
            journeyPlannerRouteDAOSet.add(journeyPlannerRouteDAO);
            authenticationDAO.setJourneyPlannerRouteDAOSet(journeyPlannerRouteDAOSet);
            journeyPlannerRouteDAO.setAuthenticationDAO(authenticationDAO);
            journeyPlannerRouteRepository.save(journeyPlannerRouteDAO);
        } catch (Exception exception) {
            log.error("Error in save journey planner route: {}", exception.getMessage());
            throw exception;
        }
    }

    public JourneyPlannerConfirmedRouteDTO getJourneyPlannerRoute() throws Exception {

        log.info("Request received in getJourneyPlannerRoute method");
        JourneyPlannerConfirmedRouteDTO journeyPlannerConfirmedRouteDTO = null;
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            JourneyPlannerRouteDAO journeyPlannerRouteDAO = journeyPlannerRouteRepository.findByAuthenticationDAOAndIsActive(authenticationDAO, true);
            if (journeyPlannerRouteDAO != null) {

                journeyPlannerConfirmedRouteDTO = new JourneyPlannerConfirmedRouteDTO();
                journeyPlannerConfirmedRouteDTO.setAmount(journeyPlannerRouteDAO.getAmount());
                journeyPlannerConfirmedRouteDTO.setArrivalTime(journeyPlannerRouteDAO.getArrivalTime());
                journeyPlannerConfirmedRouteDTO.setDepartureTime(journeyPlannerRouteDAO.getDepartureTime());
                journeyPlannerConfirmedRouteDTO.setTotalDistance(journeyPlannerRouteDAO.getTotalDistance());
                journeyPlannerConfirmedRouteDTO.setTotalDuration(journeyPlannerRouteDAO.getTotalDuration());

                List<JourneyModeDetailsDAO> journeyModeDetailsDAOList = journeyPlannerRouteDAO.getJourneyModeDetailsDAOList();
                SortedSet<JourneyModeDetailsDTO> journeyModeDetailsDTOSortedSet =
                        new TreeSet<>(Comparator.comparing(JourneyModeDetailsDTO::getTime));
                for (JourneyModeDetailsDAO journeyModeDetailsDAO : journeyModeDetailsDAOList) {
                    JourneyModeDetailsDTO journeyModeDetails = new JourneyModeDetailsDTO();

                    journeyModeDetails.setDistance(journeyModeDetailsDAO.getDistance());
                    journeyModeDetails.setTime(journeyModeDetailsDAO.getTime());
                    journeyModeDetails.setType(journeyModeDetailsDAO.getType());
                    journeyModeDetails.setSource(journeyModeDetailsDAO.getSource());
                    journeyModeDetails.setDestination(journeyModeDetailsDAO.getDestination());
                    journeyModeDetails.setRoute(journeyModeDetailsDAO.getRoute());
                    journeyModeDetails.setTravelTime(journeyModeDetailsDAO.getTravelTime());
                    journeyModeDetails.setFare(journeyModeDetailsDAO.getFare()!=null?journeyModeDetailsDAO.getFare():0.0);
                    if (journeyModeDetailsDAO.getSourceId() != null && journeyModeDetailsDAO.getDestinationId() != null) {
                        String journeyType = journeyModeDetailsDAO.getType();
                        List<Time> journeyTimings = new ArrayList<>();
                        switch (journeyType){
                            case METRO:
                                journeyTimings = timeTableService.getUpcomingTimings(journeyModeDetailsDAO.getSourceId(),
                                        journeyModeDetailsDAO.getDestinationId());
                                break;
                            case BUS:
                                journeyTimings = busTimeTableService.getUpcomingTimings(journeyModeDetailsDAO.getSourceId(),
                                        journeyModeDetailsDAO.getDestinationId());
                                break;
                            default:
                                log.info("No JourneyType Matching");
                                break;
                        }
                        journeyModeDetails.setTimings(journeyTimings);
                    }

                    journeyModeDetails.setIntermediateStops(journeyModeDetailsDAO.getIntermediateStops());
                    journeyModeDetails.setNoOfIntermediateStops(journeyModeDetailsDAO.getNoOfIntermediateStops());
                    journeyModeDetails.setSourceLatitude(journeyModeDetailsDAO.getSourceLatitude());
                    journeyModeDetails.setSourceLongitude(journeyModeDetailsDAO.getSourceLongitude());
                    journeyModeDetails.setDestinationLatitude(journeyModeDetailsDAO.getDestinationLatitude());
                    journeyModeDetails.setDestinationLongitude(journeyModeDetailsDAO.getDestinationLongitude());
                    journeyModeDetails.setEstimatedArrivalTime(journeyModeDetailsDAO.getEstimatedArrivalTime());
                    journeyModeDetails.setSourceId(journeyModeDetailsDAO.getSourceId());
                    journeyModeDetails.setDestinationId(journeyModeDetailsDAO.getDestinationId());
                    if (journeyModeDetailsDAO.getType().equals(METRO)) {
                        journeyModeDetails.setBookingAllowed(true);
                    } else {
                        journeyModeDetails.setBookingAllowed(journeyModeDetailsDAO.getBookingAllowed());
                    }
                    journeyModeDetails.setTicketBooked(journeyModeDetailsDAO.getTicketBooked());
                    journeyModeDetails.setTicketId(journeyModeDetailsDAO.getTicketId());

                    journeyModeDetailsDTOSortedSet.add(journeyModeDetails);
                }
                journeyPlannerConfirmedRouteDTO.setJourneyModeDetails(new ArrayList<>(journeyModeDetailsDTOSortedSet));
                return journeyPlannerConfirmedRouteDTO;
            }
        } catch (Exception exception) {
            log.error("Error in get journey planner route: {}", exception.getMessage());
            throw exception;
        }
        return journeyPlannerConfirmedRouteDTO;
    }

    public void endJourney() throws Exception {
        log.info("Request received in endJourney method");
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            JourneyPlannerRouteDAO journeyPlannerRouteDAO = journeyPlannerRouteRepository.findByAuthenticationDAOAndIsActive(authenticationDAO, true);
            if (journeyPlannerRouteDAO != null) {
                journeyPlannerRouteDAO.setActive(false);
                journeyPlannerRouteRepository.save(journeyPlannerRouteDAO);
                return;
            } else {
                throw new Exception("There is no Ongoing journey");
            }
        } catch (Exception exception) {
            log.error("Exception in endJourney: {}", exception.getMessage());
            throw exception;
        }
    }
}
