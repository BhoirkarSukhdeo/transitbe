package com.axisbank.transit.core.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.model.response.FeedbackCategoryDTO;
import com.axisbank.transit.core.service.FeedbackCategoryService;
import com.axisbank.transit.core.shared.constants.FeedbackConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.FEEDBACK_CATEGORY_URI;

@RestController
@RequestMapping(BASE_URI+FEEDBACK_CATEGORY_URI)
public class FeedbackCategoryController {

    @Autowired
    FeedbackCategoryService feedbackCategoryService;

    @GetMapping("/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<FeedbackCategoryDTO>>> getCategories() throws Exception {
        List<FeedbackCategoryDTO> feedbackCategoryDTOList = feedbackCategoryService.getCategories();
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, feedbackCategoryDTOList);
    }

    @PostMapping("")
    public ResponseEntity<BaseResponse<String>> saveCategory(@RequestBody FeedbackCategoryDTO feedbackCategoryDTO) throws Exception{
        feedbackCategoryService.saveFeedbackCategory(feedbackCategoryDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, FeedbackConstants.SAVE_CATEGORY_SUCCESS_MESSAGE);
    }
}
