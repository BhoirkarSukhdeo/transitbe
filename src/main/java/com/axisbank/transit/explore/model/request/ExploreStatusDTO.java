package com.axisbank.transit.explore.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExploreStatusDTO {
    private String exploreId;
    private String status;
}
