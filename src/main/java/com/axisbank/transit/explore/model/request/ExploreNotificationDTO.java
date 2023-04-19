package com.axisbank.transit.explore.model.request;

import com.axisbank.transit.explore.model.DTO.MiscDTO;
import com.axisbank.transit.explore.model.DTO.TargetAudienceDTO;
import com.axisbank.transit.explore.model.DTO.TargetOptionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExploreNotificationDTO {

    private String exploreId;
    private String exploreType;
    private String name;
    private String title;
    private String subType;
    private String category;
    private String description;
    private List<SlotDTO> slot;
    private AddressDTO address;
    private String disclaimer;
    private String termsAndConditions;
    private TargetAudienceDTO targetAudience;
    private String logoLink;
    private String bannerLink;
    private MiscDTO misc;
    private String websiteLink;
}