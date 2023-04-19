package com.axisbank.transit.userDetails.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfoDTO {

    private String fcmToken;
    private String osType;
    private String osVersion;
    private String appVersion;
}
