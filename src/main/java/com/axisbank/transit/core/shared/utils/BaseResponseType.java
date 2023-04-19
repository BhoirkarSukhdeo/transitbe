package com.axisbank.transit.core.shared.utils;


import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseResponseType {

    public static <T> T errorResponse(Integer statusCode, String message) {
        BaseResponse<?> baseResponse  =  new BaseResponse(statusCode, message, null);
        return (T)new ResponseEntity<BaseResponse<?>>(baseResponse, HttpStatus.BAD_REQUEST);
    }
    public static <T> T errorResponse(String message) {
        BaseResponse<?> baseResponse  =  new BaseResponse(TransitAPIConstants.API_FAIL_CODE, message, null);
        return (T)new ResponseEntity<BaseResponse<?>>(baseResponse, HttpStatus.BAD_REQUEST);
    }

    public static <T> T successfulResponse(Integer statusCode, Object data) {
        BaseResponse<Object> baseResponse  =  new BaseResponse(statusCode, "", data);
        return (T)new ResponseEntity<BaseResponse<Object>>(baseResponse, HttpStatus.OK);
    }

    public static <T> T successfulResponse(Object data) {
        BaseResponse<Object> baseResponse  =  new BaseResponse(TransitAPIConstants.API_SUCCESS_CODE, "", data);
        return (T)new ResponseEntity<BaseResponse<Object>>(baseResponse, HttpStatus.OK);
    }

    public static <T> T forbiddenResponse(Integer statusCode, String message) {
        BaseResponse<?> baseResponse  =  new BaseResponse(statusCode, message, null);
        return (T)new ResponseEntity<BaseResponse<?>>(baseResponse, HttpStatus.FORBIDDEN);
    }
    public static <T> T forbiddenResponse(String message) {
        BaseResponse<?> baseResponse  =  new BaseResponse(TransitAPIConstants.API_FAIL_CODE, message, null);
        return (T)new ResponseEntity<BaseResponse<?>>(baseResponse, HttpStatus.FORBIDDEN);
    }
}
