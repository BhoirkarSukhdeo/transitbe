package com.axisbank.transit.core.controller;

import com.axisbank.transit.core.model.request.FeedbackRequestDTO;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.model.response.FeedbackResponseDTO;
import com.axisbank.transit.core.service.FeedbackService;
import com.axisbank.transit.core.shared.constants.FeedbackConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.FEEDBACK_URI;

@RestController
@RequestMapping(BASE_URI+FEEDBACK_URI)
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> saveFeedback(@RequestBody FeedbackRequestDTO feedbackRequestDTO) throws Exception{
        feedbackService.saveFeedback(feedbackRequestDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, FeedbackConstants.SAVE_FEEDBACK_SUCCESS_MESSAGE);
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<List<FeedbackResponseDTO>>> getFeedbacks(@RequestParam(name = "page") int page,
                                                                                @RequestParam(name = "size") int size) throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, feedbackService.getFeedbacks(page, size));
    }

}
