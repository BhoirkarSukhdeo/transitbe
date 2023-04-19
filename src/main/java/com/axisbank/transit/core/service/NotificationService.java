package com.axisbank.transit.core.service;


import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.response.AddNotificationDTO;
import com.axisbank.transit.core.model.response.NotificationDTO;
import com.axisbank.transit.core.model.response.NotificationDetailDTO;

import java.util.List;

public interface NotificationService {
       List<NotificationDTO> getNotifications(int page, int size) throws Exception;
       NotificationDetailDTO getNotificationDetail(String id) throws Exception;
       void saveNotification(AddNotificationDTO addNotificationDTO) throws Exception;
       AddNotificationDTO  getAddNotificationDTOForTopUp(String typeId, String status, Double amount, AuthenticationDAO authenticationDAO)throws Exception;
       AddNotificationDTO getAddNotificationDTOForPurchaseTicket(String typeId,String status,String fromTotoStations, AuthenticationDAO authenticationDAO) throws Exception;
       void saveNotification(List<AuthenticationDAO> authenticationDAOS, AddNotificationDTO addNotificationDTO);
}
