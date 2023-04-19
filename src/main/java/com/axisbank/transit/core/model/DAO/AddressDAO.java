package com.axisbank.transit.core.model.DAO;

import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "address")
@Audited
public class AddressDAO extends BaseEntity {

    @NotAudited
    @Column(name = "address_id")
    private String addressId;

    @Column(name = "line1")
    private String line1;

    @Column(name = "line2")
    private String line2;

    @Column(name = "address_type")
    private String addressType;

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "state")
    private String state;

    @Column(name = "pincode")
    @Size(max = 6)
    private String pincode;

    @Column(name = "link")
    private String link;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @OneToMany(mappedBy = "addressDAO",cascade = CascadeType.ALL)
    private Set<ExploreDAO> exploreDAOSet;

}
