package com.axisbank.transit.kmrl.constant;

import com.axisbank.transit.kmrl.model.DAO.KmrlTicketTypeDetails;

import java.util.HashMap;
import java.util.Map;

public class KmrlTicketTypes {
    public static Map<String, KmrlTicketTypeDetails> TICKET_TYPE_MAP;
    static
    {
        TICKET_TYPE_MAP = new HashMap<>();
        KmrlTicketTypeDetails typeDetailsSJT = new KmrlTicketTypeDetails("One way", "SJT", 11);
        KmrlTicketTypeDetails typeDetailsRJT = new KmrlTicketTypeDetails("Round trip", "RJT", 12);
        TICKET_TYPE_MAP.put("SJT", typeDetailsSJT);
        TICKET_TYPE_MAP.put("RJT", typeDetailsRJT);
    }

}
