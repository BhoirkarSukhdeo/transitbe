package com.axisbank.transit.bus.controller;

import com.axisbank.transit.bus.model.DTO.BusDeparturesDTO;
import com.axisbank.transit.bus.service.BusTimeTableService;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.util.List;

import static com.axisbank.transit.bus.constants.BusApiConstants.BUS_BASE;
import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.kmrl.constant.KmrlApiConstants.TIMETABLE;

@RestController
@RequestMapping(BASE_URI+BUS_BASE+TIMETABLE)
public class BusTimetableController {
    @Autowired
    BusTimeTableService busTimeTableService;
    @GetMapping("/departures")
    public ResponseEntity<BaseResponse<List<BusDeparturesDTO>>> getUpcomingDepartures(@RequestParam(value = "source") String source,
                                                                                      @RequestParam(value = "destination", defaultValue = "") String destination){
        if (destination==null || destination.equals(""))
            return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,
                busTimeTableService.getUpcomingDepartures(source));
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,
                busTimeTableService.getUpcomingTimings(source,destination));
    }
}
