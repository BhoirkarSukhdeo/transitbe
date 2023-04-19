package com.axisbank.transit.core.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;

@Slf4j
@RestController
@RequestMapping(BASE_URI)
public class TestController {

    @GetMapping("/test")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public String test()  {
        log.info("request receive for test method");
        return "ok";
    }

    @GetMapping("/successfulResponse")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<Map<String, String>>> successFulResponse() {
        log.info("request receive for Base Response success");
        Map<String, String> books = new HashMap<>();
        books.put("Sharp Objects","St. Flemming");
        books.put("Gone Girl", "Gillian Flynn");
        books.put("This was a Man","Jeffrey Archer");
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, books);
    }

    @GetMapping("/errorResponse")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> errorResponse() {
        log.info("request receive for Base Response error");
        String message ="Failed to fetch Data";
        return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE,message);
    }
}
