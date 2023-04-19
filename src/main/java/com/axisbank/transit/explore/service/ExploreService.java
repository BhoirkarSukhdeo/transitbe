package com.axisbank.transit.explore.service;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.model.DTO.ExploreFilters;
import com.axisbank.transit.explore.model.DTO.TargetAudienceDTO;
import com.axisbank.transit.explore.model.request.ExploreNotificationDTO;
import com.axisbank.transit.explore.model.request.ExploreStatusDTO;
import com.axisbank.transit.explore.model.response.ExploreDetailAdminDTO;
import com.axisbank.transit.explore.model.response.ExploreDetailDTO;
import com.axisbank.transit.explore.model.response.ExploreListViewDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExploreService {
    void createExploreNotification(ExploreNotificationDTO exploreNotificationDTO) throws Exception;
    List<ExploreListViewDTO> getAllNotifications(String category, String subType, String exploreType, double latitude, double longitude) throws Exception;
    ExploreDetailDTO getExploreDetails(String exploreId, double latitude, double longitude) throws Exception;
    List<ExploreDetailAdminDTO> getAllExplore(int page, int size, String exploreType) throws Exception;
    ExploreDAO updateCurrentExploreStatusAndUpdatedBy(ExploreStatusDTO exploreStatusDTO) throws Exception;
    void pushNotifications(ExploreDAO exploreDAO) throws Exception;
    void deleteExplore(String exploreId) throws Exception;
    void updateExplore(ExploreNotificationDTO exploreNotificationDTO) throws Exception;
    Double getNearByExploerRadiusGC();

    List<String> readTargettingFile(MultipartFile file) throws Exception;

    TargetAudienceDTO getTargetAudience() throws Exception;

    ExploreFilters getExploreFilters() throws Exception;
    void mapExploreItems(AuthenticationDAO authenticationDAO) throws Exception;
}
