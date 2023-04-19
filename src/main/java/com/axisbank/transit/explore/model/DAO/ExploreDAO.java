package com.axisbank.transit.explore.model.DAO;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.AddressDAO;
import com.axisbank.transit.core.model.DAO.BaseEntity;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.explore.model.DTO.MiscDTO;
import com.axisbank.transit.explore.model.DTO.TargetAudienceDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "explore")
@Audited
public class ExploreDAO extends BaseEntity {

    @NotAudited
    @Column(name = "explore_id")
    private String exploreId;

    @Column(name = "explore_type")
    private String exploreType;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "sub_type")
    private String subType;

    @Column(name = "category")
    private String category;

    @Lob
    @Column(name = "disclaimer")
    private String disclaimer;

    @Column(name = "ticket_link")
    private String ticketLink;

    @Lob
    @Column(name = "terms_and_conditions")
    private String termsAndConditions;

    @Lob
    @Column(name = "target_audience")
    private String targetAudience;

    @Lob
    @Column(name = "misc")
    private String misc;

    @Column(name = "logo_link")
    private String logoLink;

    @Column(name = "banner_link")
    private String bannerLink;

    @Column(name = "website_link")
    private String websiteLink;

    @Column(name = "current_status")
    private String currentStatus;

    @ManyToOne
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;

    @OneToMany(mappedBy = "exploreDAO",cascade = CascadeType.ALL)
    @NotAudited
    private Set<SlotDAO> slotDAOSet;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private AddressDAO addressDAO;

    @NotAudited
    @ManyToMany
    @JoinTable(
            name = "user_explore_mapping",
            joinColumns = @JoinColumn(name = "explore_id"),
            inverseJoinColumns = @JoinColumn(name = "authentication_id")
    )
    private Set<AuthenticationDAO> authenticationDAOSet = new HashSet<>();

    public MiscDTO getMisc() throws JsonProcessingException {
        if(misc == null){
            return null;
        }
        return CommonUtils.convertJsonStringToObject(misc, MiscDTO.class);
    }

    public void setMisc(MiscDTO misc) throws JsonProcessingException {
        if(misc == null){
            this.misc=null;
        }
        else {
            this.misc = CommonUtils.convertObjectToJsonString(misc);
        }
    }

    public TargetAudienceDTO getTargetAudience() throws JsonProcessingException {
        return CommonUtils.convertJsonStringToObject(targetAudience, TargetAudienceDTO.class);
    }

    public void setTargetAudience(TargetAudienceDTO targetAudience) throws JsonProcessingException {
        this.targetAudience = CommonUtils.convertObjectToJsonString(targetAudience);
    }
}
