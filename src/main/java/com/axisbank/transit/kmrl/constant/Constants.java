package com.axisbank.transit.kmrl.constant;

public class Constants {
    public static final String KMRL_BLUE_LINE = "Blue";
    public static final String FARE_CHART_IMAGE_URL = "https://s3-ap-south-1.amazonaws.com/kmrldata/wp-content/uploads/2020/10/22113639/KMRL_Farechart1.jpg";

    public static final String SEL_TICKET_PRICE_REQ = "<SelTicketPrice xmlns=\"http://asis-services.com/\"><fareMediaType>{0}</fareMediaType><fromValue>{1}</fromValue><toValue>{2}</toValue><ticketDate>{3}</ticketDate><lang>{4}</lang></SelTicketPrice>";
    public static final String REFUND_TICKET_REQ = "<RefundTicket xmlns=\"http://asis-services.com/\"><QRCodeId>{0}</QRCodeId></RefundTicket>";
    public static final String SEL_QR_CODE_TICKET_REQ = "<SelQrCodeTicket xmlns=\"http://asis-services.com/\"><lang>{0}</lang></SelQrCodeTicket>";
    public static final String SEL_STATIONS_REQ = "<SelStations xmlns=\"http://asis-services.com/\"><lang>{0}</lang></SelStations>";
    public static final String SEL_TICKET_HISTORY_REQ = "<SelTicketHistory xmlns=\"http://asis-services.com/\"><QRCodeId>{0}</QRCodeId></SelTicketHistory>";
    public static final String GET_QR_TICKET_LAST_STATUS = "<GetQrTicketLastStatus xmlns=\"http://asis-services.com/\"><QRCodeId>{0}</QRCodeId><ticketType>{1}</ticketType></GetQrTicketLastStatus>";
    public static final String GET_TOKEN_FULL_REQ = "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\"><soap12:Header><AutHeader xmlns=\"http://asis-services.com/\"><Username>{0}</Username><Password>{1}</Password></AutHeader></soap12:Header><soap12:Body><Token xmlns=\"http://asis-services.com/\" /></soap12:Body></soap12:Envelope>";
    public static final String INS_QR_CODE_TICKET_MOBILE_REQ = "<InsQrCodeTicketMobile xmlns=\"http://asis-services.com/\"><listTicket>{0}</listTicket></InsQrCodeTicketMobile>";
    public static final String INS_QR_CODE_TICKET_MOBILE_REQ_LIST = "<TSelectedTickets><activeFrom>{0}</activeFrom><activeTo>{1}</activeTo><explanation>{2}</explanation><FromId>{3}</FromId><peopleCount>{4}</peopleCount><price>{5}</price><ticketType>{6}</ticketType><ToId>{7}</ToId><weekendPassType>{8}</weekendPassType></TSelectedTickets>";
    public static final String GET_TOBEREFUNDED_REQ = "<GetToBeRefundedAmount xmlns=\"http://asis-services.com/\"><QRCodeId>{0}</QRCodeId></GetToBeRefundedAmount>";

    public static final String TRANSIT_CARD_PAYMENT_METHOD = "Kochi1 card";
    public static final String OTHER_PAYMENT_METHOD = "Other payment";
    public static final String OTHER_PAYMENT_METHOD_OPTIONS = "Other payment options";
    public static final String METRO_STATION_UPDATE_SUCCESS_MESSAGE = "Metro Station updated Successfully";
    public static final String METRO_UPLOAD_SUCCESS_MESSAGE = "Uploaded Successfully";
    public static final String METRO_TIMETABLE_STATUS_UPDATE = "Metro timetable status changed Successfully";
    public static final String METRO_TIMETABLE_ENABLED = "Metro timetable enabling Started";
    public static final String METRO_ROUTE_UPDATED = "Metro Route Updated Successfully";

    public static final String SUCCESSFUL_TO_BE_REFUNDED = "Your Metro Ticket from {0} to {1} will be cancelled Do you want to proceed?\n Refund Amount: INR {2}";
    public static final String FAILED_TO_BE_REFUNDED_ENTERED = "This ticket cannot be cancelled as your QR code is already validated at AFC metro gates.";
    public static final String FAILED_TO_BE_REFUNDED_WINDOW_CLOSED = "Ticket cannot be canceled as cancellation window is closed";
    public static final String FAILED_TO_BE_REFUNDED_GENERIC = "Your request cannot be completed at the moment. Please try again later.";
}
