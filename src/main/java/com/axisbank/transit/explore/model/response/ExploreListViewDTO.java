package com.axisbank.transit.explore.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExploreListViewDTO {
    private String exploreId;
    private String name;
    private String title;
    private String category;
    private String subType;
    private String logoLink;
    private double latitude;
    private double longitude;
}
