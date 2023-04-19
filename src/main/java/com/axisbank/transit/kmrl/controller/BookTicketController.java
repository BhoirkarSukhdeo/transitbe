package com.axisbank.transit.kmrl.controller;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.model.response.QuickBookDTO;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.kmrl.model.DTO.*;
import com.axisbank.transit.kmrl.service.BookTicketService;
import com.axisbank.transit.payment.service.TransactionService;
import com.axisbank.transit.userDetails.util.UserUtil;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.kmrl.constant.KmrlApiConstants.BOOK_TICKET_URL;
import static com.axisbank.transit.payment.constants.ServiceProviderConstant.KMRL;

@Slf4j
@RestController
@RequestMapping(BASE_URI+BOOK_TICKET_URL)
public class BookTicketController {

    @Autowired
    BookTicketService bookTicketService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    UserUtil userUtil;

    @GetMapping("/{sourceStationId}/{destinationStationId}")
    public ResponseEntity<BaseResponse<GetFareResponseDTO>> getFare(@PathVariable String sourceStationId,
                                                                    @PathVariable String destinationStationId,
                                                                    @RequestParam(name = "ticketType", defaultValue = "SJT")
                                                                                String ticketType) throws Exception {
        GetFareResponseDTO result = bookTicketService.getFare(sourceStationId, destinationStationId, ticketType);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, result);
    }

    @PostMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<BookTicketResponseDTO>> bookTicket(@RequestBody BookTicketRequestDTO bookTicketRequestDTO) throws Exception {
        return BaseResponseType.successfulResponse(bookTicketService.bookTicket(bookTicketRequestDTO));
    }

    @GetMapping("/viewTicket")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<TicketDetailsDTO>>> viewTicket(
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "status", defaultValue = "%") String status
    ) throws Exception {
        ViewTicketsResponseDTO viewTicketsResponseDTO =bookTicketService.getTickets(pageNo, pageSize, status);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,viewTicketsResponseDTO);
    }

    @GetMapping("/{ticketRefId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<TicketDetailsDTO>> ticketDetail(@PathVariable(name = "ticketRefId") String ticketRefId) throws Exception {
        log.info("Request Receive for getting ticket details");
        TicketDetailsDTO ticketDetailsDTO = bookTicketService.getTicketDetails(ticketRefId);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, ticketDetailsDTO);
    }

    @GetMapping("/recent")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<QuickBookDTO>>> recentBookings() throws Exception {
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        List<QuickBookDTO> recentBookings =transactionService.getRecentBookings(authenticationDAO.getId(), KMRL);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,recentBookings);
    }

    @GetMapping("/cancel-ticket-amount/{ticketRefId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> getCancelAmount(@PathVariable(name = "ticketRefId") String ticketRefId) throws Exception {
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,bookTicketService.getRefundValue(ticketRefId));
    }
    @GetMapping("/cancel-ticket/{ticketRefId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> cancelTicket(@PathVariable(name = "ticketRefId") String ticketRefId) throws Exception {
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,bookTicketService.refundTicket(ticketRefId));
    }
}
