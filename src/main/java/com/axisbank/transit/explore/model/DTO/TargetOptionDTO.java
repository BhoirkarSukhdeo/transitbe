package com.axisbank.transit.explore.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TargetOptionDTO {
    private String filter;
    private List<ExploreFilterDTO> subFilters;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<ExploreFilterDTO> getSubFilters() {
        return subFilters;
    }

    public void setSubFilters(List<ExploreFilterDTO> subFilters) {
        this.subFilters = subFilters;
    }
}
