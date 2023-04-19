package com.axisbank.transit.transitCardAPI.model.DAO;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.BaseEntity;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.transitCardAPI.model.DTO.LimitTypeDetailDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.persistence.*;

@Entity(name = "card_limit_detail")
public class CardLimitDetailsDAO extends BaseEntity {

    @Column(name = "limit_detail_id")
    private String limitDetailId;

    @OneToOne
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;

    @Lob
    @Column(name = "chip_balance_limit")
    private String chipBalanceLimit;

    @Lob
    @Column(name = "retail_POS_contactless")
    private String retailPOSContactless;

    @Lob
    @Column(name = "retail_POS_contact")
    private String retailPOSContact;

    @Lob
    @Column(name = "online_spends")
    private String onlineSpends;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "comments")
    private String comments;

    public String getLimitDetailId() {
        return limitDetailId;
    }

    public void setLimitDetailId(String limitDetailId) {
        this.limitDetailId = limitDetailId;
    }

    public AuthenticationDAO getAuthenticationDAO() {
        return authenticationDAO;
    }

    public void setAuthenticationDAO(AuthenticationDAO authenticationDAO) {
        this.authenticationDAO = authenticationDAO;
    }

    public LimitTypeDetailDTO getChipBalanceLimit() throws JsonProcessingException {
        if(chipBalanceLimit == null){
            return null;
        }
        return CommonUtils.convertJsonStringToObject(chipBalanceLimit, LimitTypeDetailDTO.class);
    }

    public void setChipBalanceLimit(LimitTypeDetailDTO chipBalanceLimit) throws JsonProcessingException{
        if(chipBalanceLimit == null){
            this.chipBalanceLimit=null;
        }
        else {
            this.chipBalanceLimit = CommonUtils.convertObjectToJsonString(chipBalanceLimit);
        }
    }

    public LimitTypeDetailDTO getRetailPOSContactless() throws JsonProcessingException{
        if(retailPOSContactless == null){
            return null;
        }
        return CommonUtils.convertJsonStringToObject(retailPOSContactless, LimitTypeDetailDTO.class);
    }

    public void setRetailPOSContactless(LimitTypeDetailDTO retailPOSContactless) throws JsonProcessingException{
        if(retailPOSContactless == null){
            this.retailPOSContactless=null;
        }
        else {
            this.retailPOSContactless = CommonUtils.convertObjectToJsonString(retailPOSContactless);
        }
    }

    public LimitTypeDetailDTO getRetailPOSContact() throws JsonProcessingException{
        if(retailPOSContact == null){
            return null;
        }
        return CommonUtils.convertJsonStringToObject(retailPOSContact, LimitTypeDetailDTO.class);
    }

    public void setRetailPOSContact(LimitTypeDetailDTO retailPOSContact) throws JsonProcessingException{
        if(retailPOSContact == null){
            this.retailPOSContact=null;
        }
        else {
            this.retailPOSContact = CommonUtils.convertObjectToJsonString(retailPOSContact);
        }
    }

    public LimitTypeDetailDTO getOnlineSpends() throws JsonProcessingException{
        if(onlineSpends == null){
            return null;
        }
        return CommonUtils.convertJsonStringToObject(onlineSpends, LimitTypeDetailDTO.class);
    }

    public void setOnlineSpends(LimitTypeDetailDTO onlineSpends) throws JsonProcessingException{
        if(onlineSpends == null){
            this.onlineSpends=null;
        }
        else {
            this.onlineSpends = CommonUtils.convertObjectToJsonString(onlineSpends);
        }
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
