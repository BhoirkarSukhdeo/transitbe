package com.axisbank.transit.core.model.response;

import java.util.List;

public class QuickBookDefaultDTO {
    private List<QuickBookDefaultDetailsDTO> defaultOptions;
    public QuickBookDefaultDTO(){}
    public QuickBookDefaultDTO(List<QuickBookDefaultDetailsDTO> defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    public List<QuickBookDefaultDetailsDTO> getDefaultOptions() {
        return defaultOptions;
    }

    public void setDefaultOptions(List<QuickBookDefaultDetailsDTO> defaultOptions) {
        this.defaultOptions = defaultOptions;
    }
}
