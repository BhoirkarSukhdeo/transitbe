package com.axisbank.transit.explore.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TargetAudienceDTO {
    private List<TargetOptionDTO> data;

    public List<TargetOptionDTO> getData() {
        return data;
    }

    public void setData(List<TargetOptionDTO> data) {
        this.data = data;
    }
}
