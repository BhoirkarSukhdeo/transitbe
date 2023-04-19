package com.axisbank.transit.explore.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.model.DTO.ExploreFilters;
import com.axisbank.transit.explore.model.DTO.TargetAudienceDTO;
import com.axisbank.transit.explore.model.request.ExploreNotificationDTO;
import com.axisbank.transit.explore.model.request.ExploreStatusDTO;
import com.axisbank.transit.explore.model.response.ExploreDetailAdminDTO;
import com.axisbank.transit.explore.model.response.ExploreDetailDTO;
import com.axisbank.transit.explore.model.response.ExploreListViewDTO;
import com.axisbank.transit.explore.service.ExploreService;
import com.axisbank.transit.explore.shared.constants.ExploreConstants;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.axisbank.transit.core.shared.constants.ApiConstants.ADMIN_URI;

@RestController
@RequestMapping(ApiConstants.BASE_URI+ ApiConstants.EXPLORE_URI)
public class ExploreController {

    @Autowired
    ExploreService exploreService;

    @PostMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> createExploreNotification(@RequestBody ExploreNotificationDTO exploreNotificationDTO) throws Exception {
        exploreService.createExploreNotification(exploreNotificationDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, ExploreConstants.CREATE_NOTIFCATION_SUCCESS_MESSAGE);
    }

    @PostMapping("/update-status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> updateExploreStatus(@RequestBody ExploreStatusDTO exploreStatusDTO) throws Exception {

        ExploreDAO exploreDAO = exploreService.updateCurrentExploreStatusAndUpdatedBy(exploreStatusDTO);

        if (exploreDAO.getCurrentStatus().equalsIgnoreCase(exploreStatusDTO.getStatus()) ) {
            return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, ExploreConstants.UPDATED_STATUS_SUCCESS);
        }
        return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE, ExploreConstants.UPDATE_STATUS_FAILURE);
    }

    @GetMapping("/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<ExploreListViewDTO>>> getAllNotifications(@RequestParam(name = "category", defaultValue = "%") String category,
                                                                    @RequestParam(name = "subType", defaultValue = "%") String subType,
                                                                    @RequestParam(name = "exploreType", defaultValue = "%") String exploreType,
                                                                    @RequestParam(name = "latitude") double latitude,
                                                                    @RequestParam(name = "longitude") double longitude) throws Exception {

        List<ExploreListViewDTO> exploreListViewDTOList = exploreService.getAllNotifications(category, subType, exploreType, latitude, longitude);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, exploreListViewDTOList);
    }

    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<ExploreDetailDTO>> getExploreDetails(@RequestParam(name = "exploreId") String exploreId,
                                                                            @RequestParam(name = "latitude") double latitude,
                                                                            @RequestParam(name = "longitude") double longitude) throws Exception{
        ExploreDetailDTO exploreDetailDTO = exploreService.getExploreDetails(exploreId, latitude, longitude);
        if (exploreDetailDTO != null ) {
            return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, exploreDetailDTO);
        }
        return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE, ExploreConstants.GET_ALL_NOTIFICATION_FAILURE_MESSAGE);
    }

    @GetMapping(ADMIN_URI)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<ExploreDetailAdminDTO>>> getAllExploreAdmin(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "exploreType", defaultValue = "%") String exploreType) throws Exception {
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,
                exploreService.getAllExplore(page, size, exploreType));
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @GetMapping("/delete/{exploreId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> deleteExplore(@PathVariable String exploreId) throws Exception {
        exploreService.deleteExplore(exploreId);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, "Successfully deleted");
    }

    @PostMapping("/update")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> updateExplore(@RequestBody ExploreNotificationDTO exploreNotificationDTO) throws Exception {
        exploreService.updateExplore(exploreNotificationDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, ExploreConstants.UPDATE_NOTIFCATION_SUCCESS_MESSAGE);
    }

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.PUBLISHER, RoleConstants.CHECKER})
    @PostMapping("/read-targetting-file")
    public ResponseEntity<List<String>> readTargettingFile(@RequestParam("file") MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        String validFileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (!contentType.equals(validFileType)) {
            throw new Exception("Invalid File Type");
        }
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, exploreService.readTargettingFile(file));
    }

    @GetMapping("/target-audience")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<TargetAudienceDTO>> getTargetAudience() throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, exploreService.getTargetAudience());
    }

    @GetMapping("/filters")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<ExploreFilters>> getExploreFilters() throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, exploreService.getExploreFilters());
    }
}
