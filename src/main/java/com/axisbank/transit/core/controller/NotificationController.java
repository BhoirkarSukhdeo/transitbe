package com.axisbank.transit.core.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.model.response.NotificationDTO;
import com.axisbank.transit.core.model.response.NotificationDetailDTO;
import com.axisbank.transit.core.service.NotificationService;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.NOTIFICATION;


@RestController
@RequestMapping(BASE_URI+NOTIFICATION)
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<NotificationDTO>>> getNotifications(@RequestParam(name = "page",defaultValue = "0") int page,
                                                                                @RequestParam(name = "size",defaultValue = "10") int size) throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, notificationService.getNotifications(page, size));
    }

    @RequestMapping(path="/{notificationRefId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<NotificationDetailDTO>> getNotificationDetail(@PathVariable("notificationRefId") String id) throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, notificationService.getNotificationDetail(id));
    }
}
