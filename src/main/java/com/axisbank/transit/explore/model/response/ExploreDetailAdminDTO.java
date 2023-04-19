package com.axisbank.transit.explore.model.response;

import com.axisbank.transit.explore.model.DTO.TargetAudienceDTO;
import com.axisbank.transit.explore.model.request.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExploreDetailAdminDTO {
    private String exploreId;
    private String name;
    private String title;
    private String subType;
    private String description;
    private AddressDTO address;
    private String disclaimer;
    private String termsAndConditions;
    private String logoLink;
    private String bannerLink;
    private String websiteLink;
    private String currentStatus;
    private String category;
    private String homeScreenBannerLink;
    private LocalDate startDate;
    private LocalDate endDate;
    private String startTime;
    private String endTime;
    private TargetAudienceDTO targetAudience;
}
