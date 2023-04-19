package com.axisbank.transit.transitCardAPI.constants;

public class TransitCardAPIConstants {
    public static final String CARD_VERIFICATION_URL = "cardVerification";
    public static final String GET_CARD_ALL_LIMIT_AND_BALANCE_URL = "getCardAllLimitAndBalanceInfo";
    public static final String GET_CARD_ALL_TXN_URL = "getCardAllTransactions";
    public static final String SW_CARD_INFO_URL = "getCardInfo";
    public static final String GET_CITY_LIST_URL = "GetCityList";
    public static final String MATCH_CARD_AND_NEW_CUSTOMER_URL = "MatchCardAndNewCustomer";
    public static final String TOPUP_TO_CARD_WITH_LAST_FOUR_DIGITS_URL = "topupToCardWithLastFourDigits";
    public static final String UPDATE_CARD_STATUS_URL = "UpdateCardStatusr";
    public static final String GET_CUSTOMER_URL = "GetCustomer";
    public static final String GET_CUSTOMER_CARD_INFO_URL = "GetCustomerCardInfo";
    public static final String UPDATE_CUSTOMER_URL = "UpdateCustomer";
    public static final String GET_CARD_INFO_FOR_PORTAL_URL = "GetCardInfoForPortal";
    public static final String TOPUP_TO_PREPAID_URL = "topupToPrepaid";
    public static final String TOPUP_TO_PREPAID_REVERSAL_URL = "TopupToPrepaid_reversal";
    public static final String GET_TXN_BY_ID_URL = "GetTxnById";
    public static final String BLOCK_CARD_URL = "blockCard";
    public static final String LINK_CARD_URL = "linkCard";

    //    Transit PPIM Related constants
    public static final String CUST_EXIST_CHECK_REQUEST= "custExistCheckRequest";
    public static final String GET_ENTITY_DOC_RESPONSE = "getEntityDocResponse";
    public static final String GET_ENTITY_DOC_RECORD = "getEntityDocRecord";
    public static final String AVAILABLE_LIMIT_REQUEST = "availableLimtRequest";


    //    Transit Finacle Related constants
    public static final String GET_CUSTOMER_DETAILS_URI = "getCustomerDtlsRequest";
    public static final String GET_ENTITY_DOC_URI = "getEntityDocRequest";
    public static final String GET_CUSTOMER_DTLS_RESPONSE = "getCustomerDtlsResponse";
    public static final String PAN="PAN";

// Encryption Decryption key for transit apis
    public static final String CARD_VERIFICATION_KEY = "CardVerification";
    public static final String GET_CUSTOMER_CARD_INFO_KEY = "GetCustomerCardInfo";
    public static final String GET_CARD_ALL_LIMIT_AND_BALANCE_KEY = "GetAllLimitAndBalanceInfo";
    public static final String GET_CUSTOMER_KEY = "GetCustomer";
    public static final String UPDATE_CUSTOMER_KEY = "UpdateCustomer";
    public static final String UPDATE_CARD_STATUS_KEY = "UpdateCardStatus";
    public static final String GET_TXN_BY_ID_KEY = "GetTxnById";
    public static final String TOPUP_TO_PREPAID_REVERSAL_KEY = "TopupToPrepaid_Reversal";
    public static final String TOPUP_TO_PREPAID_KEY = "TopupToPrepaid";
    public static final String GET_CARD_INFO_FOR_PORTAL_KEY = "GetCardInfoForPortal";
    public static final String TOPUP_TO_CARD_WITH_LAST_FOUR_DIGITS_KEY = "TopupToCardWithLastFourDigits";
    public static final String MATCH_CARD_AND_NEW_CUSTOMER_KEY = "MatchCardAndNewCustomer";
    public static final String GET_CARD_ALL_TXN_KEY = "GetCardAllTransactions";
    public static final String SW_CARD_INFO_KEY = "GetCardInfo";
    public static final String GET_CITY_LIST_KEY = "GetCityList";
    public static final String MBRID = "1";
    public static final String LANGUAGE = "EN";
    public static final String SERVICE_REQUEST_VERSION = "1.0";
    public static final String REQUEST_UUID = "1";
    public static final String ALGORITHM_AES = "AES";




    //
    public static final String ALLOW_PROCESS_CHANNEL="API";
    public static final String BALANCE_TYPE="H";
    public static final String INSERT_CHANNEL="2";
    public static final String SHOW_IN_STMT="true";
    public static final String OTS_TOPUP_VALUE="524";
    public static final String OTS_DEBIT_VALUE="146";

    public static final String OCT_TOPUP_VALUE="70";
    public static final String OCT_DEBIT_VALUE="10";
    public static final String DESCRIPTIONS="Update from Transit App";
    public static final String CARD_LINK_SUCCESSFULLY="Card Link Successfully";
    public static final String CARD_LINK_FAILED="Card Link Failed";

//    local api constant
    public static final String GET_ALL_LIMIT_AND_BALANCE_URL="getCardAllLimitAndBalanceInfo";
    public static final String GET_TRANSIT_CARD_TRANSACTIONS="getTransitCardTransactions";
    public static final String GET_TRANSIT_CARD_INFO="getTransitCardInfo";


//
    public static final String TRANSIT_CARD = "TransitCard";
    public static final String PAYMENT = "Payment";
    public static final String USER_DETAILS = "UserDetails";
    public static final String AUTHENTICATION = "Authentication";
    public static final String TOP_UP = "top_up";

//    Admin portal related Constants
    public static final String TRANSIT_USER="transit_user";


    public static final String PERMANENT_BLOCK = "Permanent";
    public static final String TEMP_BLOCK = "Temporary";
    public static final String ACTIVE = "Active";
    public static final String EXPIRED = "Expired";

    // Update card limit api constants
    public static final String UPDATE_CARD_OFFLINE_AMOUNT_REQUEST = "updateCardOfflineAmountRequest";
    public static final String UPDATE_CARD_LIMIT_REQUEST = "updateCardLimitRequest";

}