package com.axisbank.transit.payment.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.model.response.JourneyLocationDTO;
import com.axisbank.transit.core.model.response.QuickBookDTO;
import com.axisbank.transit.core.model.response.QuickBookDefaultDTO;
import com.axisbank.transit.core.model.response.QuickBookDefaultDetailsDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DAO.TicketDAO;
import com.axisbank.transit.kmrl.repository.TicketRepository;
import com.axisbank.transit.kmrl.service.BookTicketService;
import com.axisbank.transit.kmrl.service.StationService;
import com.axisbank.transit.payment.constants.CommonConstants;
import com.axisbank.transit.payment.constants.Helper;
import com.axisbank.transit.payment.constants.ServiceProviderConstant;
import com.axisbank.transit.payment.constants.TransactionStatus;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.payment.model.DTO.TransactionAdminDetailsDTO;
import com.axisbank.transit.payment.model.DTO.TransactionDetailsDTO;
import com.axisbank.transit.payment.model.DTO.TransactionFiltersDTO;
import com.axisbank.transit.payment.repository.TransactionRepository;
import com.axisbank.transit.payment.service.PaymentService;
import com.axisbank.transit.payment.service.TransactionService;
import com.axisbank.transit.transitCardAPI.constants.TxnStatus;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.juspay.model.Order;
import in.juspay.model.Refund;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.QUICK_BOOK_DEFAULT;
import static com.axisbank.transit.kmrl.constant.Constants.TRANSIT_CARD_PAYMENT_METHOD;
import static com.axisbank.transit.kmrl.constant.KmrlTicketTypes.TICKET_TYPE_MAP;
import static com.axisbank.transit.payment.constants.PaymentServiceProviderConstant.PAYMENT_GATEWAY;
import static com.axisbank.transit.payment.constants.PaymentServiceProviderConstant.TRANSIT_CARD;
import static com.axisbank.transit.payment.constants.ServiceProviderConstant.KMRL;
import static com.axisbank.transit.payment.constants.TransactionStatus.*;
import static com.axisbank.transit.transitCardAPI.constants.TxnType.*;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    PaymentService paymentService;
    @Autowired
    TransitCardTxnService transitCardTxnService;
    @Autowired
    GlobalConfigService globalConfigService;
    @Autowired
    RedisClient redisClient;
    @Autowired
    StationService stationService;
    @Autowired
    BookTicketService bookTicketService;
    @Autowired
    UserUtil userUtil;

    @Override
    public TransactionDAO saveTxn(TransactionDAO transactionDAO) {
        return transactionRepository.save(transactionDAO);
    }

    @Override
    public TransactionDAO getTnxByOrderId(String orderId) {
        return transactionRepository.findByOrderIdAndIsActive(orderId, true);
    }

    @Override
    public List<TransactionDAO> getTxnByTypeAndSp(String serviceProvider, String sprefId, String txnType, Pageable paging) {

        return transactionRepository.findAllByServiceProviderAndSpRefIdAndTxnTypeAndFinalTxnStatusOrderByIdDesc(serviceProvider, sprefId, txnType, TransactionStatus.DONE.toString(), paging);
    }

    @Override
    public List<TransactionDetailsDTO> getAllTxn(int pageNo, int pageSize, AuthenticationDAO authenticationDAO,
                                                 String category, String paymentType, String status, Date fromDate,
                                                 Date toDate) {
        Pageable paging = PageRequest.of(pageNo,pageSize, Sort.by("txnCompletedOn").descending());
        List<TransactionDAO> transactionDAOS = transactionRepository.
                findAllByAuthenticationDAOAndFinalTxnStatusLikeAndTxnTypeLikeAndPspPaymentMethodTypeLikeAndTxnInitiatedOnBetween(
                        authenticationDAO, status, category, paymentType,  fromDate, toDate, paging);
        List<TransactionDetailsDTO> transactionDetailsDTOS = new ArrayList<>();
        for (TransactionDAO transactionDAO:transactionDAOS){
            TransactionDetailsDTO transactionDetailsDTO = new TransactionDetailsDTO();
//          Update txnStatus to DONE if txnType is REFUND and current order status is refunded
            try {
                if(transactionDAO.getTxnType().equalsIgnoreCase(REFUND.toString()) &&
                        transactionDAO.getFinalTxnStatus().equalsIgnoreCase(TxnStatus.INITIATED.toString())) {
                    transactionDAO =updateRefundOrderStatus(transactionDAO);
                }
            } catch (Exception ex) {
                log.error("Failed to update RefundOrder Status: {}",ex.getMessage());
            }
            transactionDetailsDTO.setAmount(String.valueOf(transactionDAO.getAmount()));
            transactionDetailsDTO.setDateAndTime(transactionDAO.getTxnCompletedOn());
            transactionDetailsDTO.setPaidTO(transactionDAO.getServiceProvider().replaceAll("_", " "));
            transactionDetailsDTO.setPaymentProvider((transactionDAO.getPaymentServiceProvider().equals(TRANSIT_CARD)) ? TRANSIT_CARD_PAYMENT_METHOD : transactionDAO.getPaymentServiceProvider());
            transactionDetailsDTO.setStatus(transactionDAO.getFinalTxnStatus());
            transactionDetailsDTO.setTransactionId(transactionDAO.getOrderId());
            transactionDetailsDTO.setType(transactionDAO.getTxnType());
            transactionDetailsDTO.setPaymentMethod(transactionDAO.getPspPaymentMethod());
            transactionDetailsDTOS.add(transactionDetailsDTO);

        }
        return transactionDetailsDTOS;
    }

    public TransactionDAO updateRefundOrderStatus(TransactionDAO transactionDAO)  {
        if(transactionDAO.getLinkedTxn() == null) {
            return  transactionDAO;
        }
        String orderId = transactionDAO.getLinkedTxn().getOrderId();
        Order order;
        try {
            order= paymentService.orderStatus(orderId, transactionDAO.getTxnType().toUpperCase());
        } catch (Exception ex) {
            log.error("Failed to fetch order status");
            return transactionDAO;
        }
        if(order.getRefunded()) {
            List<Refund> refunds = order.getRefunds();
            if (refunds.size()>0 ){

                Refund firstRefund = refunds.get(0);
                transactionDAO.setPspStatus(firstRefund.getStatus());
                transactionDAO.setPspRefId(firstRefund.getReferenceId());
                transactionDAO.setPspTxnId(firstRefund.getTxnId());
                if(firstRefund.getStatus().equalsIgnoreCase("Success"))
                transactionDAO.setFinalTxnStatus(TxnStatus.DONE.toString());
                else if(firstRefund.getStatus().equalsIgnoreCase("Failure"))
                    transactionDAO.setFinalTxnStatus(TxnStatus.FAILED.toString());

                transactionRepository.save(transactionDAO);
            }
        }
        return transactionDAO;
    }

    @Override
    public List<QuickBookDTO> getRecentBookings(long authId) {
        return getRecentBookings(authId, "%");
    }
    @Override
    public List<QuickBookDTO> getRecentBookings(long authId, String serviceProvider) {
        Pageable paging = PageRequest.of(0,5, Sort.by("id").descending());
        List<TransactionDAO> transactions = transactionRepository.findAllByAuthenticationDAO_IdAndFinalTxnStatusAndTxnTypeAndServiceProviderLike(authId,
                DONE.toString(), BOOK_TICKET.toString(), serviceProvider, paging);
        if(transactions==null || transactions.size()<1){
            return getDefaultQuickBooks();
        }
        JourneyLocationDTO journeyLocationSource = null;
        JourneyLocationDTO journeyLocationDestination = null;
        QuickBookDTO quickBook =null;
        List<QuickBookDTO> allBookings = new ArrayList<>();
        for(TransactionDAO tdo: transactions){
            String spType = tdo.getServiceProvider();
            switch (spType){
                case KMRL:
                    TicketDAO ticketDAO = ticketRepository.findByAuthenticationDAO_IdAndTicketRefId(authId,
                            tdo.getSpRefId());
                    MetroStation sourceMetro = ticketDAO.getFromMetroStation();
                    MetroStation destinationMetro = ticketDAO.getToMetroStation();
                    journeyLocationSource = new JourneyLocationDTO(sourceMetro.getDisplayName(), sourceMetro.getLatitude().toString(),
                            sourceMetro.getLongitude().toString(), sourceMetro.getStationId());
                    journeyLocationDestination = new JourneyLocationDTO(destinationMetro.getDisplayName(), destinationMetro.getLatitude().toString(),
                            destinationMetro.getLongitude().toString(), destinationMetro.getStationId());
                    quickBook = new QuickBookDTO("metro", Double.toString(tdo.getAmount()), tdo.getPspPaymentMethodType(), tdo.getPspPaymentMethod(), journeyLocationSource, journeyLocationDestination);
                    String ticketType = ticketDAO.getTicketType();
                    quickBook.setTicketType(ticketType);
                    quickBook.setTicketTypeDispName(TICKET_TYPE_MAP.get(ticketType).getDisplayName());
                    allBookings.add(quickBook);
            }
        }
        return allBookings;
    }
    private List<QuickBookDTO> getDefaultQuickBooks(){
        String redisKey = "quick_book_default";
        try{
            String quickBook = redisClient.getValue(redisKey);
            if(quickBook!=null){
                return mapper.readValue(quickBook, new TypeReference<List<QuickBookDTO>>(){});
            }
        } catch (Exception ex){
            log.info("Error while fetching quickbook from redis:{}",ex.getMessage());
        }

        List<QuickBookDTO> allBookings = new ArrayList<>();
        GlobalConfigDTO quickBookConfig = globalConfigService.getGlobalConfig(QUICK_BOOK_DEFAULT, true);
        if(quickBookConfig==null)
            return allBookings;
        QuickBookDefaultDTO node = mapper.treeToValue(quickBookConfig.getJsonValue(), QuickBookDefaultDTO.class);
        List<QuickBookDefaultDetailsDTO> quickBookOptions = node.getDefaultOptions();
        JourneyLocationDTO journeyLocationSource = null;
        JourneyLocationDTO journeyLocationDestination = null;
        QuickBookDTO quickBook =null;
        for(QuickBookDefaultDetailsDTO bookDefaultDTO:quickBookOptions){
            String spType = bookDefaultDTO.getType();
            switch (spType){
                case KMRL:
                    try{
                        MetroStation sourceMetro = stationService.getMetroStationByKMRLCode(bookDefaultDTO.getSource());
                        MetroStation destinationMetro = stationService.getMetroStationByKMRLCode(bookDefaultDTO.getDestination());
                        String fare = bookTicketService.getTicketFare(bookDefaultDTO.getSource(),bookDefaultDTO.getDestination(), true);
                        journeyLocationSource = new JourneyLocationDTO(sourceMetro.getDisplayName(), sourceMetro.getLatitude().toString(),
                                sourceMetro.getLongitude().toString(), sourceMetro.getStationId());
                        journeyLocationDestination = new JourneyLocationDTO(destinationMetro.getDisplayName(), destinationMetro.getLatitude().toString(),
                                destinationMetro.getLongitude().toString(), destinationMetro.getStationId());
                        quickBook = new QuickBookDTO("metro", fare, "", "", journeyLocationSource, journeyLocationDestination);
                        quickBook.setTicketType("SJT");
                        quickBook.setTicketTypeDispName(TICKET_TYPE_MAP.get("SJT").getDisplayName());
                        allBookings.add(quickBook);
                    } catch (Exception ex){
                        log.error("Failed to add route to quick book:{}",ex.getMessage());
                    }
                    break;
                default:
                    log.info("Invalid Travel type");
            }
        }
        try{
            if(!allBookings.isEmpty()){
                String qb = CommonUtils.convertObjectToJsonString(allBookings);
                redisClient.setValue(redisKey,qb);
            }
        } catch (Exception ex){
            log.error("Failed to set redis data, exception:{}",ex.getMessage());
        }
        return allBookings;
    }

    @Override
    public void createRefund(TransactionDAO txn){
        createRefund(txn, txn.getAmount());
    }
    @Override
    public TransactionDAO createRefund(TransactionDAO txn, Double amount){
        TransactionDAO refundTxn = new TransactionDAO();
        refundTxn.setOrderId(CommonUtils.generateRandInt(10));
        refundTxn.setAmount(amount);
        refundTxn.setLinkedTxn(txn);
        refundTxn.setTxnType(REFUND.toString());
        refundTxn.setFinalTxnStatus(INITIATED.toString());
        String pspType = txn.getPaymentServiceProvider();
        refundTxn.setPaymentServiceProvider(txn.getServiceProvider());
        refundTxn.setServiceProvider(txn.getPspPaymentMethod());
        refundTxn.setSpTxnId(txn.getPspTxnId());
        refundTxn.setSpStatus(txn.getPspStatus());
        refundTxn.setSpRefId(txn.getPspRefId());
        refundTxn.setAuthenticationDAO(txn.getAuthenticationDAO());
        refundTxn.setTxnCompletedOn(new Date(CommonUtils.getCurrentTimeMillis()));
        refundTxn.setPspPaymentMethodType(txn.getServiceProvider());
        refundTxn.setPspPaymentMethod(txn.getServiceProvider());
        try {
            switch (pspType){
                case PAYMENT_GATEWAY:
                    Order refund = paymentService.refundOrder(txn.getOrderId(), refundTxn.getOrderId(), amount,
                            txn.getTxnType().toUpperCase());
                    List<Refund> refunds = refund.getRefunds();
                    if (refund.getRefunded() && refunds.size()>0){
                        Refund firstRefund = refunds.get(0);
                        refundTxn.setFinalTxnStatus(firstRefund.getStatus());
                        refundTxn.setPspStatus(firstRefund.getStatus());
                        refundTxn.setPspRefId(firstRefund.getReferenceId());
                        refundTxn.setPspTxnId(firstRefund.getTxnId());
                    }
                    break;
                case TRANSIT_CARD:
                    try{
                        transitCardTxnService.createTransitCardRefund(txn.getPspRefId(), txn.getPspTxnId());
                        refundTxn.setFinalTxnStatus(DONE.toString());
                        refundTxn.setPspStatus(DONE.toString());
                        refundTxn.setPspRefId(txn.getPspRefId());
                        refundTxn.setPspTxnId(txn.getPspTxnId());
                    } catch (Exception ex){
                        log.info("Failed to reverse Kochi1 card transaction");
                        refundTxn.setFinalTxnStatus(FAILED.toString());
                        refundTxn.setPspStatus(FAILED.toString());
                        refundTxn.setPspRefId(txn.getPspRefId());
                        refundTxn.setPspTxnId(txn.getPspTxnId());
                        throw ex;
                    }
                    break;
                default:
                    log.debug("Payment Service provider is invalid");
                    throw new Exception("Payment Service provider is invalid");
            }

        } catch (Exception ex){
            log.info("Failed to refund due to exception:{}",ex.getMessage());
        }
        return transactionRepository.save(refundTxn);
    }

    @Override
    public TransactionFiltersDTO getAllFilters() throws Exception {
        log.info("Request Received in getAllFilters: ");
        Set<String> statuses = new HashSet<>();
        Set<String> categories = new HashSet<>();
        Set<String> paymentTypes = new HashSet<>();
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            List<TransactionDAO> transactionDAOList = transactionRepository.findAllByAuthenticationDAO_Id(authenticationDAO.getId());
            for (TransactionDAO transactionDAO : transactionDAOList) {
                CollectionUtils.addIgnoreNull(statuses, transactionDAO.getFinalTxnStatus());
                CollectionUtils.addIgnoreNull(categories, transactionDAO.getTxnType());
                CollectionUtils.addIgnoreNull(paymentTypes, transactionDAO.getPspPaymentMethodType());
            }

            TransactionFiltersDTO transactionFiltersDTO = new TransactionFiltersDTO();
            transactionFiltersDTO.setStatuses(statuses);
            transactionFiltersDTO.setCategories(categories);
            transactionFiltersDTO.setPaymentTypes(paymentTypes);

            return transactionFiltersDTO;
        } catch (Exception exception) {
            log.error("Error in getting Txn Filters: {}",exception.getMessage());
            throw new Exception("Error in Getting Transaction Filters.");
        }

    }

    @Override
    public List<TransactionAdminDetailsDTO> getAllTransactions(Date startDate, Date endDate, String status, String type) throws Exception {
        try{
            List<String> statusList = Arrays.asList(status.split(","));
            List<TransactionDAO> transactionDAOList = transactionRepository.findAllByTxnTypeLikeAndFinalTxnStatusInAndTxnInitiatedOnBetween(type,statusList,startDate, endDate);

            List <TransactionAdminDetailsDTO> transactionList = new ArrayList<>();

            for(TransactionDAO transactionDAO: transactionDAOList){

                AuthenticationDAO authenticationDAO = transactionDAO.getAuthenticationDAO();
                DAOUser daoUser = authenticationDAO.getDaoUser();
                TicketDAO ticketDAO = transactionDAO.getTicketDAO();
                Set<TransactionDAO> refunds = transactionDAO.getAllLinkedTxns();
                TransactionDAO refundId = !refunds.isEmpty()? refunds.stream().findFirst().get(): null;

                TransactionAdminDetailsDTO transactionAdminDetailsDTO = new TransactionAdminDetailsDTO();
                transactionAdminDetailsDTO.setUserId(daoUser.getUserId()!=null? daoUser.getUserId(): CommonConstants.NA);
                transactionAdminDetailsDTO.setMobile(authenticationDAO.getMobile()!= null? CommonUtils.maskString(authenticationDAO.getMobile(),2,5,'*'): CommonConstants.NA);
                Date completionDate = transactionDAO.getTxnCompletedOn();
                String date = "";
                String time = "";
                if (completionDate!=null){
                    date = CommonUtils.getDateFormat(transactionDAO.getTxnCompletedOn(), "yyyy-MM-dd");
                    time = CommonUtils.getDateFormat(transactionDAO.getTxnCompletedOn(), "HH:mm:ss");
                }
                transactionAdminDetailsDTO.setDate(date);
                transactionAdminDetailsDTO.setTime(time);
                transactionAdminDetailsDTO.setTxnType(transactionDAO.getTxnType());
                transactionAdminDetailsDTO.setTxnId(transactionDAO.getOrderId());
                transactionAdminDetailsDTO.setRefundId(refundId != null? refundId.getOrderId(): CommonConstants.NA);
                transactionAdminDetailsDTO.setTopUpType(transactionDAO.getTxnType().equals(TOP_UP.toString())? CommonConstants.MANUAL: CommonConstants.NA); //TODO:  Manual / Auto-topup / NA to be added
                transactionAdminDetailsDTO.setTicketType(ticketDAO!=null && ticketDAO.getTicketType()!= null ? Helper.getJourneyType(ticketDAO.getTicketType()) : CommonConstants.NA);
                transactionAdminDetailsDTO.setAutoBookingType(CommonConstants.NA); //TODO: Pass / Non-pass / NA to be added
                transactionAdminDetailsDTO.setBillTo(transactionDAO.getServiceProvider());
                transactionAdminDetailsDTO.setTransactionAmount(transactionDAO.getAmount());
                transactionAdminDetailsDTO.setPaymentMode(transactionDAO.getPaymentServiceProvider());
                transactionAdminDetailsDTO.setPaymentStatus(transactionDAO.getTxnType().equalsIgnoreCase(REFUND.toString())
                        && transactionDAO.getPspStatus().equalsIgnoreCase(DONE.toString()) ? REFUNDED.toString() :transactionDAO.getPspStatus());
                transactionAdminDetailsDTO.setPaymentGatewayTxnId(!transactionDAO.getPaymentServiceProvider().equals(TRANSIT_CARD) && transactionDAO.getPspTxnId()!=null?transactionDAO.getPspTxnId(): CommonConstants.NA);
                transactionAdminDetailsDTO.setTransactionStatus(Helper.getPaymentStatus(transactionDAO.getFinalTxnStatus()));
                String proceedTxnID = transactionDAO.getPaymentServiceProvider().equals(TRANSIT_CARD) && transactionDAO.getPspTxnId() != null ?
                        transactionDAO.getPspTxnId(): transactionDAO.getServiceProvider().equalsIgnoreCase(ServiceProviderConstant.TRANSIT_CARD)?transactionDAO.getSpTxnId():CommonConstants.NA;
                transactionAdminDetailsDTO.setProceedTxnId(proceedTxnID);
                transactionAdminDetailsDTO.setSource(ticketDAO != null && ticketDAO.getFromMetroStation() != null ? ticketDAO.getFromMetroStation().getDisplayName() : CommonConstants.NA);
                transactionAdminDetailsDTO.setDestination(ticketDAO != null && ticketDAO.getToMetroStation() != null ? ticketDAO.getToMetroStation().getDisplayName() : CommonConstants.NA);
                transactionAdminDetailsDTO.setCouponId(CommonConstants.NA);
                transactionAdminDetailsDTO.setPayableAmount(transactionDAO.getAmount());
                transactionAdminDetailsDTO.setTransactionReferenceNumber(transactionDAO.getPspTxnId()!=null? transactionDAO.getPspTxnId(): CommonConstants.NA);
                transactionAdminDetailsDTO.setAuthCode(CommonConstants.NA);
                transactionAdminDetailsDTO.setMerchantCoupon(CommonConstants.NA);
                transactionAdminDetailsDTO.setAfcTxnId(ticketDAO != null && ticketDAO.getTicketTransactionId() != null ? ticketDAO.getTicketTransactionId() :CommonConstants.NA);
                transactionAdminDetailsDTO.setUserName(daoUser!= null? CommonUtils.getFullName(daoUser.getFirstName(), daoUser.getMiddleName(), daoUser.getLastName()): CommonConstants.NA);
                transactionAdminDetailsDTO.setMerchantId(transactionDAO.getMerchantId()!=null?transactionDAO.getMerchantId():CommonConstants.NA);
                transactionList.add(transactionAdminDetailsDTO);
            }

            return transactionList;
        } catch (Exception exception){
            log.error("Error in Gettig Transactions: {}",exception.getMessage());
            throw new Exception("Error in Gettig Transactions");
        }
    }
}
