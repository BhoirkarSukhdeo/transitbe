package com.axisbank.transit.userDetails.model.DAO;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Entity(name = "device_info")
public class DeviceInfo extends BaseEntity {

    @NotAudited
    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "os_type")
    private String osType;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "app_version")
    private String appVersion;

    @NotAudited
    @OneToOne
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;
}
