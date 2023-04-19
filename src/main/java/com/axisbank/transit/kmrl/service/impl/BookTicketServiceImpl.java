package com.axisbank.transit.kmrl.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.journey.model.DAO.JourneyModeDetailsDAO;
import com.axisbank.transit.journey.model.DAO.JourneyPlannerRouteDAO;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.axisbank.transit.journey.repository.JourneyPlannerRouteRepository;
import com.axisbank.transit.journey.utils.JourneyUtils;
import com.axisbank.transit.kmrl.client.KmrlTicketingClient;
import com.axisbank.transit.kmrl.constant.KmrlSecondaryTicketStatus;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DAO.TicketDAO;
import com.axisbank.transit.kmrl.model.DTO.*;
import com.axisbank.transit.kmrl.repository.TicketRepository;
import com.axisbank.transit.kmrl.service.BookTicketService;
import com.axisbank.transit.kmrl.service.StationService;
import com.axisbank.transit.kmrl.service.TimeTableService;
import com.axisbank.transit.payment.constants.ServiceProviderConstant;
import com.axisbank.transit.payment.constants.TransactionStatus;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.payment.service.PaymentService;
import com.axisbank.transit.payment.service.TransactionService;
import com.axisbank.transit.transitCardAPI.TransitCardClient.TransitCardClient;
import com.axisbank.transit.transitCardAPI.constants.TxnStatus;
import com.axisbank.transit.transitCardAPI.exceptions.BlockedCardException;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardInfoDTO;
import com.axisbank.transit.transitCardAPI.model.request.TopupRequest;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid.TopupToPrepaid;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import in.juspay.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.AFC_CANCELLATION_WINDOW;
import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.PAYMENT_OFFERS;
import static com.axisbank.transit.journey.constants.JourneyTypes.METRO;
import static com.axisbank.transit.kmrl.constant.Constants.*;
import static com.axisbank.transit.kmrl.constant.KmrlSecondaryTicketStatus.*;
import static com.axisbank.transit.kmrl.constant.KmrlTicketTypes.TICKET_TYPE_MAP;
import static com.axisbank.transit.kmrl.constant.TransitTicketStatus.*;
import static com.axisbank.transit.payment.constants.CommonConstants.FAILED;
import static com.axisbank.transit.payment.constants.PaymentServiceProviderConstant.PAYMENT_GATEWAY;
import static com.axisbank.transit.payment.constants.PaymentServiceProviderConstant.TRANSIT_CARD;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.ACTIVE;
import static com.axisbank.transit.transitCardAPI.constants.TxnType.BOOK_TICKET;

@Service
@Transactional
@Slf4j
public class BookTicketServiceImpl implements BookTicketService {

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    StationService stationService;

    @Autowired
    KmrlTicketingClient kmrlTicketingClient;

    @Autowired
    UserUtil userUtil;

    @Autowired
    PaymentService paymentService;
    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransitCardTxnService transitCardTxnService;

    @Autowired
    TransitCardClient transitCardClient;

    @Autowired
    TimeTableService timeTableService;
    @Autowired
    RedisClient redisClient;
    @Autowired
    GlobalConfigService globalConfigService;

    @Autowired
    JourneyPlannerRouteRepository journeyPlannerRouteRepository;

    public GetFareResponseDTO getFare(String sourceStationId, String destinationStationId, String ticketType) throws Exception {
        log.info("inside get Fare method");
        GetFareResponseDTO getFareResponseDTO = null;
        List<String> validTicketTypes = new ArrayList<>(TICKET_TYPE_MAP.keySet());
        if(!validTicketTypes.contains(ticketType))
            ticketType = "SJT";
        try {
            MetroStation source = stationService.getStationById(sourceStationId);
            MetroStation destination = stationService.getStationById(destinationStationId);
            String ticketPrice = getTicketFare(source.getStationCode(), destination.getStationCode(), ticketType, false);

            getFareResponseDTO = new GetFareResponseDTO();
            String todaysDate = CommonUtils.getCurrentDateTime("dd MMM yyyy");
            GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(PAYMENT_OFFERS, true);
            String transitCardDesc=null;
            try{
                if(globalConfig!=null){
                    transitCardDesc = globalConfig.getJsonValue().get("kochi1Card").get("offerText").asText();
                }
            } catch (Exception ex){
                log.error("Unable to get transit offers");
            }
            String ticketTypeName = TICKET_TYPE_MAP.get(ticketType).getDisplayName();
            getFareResponseDTO.setTicketTypes(new ArrayList<>(TICKET_TYPE_MAP.values()));
            List<PaymentMethodsDTO> paymentMethods = new ArrayList<>();
            PaymentMethodsDTO paymentMethodsDTO1 = new PaymentMethodsDTO();
            paymentMethodsDTO1.setTitle(TRANSIT_CARD_PAYMENT_METHOD);
            paymentMethodsDTO1.setDescription(transitCardDesc);

            PaymentMethodsDTO paymentMethodsDTO2 = new PaymentMethodsDTO();
            paymentMethodsDTO2.setTitle(OTHER_PAYMENT_METHOD_OPTIONS);
            paymentMethodsDTO2.setSubtitle("Debit/Credit/NetBanking/UPI");

            paymentMethods.add(paymentMethodsDTO1);
            paymentMethods.add(paymentMethodsDTO2);

            getFareResponseDTO.setFromStationDisplayName(source.getDisplayName());
            getFareResponseDTO.setToStationDisplayName(destination.getDisplayName());
            getFareResponseDTO.setFromStationId(sourceStationId);
            getFareResponseDTO.setToStationId(destinationStationId);
            getFareResponseDTO.setJourneyDate(todaysDate);
            getFareResponseDTO.setTicketType(ticketTypeName);
            getFareResponseDTO.setMetroLine("Blue Line"); // to be fetched
            getFareResponseDTO.setTicketFare(ticketPrice);
            getFareResponseDTO.setPaymentMethods(paymentMethods);

        } catch (Exception exception) {
            log.error("Exception in getFare: {}", exception.getMessage());
            throw exception;
        }
        return getFareResponseDTO;
    }

    public BookTicketResponseDTO bookTicket(BookTicketRequestDTO bookTicketRequestDTO) throws Exception {

        MetroStation source = stationService.getStationById(bookTicketRequestDTO.getFromStationId());
        MetroStation destination = stationService.getStationById(bookTicketRequestDTO.getToStationId());
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        isBookingAllowed(authenticationDAO.getId(), source, destination);
        List<String> validTicketTypes = new ArrayList<>(TICKET_TYPE_MAP.keySet());
        String ticketType = bookTicketRequestDTO.getTicketType();
        if(!validTicketTypes.contains(ticketType))
            ticketType = "SJT";
        String ticketPrice = getTicketFare(source.getStationCode(), destination.getStationCode(), ticketType, false);
        TicketDAO ticketDAO = new TicketDAO();
        ticketDAO.setAuthenticationDAO(authenticationDAO);
        ticketDAO.setTicketRefId(CommonUtils.generateRandInt(10));
        ticketDAO.setTicketStatus(null); // to be decided
        ticketDAO.setTicketType(ticketType);
        ticketDAO.setTransportMode("Metro"); // to be decided
        ticketDAO.setTicketFare(Double.parseDouble(ticketPrice));
        ticketDAO.setFromMetroStation(source);
        ticketDAO.setToMetroStation(destination);
        ticketDAO.setTravellers(bookTicketRequestDTO.getTravellers());

        TopupRequest orderRequest = new TopupRequest();
        orderRequest.setAmount(ticketPrice);
        String paymentMethod = bookTicketRequestDTO.getPaymentMethod();
        log.info("Payment Method: {}", paymentMethod);
        if (paymentMethod.equalsIgnoreCase(OTHER_PAYMENT_METHOD)) {
            return createOrderByPG(authenticationDAO, orderRequest, ticketDAO);
        } else if (paymentMethod.equalsIgnoreCase(TRANSIT_CARD_PAYMENT_METHOD)) {
            return createOrderByTransitCard(authenticationDAO, orderRequest, ticketDAO);
        }
        log.info("Payment method: {} not allowed", paymentMethod);
        throw new Exception("Invalid payment method");
    }

    private void isBookingAllowed(long authenticationDAO_Id, MetroStation source, MetroStation destination) throws Exception{
        List<TicketDAO> upcomingTickets = ticketRepository.findAllByAuthenticationDAO_IdAndTicketStatus(authenticationDAO_Id, UPCOMING);
        if (!upcomingTickets.isEmpty()) {
            for (TicketDAO ticketDAO : upcomingTickets) {
                ticketDAO = setTicketStatusFromKmrl(ticketDAO);
                if (ticketDAO.getTicketStatus().equalsIgnoreCase(UPCOMING)) {
                    throw new Exception("You have already booked a ticket from "+ticketDAO.getFromMetroStation().getDisplayName()+" to "+ticketDAO.getToMetroStation().getDisplayName()+", Please complete your journey before booking another ticket.");
                }
            }
        }
        List<TicketDAO> liveTickets = ticketRepository.findAllByAuthenticationDAO_IdAndTicketStatus(authenticationDAO_Id, LIVE);
        if (!liveTickets.isEmpty()) {
            for (TicketDAO ticketDAO: liveTickets) {
                ticketDAO = setTicketStatusFromKmrl(ticketDAO);
                if (ticketDAO.getTicketStatus().equalsIgnoreCase(LIVE)) {
                    if (ticketDAO.getTicketType().equalsIgnoreCase("SJT")) {
                        if (!ticketDAO.getFromMetroStation().getStationCode().equalsIgnoreCase(source.getStationCode())) {
                            throw new Exception("You can only book ticket from "+source.getDisplayName()+" until your ongoing trip ends.");
                        }
                    } else if (ticketDAO.getTicketType().equalsIgnoreCase("RJT")) {
                        switch (ticketDAO.getSecondaryTicketStatus()) {
                            case ONE_ENTRY:
                                if (!ticketDAO.getFromMetroStation().getStationCode().equalsIgnoreCase(source.getStationCode())) {
                                    throw new Exception("You can only book ticket from "+source.getDisplayName()+" until your ongoing trip ends.");
                                }
                                break;
                            case ONE_EXIT:
                                if (!ticketDAO.getFromMetroStation().getStationCode().equalsIgnoreCase(source.getStationCode()) && !ticketDAO.getToMetroStation().getStationCode().equalsIgnoreCase(source.getStationCode())) {
                                    throw new Exception("You have a return journey booked from "+ticketDAO.getToMetroStation().getDisplayName()+" to "+ticketDAO.getFromMetroStation().getDisplayName()+". Please cancel the return ticket to book new ticket from other stations or book a new ticket from "+destination.getDisplayName()+" or "+source.getDisplayName()+".");
                                }
                                break;
                            default:
                                log.debug("New Ticket Booking allowed when RJT ticket was live");
                                break;
                        }
                    }
                }
            }
        }
    }

    private BookTicketResponseDTO createOrderByTransitCard(AuthenticationDAO authenticationDAO, TopupRequest orderRequest, TicketDAO ticketDAO) throws Exception {
        checkTransitCardBalance(authenticationDAO.getCardDetailsDAO().getCardNo(), orderRequest.getAmount());
        TransactionDAO transactionDAO = new TransactionDAO();
        String transitCardNo = null;
        if (authenticationDAO.getCardDetailsDAO() != null) {
            transitCardNo = authenticationDAO.getCardDetailsDAO().getCardNo();
        } else {
            throw new Exception("Kochi1 card is not linked");
        }
        orderRequest.setSrcRefId(ticketDAO.getTicketRefId());
        TopupToPrepaid debitFromTransitRequest = transitCardTxnService.getDebitFromTransitRequest(
                transitCardNo, orderRequest);
        try {
            JsonNode transactionstatus = transitCardClient.topupToPrepaidResponse(debitFromTransitRequest);
            JsonNode transitTxnData = transactionstatus.get("TopupToPrepaidResponse").get("ResponseBody")
                    .get("TopupToPrepaidResult");
            String result = transitTxnData.get("Result").asText("Failed");
            String transitTxnId = transitTxnData.get("TxnReferanceId").asText();
            transactionDAO.setAmount(Double.parseDouble(orderRequest.getAmount()));
            transactionDAO.setOrderId(CommonUtils.generateRandInt(10));
            transactionDAO.setTxnType(BOOK_TICKET.toString());
            transactionDAO.setPaymentServiceProvider(TRANSIT_CARD);
            transactionDAO.setServiceProvider(ServiceProviderConstant.KMRL);
            transactionDAO.setSpRefId(ticketDAO.getTicketRefId());
            transactionDAO.setTicketDAO(ticketDAO);
            transactionDAO.setAuthenticationDAO(authenticationDAO);
            transactionDAO.setPspTxnId(transitTxnId); // to be confirmed
            transactionDAO.setPspPaymentMethodType(TRANSIT_CARD_PAYMENT_METHOD); // to be confirmed
            transactionDAO.setPspPaymentMethod(TRANSIT_CARD_PAYMENT_METHOD);
            transactionDAO.setTxnCompletedOn(new Date(CommonUtils.getCurrentTimeMillis()));
            transactionDAO.setPspRefId(authenticationDAO.getCardDetailsDAO().getCardToken());
            transactionDAO.setPspStatus(result);

            if (!result.equalsIgnoreCase("success")) {
                transactionDAO.setFinalTxnStatus(TxnStatus.FAILED.toString());
                throw new Exception("Unable to debit amount via Kochi1 card");
            }

            try {
                transactionDAO = paymentService.processBookTicketTxnKmrl(transactionDAO.getOrderId(), transactionDAO);
            } catch (Exception exception) {
                transactionDAO.setFinalTxnStatus(TxnStatus.FAILED.toString());
                transactionDAO.setSpStatus(TxnStatus.FAILED.toString());
                ticketDAO.setTransactionDAO(transactionDAO);
                TransactionDAO txn = transactionService.saveTxn(transactionDAO);
                transactionService.createRefund(txn);
                BookTicketResponseDTO bookTicketResponseDTO = new BookTicketResponseDTO();
                bookTicketResponseDTO.setOrderId(transactionDAO.getOrderId());
                bookTicketResponseDTO.setBookingId(ticketDAO.getTicketRefId());
                bookTicketResponseDTO.setBookingStatus(transactionDAO.getSpStatus());
                bookTicketResponseDTO.setMethodMethodType(TRANSIT_CARD_PAYMENT_METHOD);
                bookTicketResponseDTO.setPaymentLinks(null); // to be confirmed
                bookTicketResponseDTO.setTransactionStatus(transactionDAO.getFinalTxnStatus());
                log.error("Exception in processBookTicketTxnKmrl: {}", exception.getMessage());
                return bookTicketResponseDTO;
            }

        } catch (Exception ex) {
            transactionDAO.setFinalTxnStatus(TxnStatus.FAILED.toString());
            transactionDAO.setSpStatus(TxnStatus.FAILED.toString());
            transactionService.saveTxn(transactionDAO);
            log.error("Exception in createOrderByTransitCard: {}", ex.getMessage());
            throw ex;
        }

        ticketDAO.setTransactionDAO(transactionDAO);
        transactionService.saveTxn(transactionDAO);

        updateTicketBookingStatusInActiveJourney(authenticationDAO, ticketDAO.getTicketRefId());

        BookTicketResponseDTO bookTicketResponseDTO = new BookTicketResponseDTO();
        bookTicketResponseDTO.setOrderId(transactionDAO.getOrderId());
        bookTicketResponseDTO.setBookingId(ticketDAO.getTicketRefId());
        bookTicketResponseDTO.setBookingStatus(transactionDAO.getSpStatus());
        bookTicketResponseDTO.setMethodMethodType(TRANSIT_CARD_PAYMENT_METHOD);
        bookTicketResponseDTO.setPaymentLinks(null); // to be confirmed
        bookTicketResponseDTO.setTransactionStatus(transactionDAO.getFinalTxnStatus());

        return bookTicketResponseDTO;
    }

    private void checkTransitCardBalance(String cardNo, String bookingFare) throws Exception{
        TransitCardInfoDTO cardInfoDTO = transitCardTxnService.getTransitCardInfo(cardNo);
        double hostBalance = Double.parseDouble(cardInfoDTO.getTotalHostBalance());
        double bookingAmount = Double.parseDouble(bookingFare);
        String status = cardInfoDTO.getCardStatCode();
        String subStatus = cardInfoDTO.getCardStatSubCode();
        String completeStatus = transitCardTxnService.getBlockStatus(status,subStatus);
        if(!completeStatus.equalsIgnoreCase(ACTIVE))
            throw new BlockedCardException("This Operation is not allowed on blocked/expired card");
        if (hostBalance < bookingAmount) {
            throw new Exception("Insufficient balance in Kochi1 card, Please recharge.");
        }
    }

    private void updateTicketBookingStatusInActiveJourney(AuthenticationDAO authenticationDAO, String ticketRefId) {
        JourneyPlannerRouteDAO journeyPlannerRouteDAO = journeyPlannerRouteRepository.findByAuthenticationDAOAndIsActive(authenticationDAO, true);
        if (journeyPlannerRouteDAO == null) {
            log.debug("No Active Journey Planner found.");
            return;
        }
        List<JourneyModeDetailsDAO> journeyModeDetailsDAOList = journeyPlannerRouteDAO.getJourneyModeDetailsDAOList();
        for (JourneyModeDetailsDAO journeyModeDetailsDAO : journeyModeDetailsDAOList) {
            if (journeyModeDetailsDAO.getType().equals(METRO)) {
                journeyModeDetailsDAO.setTicketBooked(true);
                journeyModeDetailsDAO.setTicketId(ticketRefId);
            }
        }
    }

    public BookTicketResponseDTO createOrderByPG(AuthenticationDAO authenticationDAO, TopupRequest request, TicketDAO ticketDAO) throws Exception {
        DAOUser daoUser = authenticationDAO.getDaoUser();
        Order order = paymentService.createOrder(request, daoUser.getPgCustomerId(), BOOK_TICKET.toString());
        TransactionDAO transactionDAO = new TransactionDAO();

        transactionDAO.setAmount(Double.parseDouble(request.getAmount()));
        transactionDAO.setOrderId(order.getOrderId());
        transactionDAO.setTxnType(BOOK_TICKET.toString());
        transactionDAO.setFinalTxnStatus(TransactionStatus.INITIATED.toString());
        transactionDAO.setMerchantId(order.getMerchantId());
        transactionDAO.setPspStatus(order.getStatus());
        transactionDAO.setPspRefId(order.getCustomerId());
        transactionDAO.setPaymentServiceProvider(PAYMENT_GATEWAY);

        transactionDAO.setServiceProvider(ServiceProviderConstant.KMRL);
        transactionDAO.setSpRefId(ticketDAO.getTicketRefId());
        transactionDAO.setSpStatus(TxnStatus.INITIATED.toString());
        transactionDAO.setTicketDAO(ticketDAO);
        transactionDAO.setAuthenticationDAO(authenticationDAO);
        ticketDAO.setTransactionDAO(transactionDAO);
        transactionService.saveTxn(transactionDAO);

        updateTicketBookingStatusInActiveJourney(authenticationDAO, ticketDAO.getTicketRefId());

        BookTicketResponseDTO bookTicketResponseDTO = new BookTicketResponseDTO();
        bookTicketResponseDTO.setOrderId(order.getOrderId());
        bookTicketResponseDTO.setBookingId(transactionDAO.getSpTxnId()); // not sure, need to be confirmed
        bookTicketResponseDTO.setBookingStatus(transactionDAO.getSpStatus());
        bookTicketResponseDTO.setMethodMethodType(OTHER_PAYMENT_METHOD);
        bookTicketResponseDTO.setPaymentLinks(order.getPaymentLinks());
        bookTicketResponseDTO.setTransactionStatus(transactionDAO.getFinalTxnStatus());

        return bookTicketResponseDTO;
    }

    @Override
    public ViewTicketsResponseDTO getTickets(int pageNo, int pageSize, String status) throws Exception {

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("updatedAt").descending());
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        ViewTicketsResponseDTO viewTicketResponseDTO = new ViewTicketsResponseDTO();
        List<TicketDAO> ticketDAOS = ticketRepository.findAllByAuthenticationDAOAndTicketStatusLike(authenticationDAO, status, paging);
        List<TicketDetailsDTO> ticketDetailsDTOS = null;
        TicketDetailsDTO viewTicketRes = null;
        String ticketType;
        String ticketTypeDispName;
        for (TicketDAO ticketDAO : ticketDAOS) {
            ticketType = ticketDAO.getTicketType();
            List<String> validTicketTypes = new ArrayList<>(TICKET_TYPE_MAP.keySet());
            if(!validTicketTypes.contains(ticketType))
                ticketType = "SJT";
            ticketTypeDispName = TICKET_TYPE_MAP.get(ticketType).getDisplayName();
            viewTicketRes = new TicketDetailsDTO();
            ticketDAO = setTicketStatusFromKmrl(ticketDAO);
            String sourceId = ticketDAO.getFromMetroStation().getStationId();
            String destinationId =ticketDAO.getToMetroStation().getStationId();
            viewTicketRes.setTicketNo(ticketDAO.getTicketNo());
            viewTicketRes.setJourneyDate(CommonUtils.getDateFormat(ticketDAO.getCreatedAt(), "yyyy-MM-dd HH:mm:ss"));
            viewTicketRes.setTicketFare(ticketDAO.getTicketFare());
            viewTicketRes.setDestination(ticketDAO.getToMetroStation().getDisplayName());
            viewTicketRes.setSource(ticketDAO.getFromMetroStation().getDisplayName());
            viewTicketRes.setTripType(ticketType);
            viewTicketRes.setTicketId(ticketDAO.getTicketNo());
            viewTicketRes.setLineType("Blueline");
            viewTicketRes.setTicketGUID(ticketDAO.getTicketGUID());
            viewTicketRes.setTicketRefId(ticketDAO.getTicketRefId());
            viewTicketRes.setTicketStatus(ticketDAO.getTicketStatus());
            viewTicketRes.setTicketTypeDispName(ticketTypeDispName);
            viewTicketRes.setSecondaryTicketStatus(ticketDAO.getSecondaryTicketStatus());
            viewTicketRes.setMessage(getTicketMessage(ticketDAO));
            Time time = getNextTripTime(sourceId,destinationId);
            if(LIVE.equalsIgnoreCase(ticketDAO.getTicketStatus()) && viewTicketResponseDTO.getLiveTicketsDTO() ==null) {
                viewTicketRes.setNextTripTime(time);
                LiveTicketsDTO liveTicketsDTO = getLiveTicketCoordinates(viewTicketRes, ticketDAO);
                viewTicketResponseDTO.setLiveTicketsDTO(liveTicketsDTO);
            } else if (UPCOMING.equalsIgnoreCase(ticketDAO.getTicketStatus())) {
                viewTicketRes.setNextTripTime(time);
                if (viewTicketResponseDTO.getUpcomingTicketsList() == null || viewTicketResponseDTO.getUpcomingTicketsList().isEmpty()) {
                    ticketDetailsDTOS = new ArrayList<>();
                    ticketDetailsDTOS.add(viewTicketRes);
                    viewTicketResponseDTO.setUpcomingTicketsList(ticketDetailsDTOS);
                } else {
                    ticketDetailsDTOS = viewTicketResponseDTO.getUpcomingTicketsList();
                    ticketDetailsDTOS.add(viewTicketRes);
                    viewTicketResponseDTO.setUpcomingTicketsList(ticketDetailsDTOS);
                }
            } else if (COMPLETED.equalsIgnoreCase(ticketDAO.getTicketStatus())) {
                if (viewTicketResponseDTO.getCompletedTicketsList() == null || viewTicketResponseDTO.getCompletedTicketsList().isEmpty()) {
                    ticketDetailsDTOS = new ArrayList<>();
                    ticketDetailsDTOS.add(viewTicketRes);
                    viewTicketResponseDTO.setCompletedTicketsList(ticketDetailsDTOS);
                } else {
                    ticketDetailsDTOS = viewTicketResponseDTO.getCompletedTicketsList();
                    ticketDetailsDTOS.add(viewTicketRes);
                    viewTicketResponseDTO.setCompletedTicketsList(ticketDetailsDTOS);
                }
            } else if(CANCELLED.equalsIgnoreCase(ticketDAO.getTicketStatus())) {
                if (viewTicketResponseDTO.getCancelledTicketsList() == null || viewTicketResponseDTO.getCancelledTicketsList().isEmpty()) {
                    ticketDetailsDTOS = new ArrayList<>();
                    ticketDetailsDTOS.add(viewTicketRes);
                    viewTicketResponseDTO.setCancelledTicketsList(ticketDetailsDTOS);
                } else {
                    ticketDetailsDTOS = viewTicketResponseDTO.getCancelledTicketsList();
                    ticketDetailsDTOS.add(viewTicketRes);
                    viewTicketResponseDTO.setCancelledTicketsList(ticketDetailsDTOS);
                }
            }

            viewTicketRes = null;
        }
        return viewTicketResponseDTO;
    }

    @Override
    public TicketDetailsDTO getTicketDetails(String ticketRefId) throws Exception {
        log.info("Request receive to fetching ticketDetail for given TicketRefId:{}",ticketRefId);
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        TicketDAO ticketDAO = ticketRepository.findByAuthenticationDAOAndTicketRefId(authenticationDAO,ticketRefId);
        if(ticketDAO==null) {
            throw new Exception("Invalid ticketRefId");
        }
        ticketDAO = setTicketStatusFromKmrl(ticketDAO);
        MetroStation sourceStation = ticketDAO.getFromMetroStation();
        MetroStation destStation = ticketDAO.getToMetroStation();
        String sourceId = sourceStation.getStationId();
        String destinationId = destStation.getStationId();

        String ticketType = ticketDAO.getTicketType();
        List<String> validTicketTypes = new ArrayList<>(TICKET_TYPE_MAP.keySet());
        if(!validTicketTypes.contains(ticketType))
            ticketType = "SJT";
        String ticketTypeDispName = TICKET_TYPE_MAP.get(ticketType).getDisplayName();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        TicketDetailsDTO ticketDetailsDTO = modelMapper.map(ticketDAO, TicketDetailsDTO.class);
        ticketDetailsDTO.setLineType("Blueline");
        ticketDetailsDTO.setTicketId(ticketDAO.getTicketNo());
        ticketDetailsDTO.setNextTripTime(getNextTripTime(sourceId,destinationId));
        ticketDetailsDTO.setTripType(ticketType);
        ticketDetailsDTO.setTicketRefId(ticketDAO.getTicketRefId());
        ticketDetailsDTO.setJourneyDate(CommonUtils.getDateFormat(ticketDAO.getCreatedAt(), "yyyy-MM-dd HH:mm:ss"));
        ticketDetailsDTO.setTicketTypeDispName(ticketTypeDispName);
        // Start time is of the day as we need to get at least one upcoming to calculate total time.
        JourneyModeDetails journeyModeDetails =timeTableService.getMetroRouteDetails(sourceId,destinationId,
                "00:00:00");
        log.debug("Journey Mode Details: {}", journeyModeDetails);
        // if null returned from journey details then calculate dist and time with walking configuration as fallback
        if (journeyModeDetails == null){
            Double dist = CommonUtils.calculateMetroDistance(sourceStation.getDistance(), destStation.getDistance());
            ticketDetailsDTO.setTotalDistance(dist);
            ticketDetailsDTO.setTotalTime(JourneyUtils.getDurationFromDistance(dist));
        } else {
            ticketDetailsDTO.setTotalDistance(journeyModeDetails.getDistance());
            ticketDetailsDTO.setTotalTime(journeyModeDetails.getTravelTime());
        }
        ticketDetailsDTO.setDestination(destStation.getDisplayName());
        ticketDetailsDTO.setSource(sourceStation.getDisplayName());
        ticketDetailsDTO.setSecondaryTicketStatus(ticketDAO.getSecondaryTicketStatus());
        ticketDetailsDTO.setMessage(getTicketMessage(ticketDAO));
        return ticketDetailsDTO;
    }

    private String getTicketMessage(TicketDAO ticketDAO) {
        String message = "";
        String source = ticketDAO.getFromMetroStation().getDisplayName();
        String destination = ticketDAO.getToMetroStation().getDisplayName();
        if (ticketDAO.getTicketType().equalsIgnoreCase("SJT")) {
            switch (ticketDAO.getSecondaryTicketStatus()) {
                case UNUSED:
                   message = "Validate this QR code at "+source+" AFC metro gate to begin your journey.";
                   break;
                case ENTERED:
                    message = "Your QR code is validated at "+source+".";
                    break;
                case USED:
                    message = "Your one-way journey is completed.";
                    break;
                case EXPIRED:
                    message = "Your ticket has expired.";
                    break;
                default:
                    log.debug("No secondary ticket status matched for SJT");
                    break;
            }
        } else if (ticketDAO.getTicketType().equalsIgnoreCase("RJT")) {
            switch (ticketDAO.getSecondaryTicketStatus()) {
                case UNUSED:
                    message = "Validate this QR code at "+source+" AFC metro gate to begin your journey.";
                    break;
                case ONE_ENTRY:
                    message = "Your QR code is validated at "+source+".";
                    break;
                case ONE_EXIT:
                    message = "Your journey from "+source+" to "+destination+" is completed. Validate this QR code at "+destination+" to begin your return journey.";
                    break;
                case TWO_ENTRY:
                    message = "Your QR code is validated at "+destination+".";
                    break;
                case USED:
                    message = "Your round trip journey is completed.";
                    break;
                case EXPIRED:
                    message = "Your ticket has expired.";
                    break;
                default:
                    log.debug("No secondary ticket status matched for RJT");
                    break;
            }
        }
        return message;
    }

    private TicketDAO setTicketStatusFromKmrl(TicketDAO ticketDAO) throws Exception{
        if (ticketDAO.getTicketStatus().equalsIgnoreCase(CANCELLED))
            return ticketDAO;
        if (ticketDAO.getTicketStatus().equalsIgnoreCase(COMPLETED)) {
            if (ticketDAO.getSecondaryTicketStatus() == null) {
                ticketDAO.setSecondaryTicketStatus(USED);
            }
            return ticketDAO;
        }

        try {
            JsonNode qrTicketLastStatus = kmrlTicketingClient.getQRTicketLastStatus(ticketDAO.getTicketGUID(), ticketDAO.getTicketType());
            JsonNode getQrTicketLastStatusResult = qrTicketLastStatus.get("GetQrTicketLastStatusResponse").get("GetQrTicketLastStatusResult");
            if (!getQrTicketLastStatusResult.get("Result").asText().equalsIgnoreCase("1")) {
                log.debug("Error in getting ticket last status");
                throw new Exception("Error in getting KMRL ticket status");
            }
            String currentDateInString = CommonUtils.currentDateTime("yyyy-MM-dd");
            Date currentDate = CommonUtils.getDate(currentDateInString, "yyyy-MM-dd");
            Date journeyDate = CommonUtils.getDate(ticketDAO.getJourneyDate(), "yyyy-MM-dd");
            boolean passed = journeyDate.before(currentDate);
            switch (getQrTicketLastStatusResult.get("Status").asText().toUpperCase()) {
                case KmrlSecondaryTicketStatus.UNUSED:
                    if(ticketDAO.getTicketStatus().equalsIgnoreCase(UPCOMING) && passed) {
                        ticketDAO.setTicketStatus(COMPLETED);
                        ticketDAO.setSecondaryTicketStatus(EXPIRED);
                        break;
                    }
                    ticketDAO.setTicketStatus(UPCOMING);
                    ticketDAO.setSecondaryTicketStatus(UNUSED);
                    break;
                case KmrlSecondaryTicketStatus.USED:
                    ticketDAO.setTicketStatus(COMPLETED);
                    ticketDAO.setSecondaryTicketStatus(USED);
                    break;
                case KmrlSecondaryTicketStatus.ENTERED:
                    ticketDAO.setTicketStatus(LIVE);
                    ticketDAO.setSecondaryTicketStatus(ENTERED);
                    break;
                case KmrlSecondaryTicketStatus.ONE_ENTRY:
                    ticketDAO.setTicketStatus(LIVE);
                    ticketDAO.setSecondaryTicketStatus(ONE_ENTRY);
                    break;
                case KmrlSecondaryTicketStatus.ONE_EXIT:
                    ticketDAO.setTicketStatus(LIVE);
                    ticketDAO.setSecondaryTicketStatus(ONE_EXIT);
                    break;
                case KmrlSecondaryTicketStatus.TWO_ENTRY:
                    ticketDAO.setTicketStatus(LIVE);
                    ticketDAO.setSecondaryTicketStatus(TWO_ENTRY);
                default:
                    if(ticketDAO.getTicketStatus().equalsIgnoreCase(UPCOMING) && passed) {
                        ticketDAO.setTicketStatus(COMPLETED);
                        break;
                    }
                    ticketDAO.setTicketStatus(UPCOMING);
                    break;
            }
            return ticketRepository.save(ticketDAO);
        } catch (Exception exception) {
            log.error("Error in getting ticket status from KMRL: {}", exception.getMessage());
            return ticketDAO;
        }
    }


    private Time getNextTripTime(String sourceId, String destinationId) {
        List<Time> times = timeTableService.getUpcomingTimings(sourceId,destinationId);
        if(!times.isEmpty()) {
            return  times.get(0);
        }
        return null;
    }


    /***
     *  This method use Entity @viewTicketRes and TicketDAO as parameters and create and  return LiveTicketDTO entity
     * @param ticketDetailsDTO
     * @param ticketDAO
     * @return liveTicketsDTO
     */

    private LiveTicketsDTO getLiveTicketCoordinates(TicketDetailsDTO ticketDetailsDTO, TicketDAO ticketDAO) {
        LiveTicketsDTO liveTicketsDTO = new LiveTicketsDTO();
        liveTicketsDTO.setSourceLatitude(ticketDAO.getFromMetroStation().getLatitude());
        liveTicketsDTO.setSourceLongitude(ticketDAO.getFromMetroStation().getLongitude());
        liveTicketsDTO.setDestinationLatitude(ticketDAO.getToMetroStation().getLatitude());
        liveTicketsDTO.setDestinationLongitude(ticketDAO.getToMetroStation().getLongitude());
        liveTicketsDTO.setTicketData(ticketDetailsDTO);
        return liveTicketsDTO;
    }

    @Override
    public String getTicketFare(String sourceStationCode, String destinationStationCode) throws Exception {
        return getTicketFare(sourceStationCode, destinationStationCode,false);
    }

    @Override
    public String getTicketFare(String sourceStationCode, String destinationStationCode, boolean fromCache) throws Exception {
        return getTicketFare(sourceStationCode, destinationStationCode,"SJT", fromCache);
    }
    @Override
    public String getTicketFare(String sourceStationCode, String destinationStationCode, String ticketType, boolean fromCache) throws Exception {
        String redisKey = "metro_fare:"+ticketType+":"+sourceStationCode+"-"+destinationStationCode;
        try{
            if(fromCache){
                String price = redisClient.getValue(redisKey);
                if (price!=null && !price.equalsIgnoreCase(""))
                    return price;
            }
        } catch (Exception ex){
            log.info("Failed to get metro fare");
        }
        String currentDateTime = CommonUtils.getCurrentDateTime("yyyy-MM-dd");
        JsonNode ticketPriceResult = kmrlTicketingClient.selTicketPrice(ticketType, sourceStationCode,
                destinationStationCode, currentDateTime, "EN");
        JsonNode selTicketPriceResult = ticketPriceResult.get("SelTicketPriceResponse").get("SelTicketPriceResult");
        if (!selTicketPriceResult.get("description").asText().equalsIgnoreCase("Success")) {
            log.debug("Error in getting ticket price for sourceStationCode: "+sourceStationCode+" and destinationStationCode: "+destinationStationCode);
            return "";
        }
        String price = selTicketPriceResult.get("ticketPrice").asText();
        try{
            redisClient.setValue(redisKey, price, 6*60*60);
        } catch (Exception ex){
            log.error("Failed to set metro fare");
        }
        return price;
    }

    @Override
    public String getRefundValue(String ticketId) throws Exception {
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        TicketDAO ticketDAO = ticketRepository.findByAuthenticationDAOAndTicketRefId(authenticationDAO,ticketId);
        if(ticketDAO==null)
            throw new Exception("Ticket Not found");
        String refund_type = validateTicketBeforeRefund(ticketDAO);
        JsonNode refundData = kmrlTicketingClient.toBeRefundedTicket(ticketDAO.getTicketGUID());
        JsonNode data = refundData.get("GetToBeRefundedAmountResponse").get("GetToBeRefundedAmountResult");
        if (data.get("Result").asText().equalsIgnoreCase("0")) {
            log.error(data.get("Description").asText());
            throw new Exception(FAILED_TO_BE_REFUNDED_GENERIC);
        }
        String totalRefund = data.get("Amount").asText();
        String stationA = ticketDAO.getFromMetroStation().getDisplayName();
        String stationB = ticketDAO.getToMetroStation().getDisplayName();
        if(refund_type.equalsIgnoreCase("RJT_PARTIAL")){
            String temp = stationA;
            stationA = stationB;
            stationB = temp;
        }
        return MessageFormat.format(SUCCESSFUL_TO_BE_REFUNDED,stationA, stationB, totalRefund);
    }

    @Override
    public String refundTicket(String ticketId) throws Exception {
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        TicketDAO ticketDAO = ticketRepository.findByAuthenticationDAOAndTicketRefId(authenticationDAO,ticketId);
        if(ticketDAO==null)
            throw new Exception("Ticket Not found");
        validateTicketBeforeRefund(ticketDAO);
        JsonNode refundData = kmrlTicketingClient.refundTicket(ticketDAO.getTicketGUID());
        JsonNode data = refundData.get("RefundTicketResponse").get("RefundTicketResult");
        if (data.get("Result").asText().equalsIgnoreCase("0")) {
            log.error(data.get("Description").asText());
            throw new Exception(FAILED_TO_BE_REFUNDED_GENERIC);
        }
        Double totalRefund = data.get("Amount").asDouble();
        TransactionDAO refundTxn = transactionService.createRefund(ticketDAO.getTransactionDAO(),totalRefund);
        if (refundTxn.getFinalTxnStatus().equalsIgnoreCase(FAILED)){
            throw new Exception("Failed to refund Amount");
        }
        ticketDAO.setTicketStatus(CANCELLED);
        ticketDAO.setSecondaryTicketStatus(CANCELLED);
        ticketRepository.save(ticketDAO);
        return "Ticket refunded for amount: "+totalRefund;
    }

    private String validateTicketBeforeRefund(TicketDAO ticketDAO) throws Exception {
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(AFC_CANCELLATION_WINDOW, true);
        if(globalConfig!=null){
            JsonNode node = globalConfig.getJsonValue();
            String fromTime = node.get("from").asText();
            String toTime = node.get("to").asText();
            String currentTime = CommonUtils.getCurrentDateTime("HH:mm:ss");
            log.info("Current Time: {}", currentTime);
            if(!CommonUtils.checkTimeBetween(fromTime,toTime,currentTime))
                throw new Exception(FAILED_TO_BE_REFUNDED_WINDOW_CLOSED);
        }
        if(ticketDAO.getTicketStatus().equalsIgnoreCase(COMPLETED))
            throw new Exception("You cannot cancel used ticket");
        JsonNode qrTicketLastStatus = kmrlTicketingClient.getQRTicketLastStatus(ticketDAO.getTicketGUID(), ticketDAO.getTicketType());
        JsonNode getQrTicketLastStatusResult = qrTicketLastStatus.get("GetQrTicketLastStatusResponse").get("GetQrTicketLastStatusResult");
        if (!getQrTicketLastStatusResult.get("Result").asText().equalsIgnoreCase("1")) {
            log.debug("Error in getting ticket last status");
            throw new Exception("Error in getting KMRL ticket status");
        }
        String ticketStatus = getQrTicketLastStatusResult.get("Status").asText().toUpperCase();
        String ticketType = ticketDAO.getTicketType();
        if(ticketStatus.equalsIgnoreCase(USED))
            throw new Exception(FAILED_TO_BE_REFUNDED_ENTERED);
        if((ticketType.equalsIgnoreCase("SJT") && ticketStatus.equalsIgnoreCase(ENTERED)))
            throw new Exception(FAILED_TO_BE_REFUNDED_ENTERED);
        if(ticketType.equalsIgnoreCase("RJT")){
            if(ticketStatus.equalsIgnoreCase(UNUSED))
                return "RJT_FULL";
            if(ticketStatus.equalsIgnoreCase(ONE_ENTRY) || ticketStatus.equalsIgnoreCase(TWO_ENTRY))
                throw new Exception(FAILED_TO_BE_REFUNDED_ENTERED);
            if(ticketStatus.equalsIgnoreCase(ONE_EXIT)){
                TransactionDAO transactionDAO = ticketDAO.getTransactionDAO();
                if(transactionDAO.getPaymentServiceProvider().equalsIgnoreCase(TRANSIT_CARD))
                    throw new Exception(FAILED_TO_BE_REFUNDED_ENTERED);
                return "RJT_PARTIAL";
            }
        }
        return "SJT_FULL";
    }
}
