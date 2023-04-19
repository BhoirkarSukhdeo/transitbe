package com.axisbank.transit.kmrl.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.config.ApplicationSetupData;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.axisbank.transit.journey.repository.JourneyPlannerRouteRepository;
import com.axisbank.transit.kmrl.client.KmrlTicketingClient;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DAO.TicketDAO;
import com.axisbank.transit.kmrl.model.DTO.BookTicketRequestDTO;
import com.axisbank.transit.kmrl.model.DTO.BookTicketResponseDTO;
import com.axisbank.transit.kmrl.model.DTO.GetFareResponseDTO;
import com.axisbank.transit.kmrl.model.DTO.TicketDetailsDTO;
import com.axisbank.transit.kmrl.repository.TicketRepository;
import com.axisbank.transit.kmrl.service.impl.BookTicketServiceImpl;
import com.axisbank.transit.payment.constants.ServiceProviderConstant;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.payment.service.PaymentService;
import com.axisbank.transit.payment.service.TransactionService;
import com.axisbank.transit.transitCardAPI.TransitCardClient.TransitCardClient;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardInfoDTO;
import com.axisbank.transit.transitCardAPI.model.request.TopupRequest;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid.TopupToPrepaid;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import in.juspay.model.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.PAYMENT_OFFERS;
import static com.axisbank.transit.journey.constants.JourneyTypes.METRO;
import static com.axisbank.transit.kmrl.constant.Constants.OTHER_PAYMENT_METHOD;
import static com.axisbank.transit.kmrl.constant.Constants.TRANSIT_CARD_PAYMENT_METHOD;
import static com.axisbank.transit.kmrl.constant.TransitTicketStatus.UPCOMING;
import static com.axisbank.transit.payment.constants.PaymentServiceProviderConstant.TRANSIT_CARD;
import static com.axisbank.transit.transitCardAPI.constants.TxnType.BOOK_TICKET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationSetupData.class, BCrypt.class})
@PowerMockIgnore("javax.crypto.*")
public class BookTicketServiceTest extends BaseTest {
    MetroStation source;
    MetroStation destination;
    GlobalConfigDTO globalConfig=null;
    JsonNode ticketPriceResult;
    private AuthenticationDAO authenticationDAO;
    TicketDAO ticketDAO;
    List<TicketDAO> ticketDAOList;
    TransactionDAO transactionDAO;
    CardDetailsDAO cardDetailsDAO;
    private DAOUser daoUser;
    List<Time> upcomingTime;
    JourneyModeDetails journeyModeDetails;
    MetroStation metroStation;

    @Mock
    GlobalConfigService globalConfigService;
    @Mock
    StationService stationService;
    @Mock
    KmrlTicketingClient kmrlTicketingClient;
    @Mock
    RedisClient redisClient;
    @Mock
    UserUtil userUtil;
    @Mock
    TicketRepository ticketRepository;
    @Mock
    TransitCardTxnService transitCardTxnService;
    @Mock
    TransitCardClient transitCardClient;
    @Mock
    PaymentService paymentService;
    @Mock
    TransactionService transactionService;
    @Mock
    JourneyPlannerRouteRepository journeyPlannerRouteRepository;
    @Mock
    TimeTableService timeTableService;

    @InjectMocks
    @Autowired
    BookTicketServiceImpl bookTicketService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(ApplicationSetupData.class);
        PowerMockito.when(ApplicationSetupData.getCardSecretKey()).thenReturn("U75m30vuhpikWy2Z");
        source = new MetroStation();
        source.setStationId("1234567890");
        source.setDisplayName("ABC");
        source.setLatitude(12.12111);
        source.setLongitude(77.1211);
        source.setDistance(14.00);
        source.setStationCode("ABC");
        source.setStationCodeDn("ABC_DN");
        source.setStationCodeUp("ABC_UP");
        source.setSetStationId(1);
        source.setActive(true);
        destination = new MetroStation();
        destination.setStationId("1234567891");
        destination.setDisplayName("ABCD");
        destination.setLatitude(12.12111);
        destination.setLongitude(78.1211);
        destination.setDistance(15.00);
        destination.setStationCode("ABCD");
        destination.setStationCodeDn("ABCD_DN");
        destination.setStationCodeUp("ABCD_UP");
        destination.setSetStationId(2);
        destination.setActive(true);
        ticketPriceResult = CommonUtils.convertJsonStringToObject("{\"SelTicketPriceResponse\":{\"SelTicketPriceResult\":{\"description\":\"Success\",\"ticketPrice\":\"20.3\"}}}", JsonNode.class);


        authenticationDAO = new AuthenticationDAO();


        daoUser = new DAOUser();
        daoUser.setOccupation("SE");
        daoUser.setDob(LocalDate.of(1994, 11, 23));
        daoUser.setGender(Gender.MALE);
        daoUser.setPgCustomerId("1211111");

        authenticationDAO.setMobile("2233771199");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        authenticationDAO.setDaoUser(daoUser);

        cardDetailsDAO = new CardDetailsDAO();
        cardDetailsDAO.setCardNo("123456789");
        cardDetailsDAO.setCardToken("1211112111");

        cardDetailsDAO.setAuthenticationDAO(authenticationDAO);
        daoUser.setAuthenticationDAO(authenticationDAO);
        authenticationDAO.setCardDetailsDAO(cardDetailsDAO);

        transactionDAO = new TransactionDAO();

        transactionDAO.setAmount(Double.parseDouble("20.3"));
        transactionDAO.setOrderId(CommonUtils.generateRandInt(10));
        transactionDAO.setTxnType(BOOK_TICKET.toString());
        transactionDAO.setPaymentServiceProvider(TRANSIT_CARD);
        transactionDAO.setServiceProvider(ServiceProviderConstant.KMRL);
        transactionDAO.setSpRefId("1232111");
        transactionDAO.setAuthenticationDAO(authenticationDAO);
        transactionDAO.setPspTxnId("121211111");
        transactionDAO.setPspPaymentMethodType(TRANSIT_CARD_PAYMENT_METHOD);
        transactionDAO.setPspPaymentMethod(TRANSIT_CARD_PAYMENT_METHOD);
        transactionDAO.setTxnCompletedOn(new Date(CommonUtils.getCurrentTimeMillis()));
        transactionDAO.setPspRefId(authenticationDAO.getCardDetailsDAO().getCardToken());
        transactionDAO.setPspStatus("success");

        metroStation = new MetroStation();
        metroStation.setDisplayName("Aluva");
        metroStation.setSetStationId(1);
        metroStation.setStationCodeUp("STA_COD_3105T_UP");
        metroStation.setStationCodeDn("STA_COD_3106T_UP");
        metroStation.setStationCode("ALVA");
        metroStation.setStationId("metro123");
        metroStation.setDistance(34.5);
        metroStation.setLatitude(76.6);
        metroStation.setLongitude(78.6);

        ticketDAO = new TicketDAO();
        ticketDAO.setTicketStatus(UPCOMING);
        ticketDAO.setTransactionDAO(transactionDAO);
        ticketDAO.setAuthenticationDAO(authenticationDAO);
        ticketDAO.setDescription("BookTicket");
        ticketDAO.setFromMetroStation(source);
        ticketDAO.setJourneyDate("2021-01-29");
        ticketDAO.setTicketFare(20.3);
        ticketDAO.setTicketGUID("1211211");
        ticketDAO.setTicketNo("12345678");
        ticketDAO.setTicketRefId("0987654321");
        ticketDAO.setTicketTransactionId("11222334455");
        ticketDAO.setCreatedAt(new Date());
        ticketDAO.setTicketType("SJT");
        ticketDAO.setToMetroStation(destination);
        ticketDAO.setTransportMode(METRO);
        short noOfTravelers = 1;
        ticketDAO.setTravellers(noOfTravelers);
        ticketDAO.setFromMetroStation(metroStation);
        ticketDAO.setToMetroStation(metroStation);

        ticketDAOList = new ArrayList<>();
        ticketDAOList.add(ticketDAO);

        transactionDAO.setTicketDAO(ticketDAO);

        upcomingTime = Arrays.asList(Time.valueOf("12:00:00"),Time.valueOf("01:00:00"),Time.valueOf("02:00:00"));

        journeyModeDetails = new JourneyModeDetails();
        journeyModeDetails.setSource("ABC");
        journeyModeDetails.setDestination("ABCD");
        journeyModeDetails.setDistance(4000d);
        journeyModeDetails.setTravelTime(600d);

    }

    @Test
    public void getFareTest() throws Exception {
        doReturn(source).when(stationService).getStationById("1234567890");
        doReturn(destination).when(stationService).getStationById("1234567891");
        doReturn(ticketPriceResult).when(kmrlTicketingClient).selTicketPrice(any(String.class), any(String.class), any(String.class),any(String.class), any(String.class));
        doReturn(globalConfig).when(globalConfigService).getGlobalConfig(PAYMENT_OFFERS, true);
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        GetFareResponseDTO fareResponseDTO = bookTicketService.getFare("1234567890", "1234567891", "SJT");
        Assert.assertNotNull(fareResponseDTO);
    }

    @Test
    public void bookTicketTest() throws Exception {

        BookTicketRequestDTO bookTicketRequestDTO = new BookTicketRequestDTO();
        bookTicketRequestDTO.setFromStationId("1234567890");
        bookTicketRequestDTO.setToStationId("1234567891");
        bookTicketRequestDTO.setPaymentMethod(TRANSIT_CARD_PAYMENT_METHOD);
        short noOfTravelers = 1;
        bookTicketRequestDTO.setTravellers(noOfTravelers);
        bookTicketRequestDTO.setTicketType("SJT");
        List<TicketDAO> upcomingTickets = new ArrayList<>();
        TransitCardInfoDTO cardInfoDTO = new TransitCardInfoDTO();
        cardInfoDTO.setTotalHostBalance("1000");
        cardInfoDTO.setCardStatCode("N");
        cardInfoDTO.setCardStatSubCode("N");
        TopupToPrepaid debitFromTransitRequest = new TopupToPrepaid();
        JsonNode transactionstatus = CommonUtils.convertJsonStringToObject("{\"TopupToPrepaidResponse\":{\"ResponseBody\":{\"TopupToPrepaidResult\":{\"Result\":\"success\",\"TxnReferanceId\":\"123456\"}}}}", JsonNode.class);

        doReturn(source).when(stationService).getStationById("1234567890");
        doReturn(destination).when(stationService).getStationById("1234567891");
        doReturn(ticketPriceResult).when(kmrlTicketingClient).selTicketPrice(any(String.class), any(String.class), any(String.class),any(String.class), any(String.class));
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        doReturn(upcomingTickets).when(ticketRepository).findAllByAuthenticationDAO_IdAndTicketStatus(any(long.class), any(String.class));
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        doReturn(cardInfoDTO).when(transitCardTxnService).getTransitCardInfo(any(String.class));
        doReturn("Active").when(transitCardTxnService).getBlockStatus(any(String.class),any(String.class));
        doReturn(debitFromTransitRequest).when(transitCardTxnService).getDebitFromTransitRequest(any(String.class), any(TopupRequest.class));
        doReturn(transactionstatus).when(transitCardClient).topupToPrepaidResponse(any(TopupToPrepaid.class));
        doReturn(transactionDAO).when(paymentService).processBookTicketTxnKmrl(any(String.class),any(TransactionDAO.class));
        doReturn(transactionDAO).when(transactionService).saveTxn(any(TransactionDAO.class));
        doReturn(null).when(journeyPlannerRouteRepository).findByAuthenticationDAOAndIsActive(any(AuthenticationDAO.class), any(boolean.class));
        BookTicketResponseDTO result = bookTicketService.bookTicket(bookTicketRequestDTO);
        Assert.assertNotNull(result);
    }

    @Test
    public void bookTicketTest2() throws Exception {

        BookTicketRequestDTO bookTicketRequestDTO = new BookTicketRequestDTO();
        bookTicketRequestDTO.setFromStationId("1234567890");
        bookTicketRequestDTO.setToStationId("1234567891");
        bookTicketRequestDTO.setPaymentMethod(OTHER_PAYMENT_METHOD);
        short noOfTravelers = 1;
        bookTicketRequestDTO.setTravellers(noOfTravelers);
        bookTicketRequestDTO.setTicketType("SJT");
        List<TicketDAO> upcomingTickets = new ArrayList<>();


        doReturn(source).when(stationService).getStationById("1234567890");
        doReturn(destination).when(stationService).getStationById("1234567891");
        Order order = new Order();
        order.setId("123");
        order.setAmount(45.6);
        order.setOrderId("order123");
        order.setDescription("desc");
        order.setAmountRefunded(23.4);
        order.setCustomerId("werd");
        order.setMerchantId("abc");

        doReturn(order).when(paymentService).createOrder(any(TopupRequest.class), any(String.class), any(String.class));
        doReturn(ticketPriceResult).when(kmrlTicketingClient).selTicketPrice(any(String.class), any(String.class), any(String.class),any(String.class), any(String.class));
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        doReturn(upcomingTickets).when(ticketRepository).findAllByAuthenticationDAO_IdAndTicketStatus(any(long.class), any(String.class));
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        doReturn(transactionDAO).when(paymentService).processBookTicketTxnKmrl(any(String.class),any(TransactionDAO.class));
        doReturn(transactionDAO).when(transactionService).saveTxn(any(TransactionDAO.class));
        doReturn(null).when(journeyPlannerRouteRepository).findByAuthenticationDAOAndIsActive(any(AuthenticationDAO.class), any(boolean.class));
        BookTicketResponseDTO result = bookTicketService.bookTicket(bookTicketRequestDTO);
        Assert.assertNotNull(result);
    }

    @Test
    public void getTicketDetailsTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        doReturn(ticketDAO).when(ticketRepository).findByAuthenticationDAOAndTicketRefId(any(AuthenticationDAO.class), any(String.class));
        JsonNode ticketStatus = CommonUtils.convertJsonStringToObject("{\"GetQrTicketLastStatusResponse\":{\"GetQrTicketLastStatusResult\":{\"Result\":\"1\",\"Status\":\"USED\"}}}", JsonNode.class);
        doReturn(ticketStatus).when(kmrlTicketingClient).getQRTicketLastStatus(any(String.class), any(String.class));
        doReturn(upcomingTime).when(timeTableService).getUpcomingTimings(any(String.class), any(String.class));
        doReturn(journeyModeDetails).when(timeTableService).getMetroRouteDetails(any(String.class), any(String.class));
        doReturn(ticketDAO).when(ticketRepository).save(any(TicketDAO.class));
        TicketDetailsDTO ticketDetailsDTO = bookTicketService.getTicketDetails("123456789");
        Assert.assertNotNull(ticketDetailsDTO);
    }

    @Test
    public void getTicketsTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(ticketRepository.findAllByAuthenticationDAOAndTicketStatusLike(any(AuthenticationDAO.class), any(String.class), any(Pageable.class))).thenReturn(ticketDAOList);
        JsonNode ticketStatus = CommonUtils.convertJsonStringToObject("{\"GetQrTicketLastStatusResponse\":{\"GetQrTicketLastStatusResult\":{\"Result\":\"1\",\"Status\":\"USED\"}}}", JsonNode.class);
        doReturn(ticketStatus).when(kmrlTicketingClient).getQRTicketLastStatus(any(String.class), any(String.class));
        when(ticketRepository.save(any(TicketDAO.class))).thenReturn(ticketDAO);
        Assert.assertNotNull(bookTicketService.getTickets(0, 10, "completed"));
    }

}
