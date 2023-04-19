package com.axisbank.transit.payment.service.impl;

import com.axisbank.transit.authentication.config.ApplicationSetupData;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DAO.TicketDAO;
import com.axisbank.transit.kmrl.repository.TicketRepository;
import com.axisbank.transit.kmrl.service.BookTicketService;
import com.axisbank.transit.kmrl.service.StationService;
import com.axisbank.transit.payment.constants.Helper;
import com.axisbank.transit.payment.constants.ServiceProviderConstant;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.payment.repository.TransactionRepository;
import com.axisbank.transit.payment.service.PaymentService;
import com.axisbank.transit.transitCardAPI.constants.TxnStatus;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.juspay.model.Customer;
import in.juspay.model.Order;
import in.juspay.model.Refund;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.QUICK_BOOK_DEFAULT;
import static com.axisbank.transit.journey.constants.JourneyTypes.METRO;
import static com.axisbank.transit.kmrl.constant.Constants.TRANSIT_CARD_PAYMENT_METHOD;
import static com.axisbank.transit.kmrl.constant.TransitTicketStatus.UPCOMING;
import static com.axisbank.transit.payment.constants.PaymentServiceProviderConstant.PAYMENT_GATEWAY;
import static com.axisbank.transit.payment.constants.PaymentServiceProviderConstant.TRANSIT_CARD;
import static com.axisbank.transit.payment.constants.ServiceProviderConstant.KMRL;
import static com.axisbank.transit.payment.constants.TransactionStatus.DONE;
import static com.axisbank.transit.transitCardAPI.constants.TxnType.BOOK_TICKET;
import static com.axisbank.transit.transitCardAPI.constants.TxnType.REFUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Customer.class, Order.class, ApplicationSetupData.class, BCrypt.class, Helper.class})
@PowerMockIgnore("javax.crypto.*")
public class TransactionServiceTests {
    ObjectMapper mapper = new ObjectMapper();
    private AuthenticationDAO authenticationDAO;
    TicketDAO ticketDAO;
    List<TicketDAO> ticketDAOList;
    TransactionDAO transactionDAO;
    CardDetailsDAO cardDetailsDAO;
    private DAOUser daoUser;
    MetroStation source;
    MetroStation destination;
    GlobalConfigDTO globalConfig=null;
    JsonNode ticketPriceResult;

    @Mock
    TransactionRepository transactionRepository;
    @Mock
    TicketRepository ticketRepository;
    @Mock
    PaymentService paymentService;
    @Mock
    TransitCardTxnService transitCardTxnService;
    @Mock
    GlobalConfigService globalConfigService;
    @Mock
    RedisClient redisClient;
    @Mock
    StationService stationService;
    @Mock
    BookTicketService bookTicketService;
    @Mock
    UserUtil userUtil;
    @InjectMocks
    @Autowired
    TransactionServiceImpl transactionService;

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

        ticketDAOList = new ArrayList<>();
        ticketDAOList.add(ticketDAO);

        transactionDAO.setTicketDAO(ticketDAO);
    }

    @Test
    public void getAllTxnTest() throws Exception {
        TransactionDAO linkedTxn = new TransactionDAO();
        linkedTxn.setOrderId("123");
        transactionDAO.setLinkedTxn(linkedTxn);
        transactionDAO.setTxnType(REFUND.toString());
        transactionDAO.setFinalTxnStatus(TxnStatus.INITIATED.toString());
        List<TransactionDAO> transactionDAOList = new ArrayList<>();
        transactionDAOList.add(transactionDAO);
        when(transactionRepository.findAllByAuthenticationDAOAndFinalTxnStatusLikeAndTxnTypeLikeAndPspPaymentMethodTypeLikeAndTxnInitiatedOnBetween(
                        any(AuthenticationDAO.class), any(String.class), any(String.class), any(String.class),  any(Date.class), any(Date.class), any(Pageable.class))).thenReturn(transactionDAOList);
        Order order = new Order();
        order.setRefunded(true);
        Refund refund = new Refund();
        refund.setAmount(23.4);
        refund.setStatus("Success");
        refund.setReferenceId("123");
        refund.setTxnId("txn123");

        List<Refund> refunds = new ArrayList<>();
        refunds.add(refund);
        order.setRefunds(refunds);

        when(paymentService.orderStatus(any(String.class), any(String.class))).thenReturn(order);
        when(transactionRepository.save(any(TransactionDAO.class))).thenReturn(transactionDAO);
        Assert.assertNotNull(transactionService.getAllTxn(0,10,authenticationDAO,"abc","card","Success", new Date(), new Date()));
    }

    @Test
    public void getRecentBookingsTest() throws Exception {
        transactionDAO.setServiceProvider(KMRL);
        List<TransactionDAO> transactionDAOList = new ArrayList<>();
        transactionDAOList.add(transactionDAO);
        when(transactionRepository.findAllByAuthenticationDAO_IdAndFinalTxnStatusAndTxnTypeAndServiceProviderLike(any(Long.class),
                any(String.class), any(String.class), any(String.class), any(Pageable.class))).thenReturn(transactionDAOList);
        when(ticketRepository.findByAuthenticationDAO_IdAndTicketRefId(any(Long.class), any(String.class))).thenReturn(ticketDAO);
        Assert.assertNotNull(transactionService.getRecentBookings(23));
    }

    @Test
    public void getRecentBookingsTest2() throws Exception {
        transactionDAO.setServiceProvider(KMRL);
        when(redisClient.getValue(any(String.class))).thenReturn(null);

        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setJson(true);
        globalConfigDTO.setValue(null);
        globalConfigDTO.setKey(QUICK_BOOK_DEFAULT);
        String json = "{\n" +
                "                \"defaultOptions\": [\n" +
                "                    {\n" +
                "                        \"source\": \"ALVA\",\n" +
                "                        \"destination\": \"MACE\",\n" +
                "                        \"type\": \"KMRL\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"source\": \"MACE\",\n" +
                "                        \"destination\": \"ALVA\",\n" +
                "                        \"type\": \"KMRL\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"source\": \"EDAP\",\n" +
                "                        \"destination\": \"ALVA\",\n" +
                "                        \"type\": \"KMRL\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }";

        JsonNode jsonNode = mapper.readTree(json);
        globalConfigDTO.setJsonValue(jsonNode);
        when(globalConfigService.getGlobalConfig(QUICK_BOOK_DEFAULT, true)).thenReturn(globalConfigDTO);

        when(stationService.getMetroStationByKMRLCode(any(String.class))).thenReturn(source);
        when(stationService.getMetroStationByKMRLCode(any(String.class))).thenReturn(source);
        when(bookTicketService.getTicketFare(any(String.class),any(String.class), any(Boolean.class))).thenReturn("20.3");

        doNothing().when(redisClient).setValue(any(String.class), any(String.class));

        when(ticketRepository.findByAuthenticationDAO_IdAndTicketRefId(any(Long.class), any(String.class))).thenReturn(ticketDAO);
        Assert.assertNotNull(transactionService.getRecentBookings(23));
    }

    @Test
    public void createRefundTest() throws Exception {
        transactionDAO.setPaymentServiceProvider(PAYMENT_GATEWAY);
        Order order = new Order();
        order.setRefunded(true);
        Refund refund = new Refund();
        refund.setAmount(23.4);
        refund.setStatus("Success");
        refund.setReferenceId("123");
        refund.setTxnId("txn123");

        List<Refund> refunds = new ArrayList<>();
        refunds.add(refund);
        order.setRefunds(refunds);

        when(paymentService.refundOrder(any(String.class), any(String.class), any(Double.class), any(String.class))).thenReturn(order);
        when(transactionRepository.save(any(TransactionDAO.class))).thenReturn(transactionDAO);
        transactionService.createRefund(transactionDAO);
    }

    @Test
    public void createRefundTest2() throws Exception {
        transactionDAO.setPaymentServiceProvider(TRANSIT_CARD);
        Mockito.doNothing().when(transitCardTxnService).createTransitCardRefund(any(String.class), any(String.class));
        when(transactionRepository.save(any(TransactionDAO.class))).thenReturn(transactionDAO);
        transactionService.createRefund(transactionDAO);
    }

    @Test
    public void getAllFiltersTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        List<TransactionDAO> transactionDAOList = new ArrayList<>();
        transactionDAOList.add(transactionDAO);
        when(transactionRepository.findAllByAuthenticationDAO_Id(any(Long.class))).thenReturn(transactionDAOList);
        Assert.assertNotNull(transactionService.getAllFilters());
    }

    @Test
    public void getAllTransactionsTest() throws Exception {
        transactionDAO.setTxnType(REFUND.toString());
        transactionDAO.setPspStatus(DONE.toString());
        List<TransactionDAO> transactionDAOList = new ArrayList<>();
        transactionDAOList.add(transactionDAO);
        when(transactionRepository.findAllByTxnTypeLikeAndFinalTxnStatusInAndTxnInitiatedOnBetween(any(String.class),anyList(),any(Date.class), any(Date.class))).thenReturn(transactionDAOList);
        PowerMockito.mockStatic(Helper.class);
        PowerMockito.when(Helper.getPaymentStatus(any(String.class))).thenReturn("abc");
        Date date = new Date();
        Assert.assertNotNull(transactionService.getAllTransactions(date, date, "Success", "abc"));
    }

}
