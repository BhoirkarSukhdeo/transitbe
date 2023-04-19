package com.axisbank.transit.transitCardAPI.model.DAO;

import com.axisbank.transit.authentication.config.ApplicationSetupData;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.BaseEntity;
import com.axisbank.transit.core.shared.utils.EncryptionUtil;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;

import javax.persistence.*;

@Entity(name = "card_details")
public class CardDetailsDAO extends BaseEntity {

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "bar_code_no")
    private String barCodeNo;

    @Column(name = "card_token")
    private String cardToken;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "authentication_id", nullable = false)
    private AuthenticationDAO authenticationDAO;

    public String getCardNo() throws Exception {
        return EncryptionUtil.decrypt(cardNo, ApplicationSetupData.getCardSecretKey(), TransitCardAPIConstants.ALGORITHM_AES);
    }

    public void setCardNo(String cardNo) throws Exception {
        this.cardNo = EncryptionUtil.encrypt(cardNo, ApplicationSetupData.getCardSecretKey(),TransitCardAPIConstants.ALGORITHM_AES);
    }

    public String getBarCodeNo() {
        return barCodeNo;
    }

    public void setBarCodeNo(String barCodeNo) {
        this.barCodeNo = barCodeNo;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public AuthenticationDAO getAuthenticationDAO() {
        return authenticationDAO;
    }

    public void setAuthenticationDAO(AuthenticationDAO authenticationDAO) {
        this.authenticationDAO = authenticationDAO;
    }
}
