package com.axisbank.transit.payment.repository;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<TransactionDAO, Long> {
    TransactionDAO findByOrderId(String orderId);
    TransactionDAO findByOrderIdAndIsActive(String orderId, Boolean isActive);
    List<TransactionDAO> findAllByServiceProviderAndSpRefIdAndTxnTypeAndFinalTxnStatusOrderByIdDesc(String serviceProvider, String spRefId,
                                                                                   String txnType, String finalTxnStatus, Pageable paging);
    List<TransactionDAO> findAllByAuthenticationDAOAndFinalTxnStatusLikeAndTxnTypeLikeAndPspPaymentMethodTypeLikeAndTxnInitiatedOnBetween(
            AuthenticationDAO authenticationDAO, String txnStatus, String txnType, String paymentMethod, Date date1, Date date2,
            Pageable paging);
    List<TransactionDAO> findAllByAuthenticationDAO_IdAndFinalTxnStatusAndTxnTypeAndServiceProviderLike(long authId,
                                                                                   String txnStatus, String txnType,
                                                                                                    String serviceProvider,
                                                                                                    Pageable pageable);
    List<TransactionDAO> findAllByAuthenticationDAO_Id(long id);
    List<TransactionDAO> findAllByTxnTypeLikeAndFinalTxnStatusInAndTxnInitiatedOnBetween(String type,List<String> status,Date startDate, Date endDate);
}