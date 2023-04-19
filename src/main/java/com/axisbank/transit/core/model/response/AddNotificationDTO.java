package com.axisbank.transit.core.model.response;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddNotificationDTO {
    private String typeId;
    private String type;
    private String title;
    private String subTitle;
    private String status;
    private String body;
    private String action;
    private boolean seen;
    private String bannerUrl;
    private AuthenticationDAO authenticationDAO;
    private Date expiryDate;
}
