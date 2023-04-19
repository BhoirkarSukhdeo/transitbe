package com.axisbank.transit.kmrl.client;

import com.axisbank.transit.core.shared.utils.SoapUtils;
import com.axisbank.transit.core.shared.utils.XMLUtils;
import com.axisbank.transit.kmrl.model.DTO.InsQrCodeTicketMobileDTO;
import com.axisbank.transit.kmrl.util.KmrlTicketingUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

import static com.axisbank.transit.kmrl.constant.Constants.*;

@Component
@Slf4j
public class KmrlTicketingClient {

    @Autowired
    KmrlTicketingUtil kmrlTicketingUtil;

    @Value("${kmrl.ticket.baseUrl}")
    private String baseURL;

    @Value("${kmrl.ticket.username}")
    private String username;

    @Value("${kmrl.ticket.password}")
    private String password;

    public JsonNode selTicketPrice(String fareMediaType, String fromValue, String toValue, String ticketDate, String lang) throws Exception{
        String requestBody = MessageFormat.format(SEL_TICKET_PRICE_REQ, fareMediaType, fromValue, toValue, ticketDate, lang);
        String soapRequest = prepareAuthenticatedRequest(requestBody);
        log.info("Request Body:{}",soapRequest);
        String soapResponse = SoapUtils.getSoapResponse(baseURL, soapRequest, "POST");
        log.info("Soap Resp:{}",soapResponse);
        return XMLUtils.xmlStringToJsonNode(soapResponse).get("Body");
    }

    public JsonNode refundTicket(String QRCodeId) throws Exception{
        String requestBody = MessageFormat.format(REFUND_TICKET_REQ, QRCodeId);
        String soapRequest = prepareAuthenticatedRequest(requestBody);
        String soapResponse = SoapUtils.getSoapResponse(baseURL, soapRequest, "POST");
        return XMLUtils.xmlStringToJsonNode(soapResponse).get("Body");
    }

    public JsonNode toBeRefundedTicket(String QRCodeId) throws Exception{
        String requestBody = MessageFormat.format(GET_TOBEREFUNDED_REQ, QRCodeId);
        String soapRequest = prepareAuthenticatedRequest(requestBody);
        String soapResponse = SoapUtils.getSoapResponse(baseURL, soapRequest, "POST");
        return XMLUtils.xmlStringToJsonNode(soapResponse).get("Body");
    }

    public JsonNode insQrCodeTicketMobile(List<InsQrCodeTicketMobileDTO> insQrCodeTicketMobileDTOList) throws Exception{
        String requestBodyList = "";
        for (InsQrCodeTicketMobileDTO insQrCodeTicketMobileDTO: insQrCodeTicketMobileDTOList) {
            requestBodyList = requestBodyList+MessageFormat.format(INS_QR_CODE_TICKET_MOBILE_REQ_LIST, insQrCodeTicketMobileDTO.getActiveFrom(), insQrCodeTicketMobileDTO.getActiveTo(), insQrCodeTicketMobileDTO.getExplanation(), insQrCodeTicketMobileDTO.getFromId(), insQrCodeTicketMobileDTO.getPeopleCount(), insQrCodeTicketMobileDTO.getPrice(), insQrCodeTicketMobileDTO.getTicketType(), insQrCodeTicketMobileDTO.getToId(), insQrCodeTicketMobileDTO.getWeekendPassType());
        }
        String requestBody = MessageFormat.format(INS_QR_CODE_TICKET_MOBILE_REQ, requestBodyList);
        String soapRequest = prepareAuthenticatedRequest(requestBody);
        log.info("Book Ticket Request:{}",soapRequest);
        String soapResponse = SoapUtils.getSoapResponse(baseURL, soapRequest, "POST");
        log.info("Book Ticket Response:{}", soapResponse);
        return XMLUtils.xmlStringToJsonNode(soapResponse).get("Body");
    }

    public JsonNode selTicketHistory(String QRCodeId) throws Exception{
        String requestBody = MessageFormat.format(SEL_TICKET_HISTORY_REQ, QRCodeId);
        String soapRequest = prepareAuthenticatedRequest(requestBody);
        String soapResponse = SoapUtils.getSoapResponse(baseURL, soapRequest, "POST");
        return XMLUtils.xmlStringToJsonNode(soapResponse).get("Body");
    }

    public JsonNode getQRTicketLastStatus(String QRCodeId, String ticketType) throws Exception{
        String requestBody = MessageFormat.format(GET_QR_TICKET_LAST_STATUS, QRCodeId, ticketType);
        String soapRequest = prepareAuthenticatedRequest(requestBody);
        String soapResponse = SoapUtils.getSoapResponse(baseURL, soapRequest, "POST");
        return XMLUtils.xmlStringToJsonNode(soapResponse).get("Body");
    }

    public JsonNode selStations(String lang) throws Exception{
        String requestBody = MessageFormat.format(SEL_STATIONS_REQ, lang);
        String soapRequest = prepareAuthenticatedRequest(requestBody);
        String soapResponse = SoapUtils.getSoapResponse(baseURL, soapRequest, "POST");
        return XMLUtils.xmlStringToJsonNode(soapResponse).get("Body");
    }

    public JsonNode getToken() throws Exception{
        String requestBody = MessageFormat.format(GET_TOKEN_FULL_REQ, username, password);
        log.info("Token Request:{}",requestBody);
        String soapResponse = SoapUtils.getSoapResponse(baseURL, requestBody, "POST");
        log.info("Token Response:{}",soapResponse);
        return XMLUtils.xmlStringToJsonNode(soapResponse).get("Body");
    }

    private String prepareAuthenticatedRequest(String requestBody) throws Exception {
        JsonNode tokenResp = getToken();
        String token = tokenResp.get("TokenResponse").get("TokenResult").asText();
        return kmrlTicketingUtil.prepareSoapRequest(requestBody, token);
    }
}
