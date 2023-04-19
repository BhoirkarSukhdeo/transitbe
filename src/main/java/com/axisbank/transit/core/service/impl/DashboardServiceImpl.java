package com.axisbank.transit.core.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.model.response.*;
import com.axisbank.transit.core.service.DashboardService;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.service.NotificationService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.model.DAO.SlotDAO;
import com.axisbank.transit.explore.model.DTO.MiscDTO;
import com.axisbank.transit.explore.repository.ExploreRepository;
import com.axisbank.transit.explore.service.ExploreService;
import com.axisbank.transit.explore.shared.constants.ExploreStatus;
import com.axisbank.transit.payment.service.TransactionService;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardInfoDTO;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.*;
import static com.axisbank.transit.explore.shared.constants.ExploreTypes.PROMOTIONAL_OFFER;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.*;

@Slf4j
@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    UserUtil userUtil;
    @Autowired
    ExploreRepository exploreRepository;
    @Autowired
    TransactionService transactionService;
    @Autowired
    TransitCardTxnService transitCardTxnService;
    @Autowired
    RedisClient redisClient;
    @Autowired
    GlobalConfigService globalConfigService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    ExploreService exploreService;
    @Value("${app.google.api.key}")
    private String googleApiKey;

    public DashboardResponseDTO getDasboardDetails() throws Exception {
        AuthenticationDAO authenticationDAO = null;
        double exploreRadius = exploreService.getNearByExploerRadiusGC();
        try {
            authenticationDAO = userUtil.getAuthObject();
            DashboardResponseDTO dashboardResponseDTO = new DashboardResponseDTO();
            DAOUser daoUser = authenticationDAO.getDaoUser();
            CardDetailsDAO cardDetailsDAO = authenticationDAO.getCardDetailsDAO();
            if (cardDetailsDAO != null) {
                TransitCardInfoDTO cardInfoDTO = transitCardTxnService.getTransitCardInfo(cardDetailsDAO.getCardNo());
                String cardStatCode = cardInfoDTO.getCardStatCode();
                String cardSubStatCode = cardInfoDTO.getCardStatSubCode();
                String status = transitCardTxnService.getBlockStatus(cardStatCode, cardSubStatCode);
                if(status.equalsIgnoreCase(PERMANENT_BLOCK) || status.equalsIgnoreCase(EXPIRED)) {
                    AuthenticationDAO auth = transitCardTxnService.linkReplacementCard(authenticationDAO);
                    if(auth!=null){
                        cardDetailsDAO = auth.getCardDetailsDAO();
                        cardInfoDTO = transitCardTxnService.getTransitCardInfo(cardDetailsDAO.getCardNo());
                        cardStatCode = cardInfoDTO.getCardStatCode();
                        cardSubStatCode = cardInfoDTO.getCardStatSubCode();
                        status = transitCardTxnService.getBlockStatus(cardStatCode, cardSubStatCode);
                    }
                }
                if(status.equalsIgnoreCase(ACTIVE)) {
                    dashboardResponseDTO.setBlock(false);
                } else {

                    dashboardResponseDTO.setBlock(true);
                    dashboardResponseDTO.setBlockType(status);
                }
                TransitCardBalanceDTO transitCardBalanceDTO = new TransitCardBalanceDTO(
                        cardInfoDTO.getTotalHostBalance(),
                        cardInfoDTO.getTotalChipBalance(),
                        cardInfoDTO.getTotalCardBalance()
                );
                sendMinBalanceNotifiation(cardInfoDTO.getTotalHostBalance(),authenticationDAO);
                dashboardResponseDTO.setBalance(transitCardBalanceDTO);
                TransitCardDetailsDTO transitCardDetailsDTO = new TransitCardDetailsDTO();
                String maskedCardNumber = CommonUtils.maskString(cardInfoDTO.getCardNo(), 0, 12, '*');
                transitCardDetailsDTO.setCardNumber(maskedCardNumber);
                transitCardDetailsDTO.setNameOnCard(cardInfoDTO.getEmbossName());
                transitCardDetailsDTO.setCvv(null);
                transitCardDetailsDTO.setExpiry(null);
                dashboardResponseDTO.setCardDetails(transitCardDetailsDTO);
                dashboardResponseDTO.setCardLinked(true);
            } else {
                dashboardResponseDTO.setCardLinked(false);
                dashboardResponseDTO.setBlock(false);
            }

            dashboardResponseDTO.setTypeOfCard("physical"); // value can be physical/virtual , can be fetched from card details in future
            String fullName = CommonUtils.getFullName(daoUser.getFirstName(), daoUser.getMiddleName(), daoUser.getLastName());
            dashboardResponseDTO.setUsername(fullName);

            List<BannerDTO> bannerDTOList = new ArrayList<>();

            List<ExploreDAO> exploreDAOList = exploreRepository.findAllByExploreTypeAndCurrentStatusAndAuthenticationDAOSet(PROMOTIONAL_OFFER, ExploreStatus.PUBLISHED, authenticationDAO);
            SortedSet<ExploreDAO> sortedExploreDAO = new TreeSet<>(Comparator.comparing(ExploreDAO::getUpdatedAt));
            for (ExploreDAO exploreDAO: exploreDAOList) {
                if (exploreDAO.getSlotDAOSet() !=null && !exploreDAO.getSlotDAOSet().isEmpty()) {
                    SortedSet<SlotDAO> sortedSlotDAO = new TreeSet<>(Comparator.comparing(SlotDAO::getStartDate));
                    for (SlotDAO slotDAO: exploreDAO.getSlotDAOSet()) {
                        sortedSlotDAO.add(slotDAO);
                    }
                    LocalDate endDate = sortedSlotDAO.last().getEndDate();
                    String endTime = sortedSlotDAO.last().getEndTime();
                    try {
                        if (CommonUtils.checkIfOfferExpired(endDate, endTime)) {
                            continue;
                        }
                    } catch (Exception exception) {
                        log.error("Error in checking expiration");
                        continue;
                    }
                }
                sortedExploreDAO.add(exploreDAO);
            }

            for (ExploreDAO exploreDAO : sortedExploreDAO) {
                BannerDTO bannerDTO = new BannerDTO();
                bannerDTO.setExploreId(exploreDAO.getExploreId());
                bannerDTO.setUrl(exploreDAO.getBannerLink());
                MiscDTO misc = exploreDAO.getMisc();
                if(misc!= null && misc.getHomeScreenLink()!=null) {
                    bannerDTO.setUrl(misc.getHomeScreenLink());
                }
                else {
                    bannerDTO.setUrl(exploreDAO.getBannerLink());
                }
                bannerDTOList.add(bannerDTO);
            }
            List<QuickBookDTO> quickBookDTOList = new ArrayList<>();
            try{
                quickBookDTOList = transactionService.getRecentBookings(authenticationDAO.getId());
            } catch (Exception exception){
                log.error("Failed to Fetch Recent bookings: {}", exception.getMessage());
            }
            dashboardResponseDTO.setQuickBook(quickBookDTOList);
            dashboardResponseDTO.setBanner(bannerDTOList.stream().limit(5).collect(Collectors.toList()));
            dashboardResponseDTO.setExploreRadius(exploreRadius);
            dashboardResponseDTO.setSharedPreference(daoUser.getUserConfiguration());

            int ticketRefreshInterval = 10;
            try{
                GlobalConfigDTO configDTO = globalConfigService.getGlobalConfig(TICKET_REFRESH_INTERVAL, false);
                if (configDTO!=null){
                    log.info("Fetch ticket refresh interval from db");
                    ticketRefreshInterval = Integer.parseInt(configDTO.getValue());
                }
            } catch (Exception ex){
                log.error("failed to get ticket refresh interval, Exception: {}",ex.getMessage());
            }

            dashboardResponseDTO.setAppConfig(new AppConfigDTO(googleApiKey, ticketRefreshInterval));
            return dashboardResponseDTO;
        } catch (Exception exception) {
            log.error("Exception in getting dashboard details: {}", exception.getMessage());
            throw new Exception("Exception in get dashboard details");
        }

    }

    private void sendMinBalanceNotifiation(String balance, AuthenticationDAO authenticationDAO){
        String redisKey = "min_balance_notification:"+authenticationDAO.getUserName();
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(MIN_BALANCE_NOTIFICATION, true);
        if(globalConfig==null){
            log.info("No Global variable configured");
            return;
        }
        try{
            if(redisClient.getValue(redisKey)!=null){
                log.info("Notification already sent today");
                return;
            }
            JsonNode data = globalConfig.getJsonValue();
            String minBalance = data.get("minBalance").asText();
            if(Double.parseDouble(minBalance)<Double.parseDouble(balance)) {
                log.info("User Has enough balance not sending Notification");
                return;
            }
            AddNotificationDTO notificationDTO = new AddNotificationDTO();
            notificationDTO.setTitle(data.get("title").asText());
            notificationDTO.setSubTitle(data.get("subtitle").asText());
            notificationDTO.setBody(data.get("subtitle").asText());
            notificationDTO.setAction(data.get("action").asText());
            notificationDTO.setType("notification");
            notificationDTO.setStatus("Successful");
            notificationDTO.setAuthenticationDAO(authenticationDAO);
            notificationService.saveNotification(notificationDTO);
            redisClient.setValue(redisKey,"true", 24*60*60);
        } catch (Exception ex){
            log.info("Failed to Create Notification:{}", ex.getMessage());
        }
    }
}
