package com.axisbank.transit.payment.aspect;

import com.axisbank.transit.core.model.response.AddNotificationDTO;
import com.axisbank.transit.core.service.NotificationService;
import com.axisbank.transit.kmrl.model.DAO.TicketDAO;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.payment.repository.TransactionRepository;
import com.axisbank.transit.transitCardAPI.constants.TxnStatus;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.axisbank.transit.core.shared.constants.NotificationConstants.FAIL_STATUS;
import static com.axisbank.transit.core.shared.constants.NotificationConstants.SUCCESS_STATUS;

@Slf4j
@Aspect
@Configuration
public class TransactionAspect {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private NotificationService notificationService;


    @After(value = "execution(* com.axisbank.transit.payment.repository.TransactionRepository.save(..))")
    public void afterSaveTransaction(JoinPoint joinPoint) throws Exception {

        log.info("request receive for sending message after saveTransation method call using AOP");
        AddNotificationDTO addNotificationDTO = null;
        try {
            Object[] allArgs = joinPoint.getArgs();
            TransactionDAO transactionDAO = (TransactionDAO) allArgs[0];
            String transactionType = transactionDAO.getTxnType();
            switch (transactionType) {
                case "BOOK_TICKET":
                    addNotificationDTO = bookTicketNotification(transactionDAO);
                    if (addNotificationDTO !=null) notificationService.saveNotification(addNotificationDTO);
                    break;
                case "TOP_UP":
                    addNotificationDTO = getTopUpNotificationDAO(transactionDAO);
                    if(addNotificationDTO!=null) notificationService.saveNotification(addNotificationDTO);
                    break;
                default:
                    log.info("Transaction is not related to book-ticket");
            }
        } catch (Exception ex) {
            log.error("Failed to send Book ticket notification: {}", ex.getMessage());
        }
    }


    private AddNotificationDTO bookTicketNotification(TransactionDAO transactionDAO) throws Exception {
        TicketDAO ticketDAO = transactionDAO.getTicketDAO();
        String bookingStatus = isBookingSuccessOrFail(transactionDAO);
        if (bookingStatus==null) return null;
        String fromToto = ticketDAO.getFromMetroStation().getDisplayName()+"-"+ticketDAO.getToMetroStation().getDisplayName();
        String typeId = bookingStatus.equalsIgnoreCase(FAIL_STATUS)?transactionDAO.getOrderId():ticketDAO.getTicketRefId();
        return notificationService.getAddNotificationDTOForPurchaseTicket(typeId,bookingStatus,fromToto, transactionDAO.getAuthenticationDAO());
    }

    private String isBookingSuccessOrFail(TransactionDAO transactionDAO) {
        if(transactionDAO.getFinalTxnStatus().equalsIgnoreCase(TxnStatus.DONE.toString())) {
            return SUCCESS_STATUS;
        } else if(transactionDAO.getFinalTxnStatus().equalsIgnoreCase(TxnStatus.FAILED.toString())) {
            return FAIL_STATUS;
        } else{
            log.info("Booking status is not a success/fail");
            return null;
        }
    }

    private AddNotificationDTO getTopUpNotificationDAO(TransactionDAO transactionDAO) {
        try{
            if(transactionDAO.getFinalTxnStatus().equalsIgnoreCase(TxnStatus.DONE.toString())) {
                return notificationService.getAddNotificationDTOForTopUp(transactionDAO.getOrderId(), SUCCESS_STATUS, transactionDAO.getAmount(), transactionDAO.getAuthenticationDAO());
            } else if(transactionDAO.getFinalTxnStatus().equalsIgnoreCase(TxnStatus.FAILED.toString())) {
                return notificationService.getAddNotificationDTOForTopUp(transactionDAO.getOrderId(), FAIL_STATUS, transactionDAO.getAmount(), transactionDAO.getAuthenticationDAO());
            } else{
                log.info("Transaction status is not a success/fail");
                return null;
            }
        } catch (Exception ex){
            log.error("Failed to fetch transaction Info:{}", ex.getMessage());
        }
        return null;
    }
}
