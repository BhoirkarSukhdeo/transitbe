package com.axisbank.transit.journey.model.DAO;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "favourite_address")
public class FavouriteAddressDAO extends BaseEntity {

    @Column(name= "address_id")
    private String addressId;

    @Column(name= "favourite_type")
    private String favouriteType;

    @Column(name= "address_title")
    private String addressTitle;

    @Column(name= "address")
    private String address;

    @Column(name= "latitute")
    private double latitute;

    @Column(name= "longitude")
    private double longitude;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;
}
