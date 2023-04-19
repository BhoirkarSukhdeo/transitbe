package com.axisbank.transit.core.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.NotificationDAO;
import com.axisbank.transit.core.model.response.AddNotificationDTO;
import com.axisbank.transit.core.model.response.NotificationDTO;
import com.axisbank.transit.core.model.response.NotificationDetailDTO;
import com.axisbank.transit.core.repository.NotificationRepository;
import com.axisbank.transit.core.service.NotificationService;
import com.axisbank.transit.core.shared.constants.NotificationConstants;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.PushNotificationUtil;
import com.axisbank.transit.userDetails.model.DAO.DeviceInfo;
import com.axisbank.transit.userDetails.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.axisbank.transit.core.shared.constants.ClickActions.homeScreen;
import static com.axisbank.transit.core.shared.constants.ClickActions.viewTickets;
import static com.axisbank.transit.core.shared.constants.NotificationConstants.TICKET_FAILED;

@Slf4j
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserUtil userUtil;

    @Autowired
    PushNotificationUtil pushNotificationUtil;

    public List<NotificationDTO> getNotifications(int page, int size) throws Exception {
        log.info("Request received in getNotifications method");
        List<NotificationDTO> notificationDTOList = new ArrayList<>();
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        Pageable requestedPage = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        try {
            List<NotificationDAO> notificationDAOList = notificationRepository.findAllByAuthenticationDAO(authenticationDAO, requestedPage);
            for(NotificationDAO notification: notificationDAOList){
                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setId(notification.getNotificationRefId());
                notificationDTO.setTypeId(notification.getTypeId());
                notificationDTO.setType(notification.getType());
                notificationDTO.setTitle(notification.getTitle());
                notificationDTO.setSubTitle(notification.getSubTitle());
                notificationDTO.setStatus(notification.getStatus());
                notificationDTO.setSeen(notification.isSeen());
                notificationDTO.setDateTime(notification.getCreationDateTime());

                if (notification.getExpiryDate() != null) {
                    String currentDateInString = CommonUtils.currentDateTime("yyyy-MM-dd HH:mm:ss");
                    Date currentDateTime = CommonUtils.getDate(currentDateInString, "yyyy-MM-dd HH:mm:ss");
                    if (notification.getExpiryDate().before(currentDateTime)) {
                        continue;
                    }
                }

                notificationDTOList.add(notificationDTO);
            }
        } catch (Exception exception) {
            log.error("Exception in getting notification list: {}", exception.getMessage());
            throw exception;
        }
        return notificationDTOList;
    }

    public NotificationDetailDTO getNotificationDetail(String notificationRefId) throws Exception {
        log.info("Request received in getNotificationDetail method");
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            NotificationDAO notificationDAO = notificationRepository.findByNotificationRefIdAndAuthenticationDAO(notificationRefId,authenticationDAO);
            NotificationDetailDTO notificationDetail = new NotificationDetailDTO();
            notificationDetail.setTitle(notificationDAO.getTitle());
            notificationDetail.setSubTitle(notificationDAO.getSubTitle());
            notificationDetail.setDescription(notificationDAO.getBody());
            notificationDetail.setBannerUrl(notificationDAO.getBannerUrl());
            return notificationDetail;
        } catch(Exception exception){
            log.error("Exception in getting notification details: {}", exception.getMessage());
            throw  exception;
        }
    }

    public void saveNotification(AddNotificationDTO addNotificationDTO) throws Exception {
        log.info(("Request received in save notification"));
        try {
            NotificationDAO notificationDAO = new NotificationDAO();
            notificationDAO.setNotificationRefId(CommonUtils.generateRandomString(30));
            notificationDAO.setStatus(addNotificationDTO.getStatus());
            if(addNotificationDTO.getTypeId()==null){
                notificationDAO.setTypeId(notificationDAO.getNotificationRefId());
            } else {
                notificationDAO.setTypeId(addNotificationDTO.getTypeId());
            }
            notificationDAO.setType(addNotificationDTO.getType());
            notificationDAO.setAction(addNotificationDTO.getAction());
            notificationDAO.setTitle(addNotificationDTO.getTitle());
            notificationDAO.setSubTitle(addNotificationDTO.getSubTitle());
            notificationDAO.setBody(addNotificationDTO.getBody());
            notificationDAO.setBannerUrl(addNotificationDTO.getBannerUrl());
            notificationDAO.setAuthenticationDAO(addNotificationDTO.getAuthenticationDAO());
            notificationRepository.save(notificationDAO);

            AuthenticationDAO authenticationDAO = addNotificationDTO.getAuthenticationDAO();
            if(authenticationDAO.getDeviceInfo() != null && !authenticationDAO.getDeviceInfo().getFcmToken().isEmpty() && authenticationDAO.getDeviceInfo().getFcmToken() != null) {
                pushNotificationUtil.sendPushNotification(authenticationDAO.getDeviceInfo().getFcmToken(),
                        addNotificationDTO.getBody(), addNotificationDTO.getTitle(), addNotificationDTO.getSubTitle(),
                        addNotificationDTO.getAction());
            }

        } catch (Exception exception) {
            log.error("Exception in save notification: {}", exception.getMessage());
            throw new Exception("Failed to save and trigger notification");
        }
    }

    @Override
    public void saveNotification(List<AuthenticationDAO> authenticationDAOS, AddNotificationDTO addNotificationDTO){
        log.info(("Request received in save multiple notification"));
        List<NotificationDAO> notificationDAOS = new ArrayList<>();
        for (AuthenticationDAO authenticationDAO: authenticationDAOS){
            try {
                NotificationDAO notificationDAO = new NotificationDAO();
                notificationDAO.setNotificationRefId(CommonUtils.generateRandomString(30));
                notificationDAO.setStatus(addNotificationDTO.getStatus());
                if(addNotificationDTO.getTypeId()==null){
                    notificationDAO.setTypeId(notificationDAO.getNotificationRefId());
                } else {
                    notificationDAO.setTypeId(addNotificationDTO.getTypeId());
                }
                notificationDAO.setType(addNotificationDTO.getType());
                notificationDAO.setAction(addNotificationDTO.getAction());
                notificationDAO.setTitle(addNotificationDTO.getTitle());
                notificationDAO.setSubTitle(addNotificationDTO.getSubTitle());
                notificationDAO.setBody(addNotificationDTO.getBody());
                notificationDAO.setBannerUrl(addNotificationDTO.getBannerUrl());
                notificationDAO.setAuthenticationDAO(authenticationDAO);
                notificationDAO.setExpiryDate(addNotificationDTO.getExpiryDate());
                notificationDAOS.add(notificationDAO);
            } catch (Exception exception) {
                log.error("Exception in generate notification object: {}", exception.getMessage());
                return;
            }
        }
        try{
            notificationRepository.saveAll(notificationDAOS);
            List<String> deviceTokens = authenticationDAOS.stream().map(AuthenticationDAO::getDeviceInfo).filter(deviceInfo -> deviceInfo!=null).map(DeviceInfo::getFcmToken).filter(fcmToken -> fcmToken!=null).collect(Collectors.toList());
            pushNotificationUtil.sendPushNotification(deviceTokens,
                    addNotificationDTO.getBody(), addNotificationDTO.getTitle(), addNotificationDTO.getSubTitle(),
                    addNotificationDTO.getAction());
        } catch(Exception exception){
            log.error("Exception in save notification: {}", exception.getMessage());
        }
    }


    public AddNotificationDTO  getAddNotificationDTOForTopUp(String typeId, String status, Double amount,
                                                             AuthenticationDAO authenticationDAO) throws Exception {
        AddNotificationDTO addNotificationDTO =getAddNotificationDTOEntity(typeId,status,NotificationConstants.TOP_UP, authenticationDAO);
        String subTitle = "";
        if(NotificationConstants.SUCCESS_STATUS.equalsIgnoreCase(status)) {
            addNotificationDTO.setStatus(NotificationConstants.SUCCESS_STATUS);
            addNotificationDTO.setTitle(NotificationConstants.TOP_UP+" "+NotificationConstants.SUCCESS_STATUS);
            subTitle = MessageFormat.format(NotificationConstants.TOP_UP_SUCCESS_TEMPLATE,amount);
        } else {
            addNotificationDTO.setStatus(NotificationConstants.FAIL_STATUS);
            addNotificationDTO.setTitle(NotificationConstants.TOP_UP+" "+NotificationConstants.FAIL_STATUS);
            subTitle =MessageFormat.format(NotificationConstants.TOP_UP_FAIL_TEMPLATE,amount);
        }
        addNotificationDTO.setAction(homeScreen.toString());
        addNotificationDTO.setSubTitle(subTitle);
        addNotificationDTO.setBody(subTitle);
        return addNotificationDTO;
    }

    public AddNotificationDTO getAddNotificationDTOForPurchaseTicket(String typeId,String status, String fromTotoStations,
                                                                     AuthenticationDAO authenticationDAO) throws Exception {

        AddNotificationDTO addNotificationDTO = getAddNotificationDTOEntity(typeId,status,
                NotificationConstants.TICKET_PURCHASE, authenticationDAO);
        String subTitle = "";
        if(NotificationConstants.SUCCESS_STATUS.equalsIgnoreCase(status)) {
            addNotificationDTO.setStatus(NotificationConstants.SUCCESS_STATUS);
            addNotificationDTO.setTitle(NotificationConstants.TICKET_PURCHASE+" "+NotificationConstants.SUCCESS_STATUS);
            addNotificationDTO.setAction(viewTickets.toString());
            subTitle =MessageFormat.format(NotificationConstants.TICKET_PURCHASE_SUCCESS_TEMPLATE,fromTotoStations);
        } else {
            addNotificationDTO.setStatus(NotificationConstants.FAIL_STATUS);
            addNotificationDTO.setTitle(NotificationConstants.TICKET_PURCHASE+" "+NotificationConstants.FAIL_STATUS);
            addNotificationDTO.setAction(homeScreen.toString());
            addNotificationDTO.setType(TICKET_FAILED);
            subTitle =MessageFormat.format(NotificationConstants.TICKET_PURCHASE_FAIL_TEMPLATE,fromTotoStations);
        }
        addNotificationDTO.setSubTitle(subTitle);
        addNotificationDTO.setBody(subTitle);

        return addNotificationDTO;
    }

    private AddNotificationDTO getAddNotificationDTOEntity(String typeId, String status, String type, AuthenticationDAO authenticationDAO) throws Exception {
        AddNotificationDTO addNotificationDTO = new AddNotificationDTO();
        addNotificationDTO.setAuthenticationDAO(authenticationDAO);
        addNotificationDTO.setType(type);
        addNotificationDTO.setTypeId(typeId);
        addNotificationDTO.setStatus(status);
        return addNotificationDTO;
    }

}
