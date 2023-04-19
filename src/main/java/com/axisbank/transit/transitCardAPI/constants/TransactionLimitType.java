package com.axisbank.transit.transitCardAPI.constants;

import com.axisbank.transit.transitCardAPI.model.DAO.TransactionLimitTypeDetails;

import java.util.HashMap;
import java.util.Map;

public class TransactionLimitType {
    public static Map<String, TransactionLimitTypeDetails> LIMIT_TYPE_MAP;
    static
    {
        LIMIT_TYPE_MAP = new HashMap<>();
        TransactionLimitTypeDetails typeDetailsT = new TransactionLimitTypeDetails("T", "Retail POS - Contactless");
        TransactionLimitTypeDetails typeDetailsI = new TransactionLimitTypeDetails("I", "Retail POS - Contact");
        TransactionLimitTypeDetails typeDetailsE = new TransactionLimitTypeDetails("E", "Online Spends");

        LIMIT_TYPE_MAP.put("T", typeDetailsT);
        LIMIT_TYPE_MAP.put("I", typeDetailsI);
        LIMIT_TYPE_MAP.put("E", typeDetailsE);
    }
}
