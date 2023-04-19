package com.axisbank.transit.explore.model.response;

import com.axisbank.transit.explore.model.DTO.TargetAudienceDTO;
import com.axisbank.transit.explore.model.DTO.TargetOptionDTO;
import com.axisbank.transit.explore.model.request.AddressDTO;
import com.axisbank.transit.explore.model.request.SlotDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExploreDetailDTO {
    private String name;
    private String title;
    private String subType;
    private String description;
    private List<SlotDTO> slot;
    private AddressDTO address;
    private String disclaimer;
    private String termsAndConditions;
    private String logoLink;
    private String bannerLink;
    private String websiteLink;
    private String distance;
    private String time;
    private TargetAudienceDTO targetAudience;
}
