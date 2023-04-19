package com.axisbank.transit.payment.service.impl;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.config.ApplicationSetupData;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.kmrl.client.KmrlTicketingClient;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DAO.TicketDAO;
import com.axisbank.transit.payment.constants.OrderStatus;
import com.axisbank.transit.payment.constants.ServiceProviderConstant;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.payment.service.TransactionService;
import com.axisbank.transit.transitCardAPI.TransitCardClient.TransitCardClient;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.request.TopupRequest;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid.TopupToPrepaid;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.juspay.exception.*;
import in.juspay.model.Customer;
import in.juspay.model.Order;
import in.juspay.model.RequestOptions;
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
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.axisbank.transit.journey.constants.JourneyTypes.METRO;
import static com.axisbank.transit.kmrl.constant.Constants.TRANSIT_CARD_PAYMENT_METHOD;
import static com.axisbank.transit.kmrl.constant.TransitTicketStatus.UPCOMING;
import static com.axisbank.transit.payment.constants.PaymentServiceProviderConstant.TRANSIT_CARD;
import static com.axisbank.transit.transitCardAPI.constants.TxnType.BOOK_TICKET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Customer.class, Order.class, ApplicationSetupData.class, BCrypt.class})
@PowerMockIgnore("javax.crypto.*")
public class PaymentServiceImplTest extends BaseTest {
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

    @InjectMocks
    @Autowired
    private PaymentServiceImpl paymentService;

    @Mock
    private TransitCardTxnService transitCardTxnService;

    @Mock
    private TransitCardClient transitCardClient;

    @Mock
    private TransactionService transactionService;

    @Mock
    KmrlTicketingClient kmrlTicketingClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Customer.class);
        PowerMockito.mockStatic(Order.class);
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
    public void createCustomerTest() throws APIException, AuthorizationException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Customer customer =   new Customer();
        PowerMockito.when(Customer.create(any(Map.class))).thenReturn(customer);
        paymentService.createCustomer("1122665522","abc");
    }

    @Test
    public void createOrderTest() throws Exception {
        Order order = new Order();
        PowerMockito.when(Order.create(any(Map.class), any(RequestOptions.class))).thenReturn(order);
        paymentService.createOrder(new TopupRequest(), "cust123", "TOP_UP");
    }

    @Test
    public void refundOrderTest() throws Exception {
        Order order = new Order();
        PowerMockito.when(Order.refund(any(String.class), any(Map.class), any(RequestOptions.class))).thenReturn(order);
        paymentService.refundOrder("123", "cust123",34.8, "TOP_UP");
    }

    @Test
    public void processTransactionTest() throws Exception {
        transactionDAO.setServiceProvider(ServiceProviderConstant.KMRL);
        when(transactionService.getTnxByOrderId(any(String.class))).thenReturn(transactionDAO);
        when(transactionService.saveTxn(any(TransactionDAO.class))).thenReturn(transactionDAO);

        Order order = new Order();
        order.setOrderId("123");
        order.setStatus(OrderStatus.CHARGED.toString());
        order.setCustomerId("cust123");
        order.setAmount(23.4);
        order.setPaymentMethod("card");
        order.setPaymentMethodType("online");

        PowerMockito.when(Order.status(any(String.class), any(RequestOptions.class))).thenReturn(order);

        String ticketResp = "{\"InsQrCodeTicketMobileResponse\":{\"InsQrCodeTicketMobileResult\":{\"errorCode\":\"Error\",\"errorMsg\":\"Error\",\"ticketGUID\":\"acajaj22Aksdjao23n\",\"ticketNo\":\"transit123\",\"result\":\"200\",\"description\":\"Success\",\"token\":\"1212121dwdfwfwfw\",\"transactionId\":\"121232121231\"}}}";
        JsonNode ticketRespNode = mapper.readTree(ticketResp);
        when(kmrlTicketingClient.insQrCodeTicketMobile(anyList())).thenReturn(ticketRespNode);
        when(transactionService.getTnxByOrderId(any(String.class))).thenReturn(transactionDAO);
        Assert.assertEquals(true, paymentService.processTransaction("123"));
    }

    @Test
    public void processTransactionTest2() throws Exception {
        transactionDAO.setServiceProvider(ServiceProviderConstant.TRANSIT_CARD);
        when(transactionService.getTnxByOrderId(any(String.class))).thenReturn(transactionDAO);
        when(transactionService.saveTxn(any(TransactionDAO.class))).thenReturn(transactionDAO);

        Order order = new Order();
        order.setOrderId("123");
        order.setStatus(OrderStatus.CHARGED.toString());
        order.setCustomerId("cust123");
        order.setAmount(23.4);
        order.setPaymentMethod("card");
        order.setPaymentMethodType("online");

        PowerMockito.when(Order.status(any(String.class), any(RequestOptions.class))).thenReturn(order);

        when(transitCardTxnService.getTransitCardNo(any(String.class))).thenReturn("abc");
        TopupToPrepaid topupToPrepaid = new TopupToPrepaid();
        when(transitCardTxnService.getTopupToPrepaidRequest(any(String.class), any(TopupRequest.class))).thenReturn(topupToPrepaid);
        String txnStatusResp = "{\"TopupToPrepaidResponse\":{\"ResponseBody\":{\"TopupToPrepaidResult\":{\"ReturnDescription\":\"Successfully completed.\",\"ErrorDetail\":\"\",\"ReturnCode\":\"2\",\"TxnReferanceId\":\"40012031\",\"TotalAmount\":\"200\",\"TopupAmount\":\"200\",\"Result\":\"Success\",\"FeeAmount\":\"0\"}}}}";
        JsonNode transactionstatus = mapper.readTree(txnStatusResp);
        when(transitCardClient.topupToPrepaidResponse(any(TopupToPrepaid.class))).thenReturn(transactionstatus);
        when(transactionService.getTnxByOrderId(any(String.class))).thenReturn(transactionDAO);
        Assert.assertEquals(true, paymentService.processTransaction("123"));
    }

    @Test
    public void getPaymentStatusTest() throws Exception {
        transactionDAO.setCreatedAt(new Date());
        when(transactionService.getTnxByOrderId(any(String.class))).thenReturn(transactionDAO);
        Assert.assertNotNull(paymentService.getPaymentStatus("123"));
    }
}
