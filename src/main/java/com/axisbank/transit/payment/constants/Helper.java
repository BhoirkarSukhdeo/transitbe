package com.axisbank.transit.payment.constants;

public class Helper {
    public static String getJourneyType(String type) {
        switch(type){
            case "SJT": return CommonConstants.SJT;
            case "RJT": return CommonConstants.RJT;
        }
        return CommonConstants.NA;
    }

    public static String getPaymentStatus(String type) {
        switch(type){
            case "DONE": return CommonConstants.DONE;
            case "INITIATED": return CommonConstants.INITIATED;
            case "FAILED": return CommonConstants.FAILED;
        }
        return CommonConstants.NA;
    }
}
