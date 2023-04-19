package com.axisbank.transit.payment.service;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.response.QuickBookDTO;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.payment.model.DTO.TransactionAdminDetailsDTO;
import com.axisbank.transit.payment.model.DTO.TransactionDetailsDTO;
import com.axisbank.transit.payment.model.DTO.TransactionFiltersDTO;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface TransactionService {
    TransactionDAO saveTxn(TransactionDAO transactionDAO);
    TransactionDAO getTnxByOrderId(String orderId);
    List<TransactionDAO> getTxnByTypeAndSp(String serviceProvider, String sprefId, String txnType, Pageable paging);
    List<TransactionDetailsDTO> getAllTxn(int pageNo, int pageSize, AuthenticationDAO authenticationDAO,
                                          String category, String paymentType, String status, Date fromDate,Date toDate);
    List<QuickBookDTO> getRecentBookings(long authId);
    List<QuickBookDTO> getRecentBookings(long authId, String serviceProvider);
    void createRefund(TransactionDAO txn);
    TransactionFiltersDTO getAllFilters() throws Exception;
    TransactionDAO updateRefundOrderStatus(TransactionDAO linkedTxn);
    List<TransactionAdminDetailsDTO> getAllTransactions(Date startDate, Date endDate, String status, String type) throws Exception;
    TransactionDAO createRefund(TransactionDAO txn, Double amount);
}
