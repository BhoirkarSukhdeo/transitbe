package com.axisbank.transit.journey.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavouriteAddressDTO {

    private String addressId;
    private String favouriteType;
    private String addressTitle;
    private String address;
    private double latitute;
    private double longitude;
}
