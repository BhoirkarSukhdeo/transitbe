package com.axisbank.transit.explore.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {

    private String line1;
    private String line2;
    private String addressType;
    private String city;
    private String district;
    private String state;
    private String pincode;
    private String link;
    private double latitude;
    private double longitude;

}
