package com.axisbank.transit.explore.model.DTO;

import java.util.List;

public class ExploreFilters {
    private List<String> offerCategories;
    private List<String> offerSubTypes;
    private List<String> exploreTypes;

    public List<String> getOfferCategories() {
        return offerCategories;
    }

    public void setOfferCategories(List<String> offerCategories) {
        this.offerCategories = offerCategories;
    }

    public List<String> getOfferSubTypes() {
        return offerSubTypes;
    }

    public void setOfferSubTypes(List<String> offerSubTypes) {
        this.offerSubTypes = offerSubTypes;
    }

    public List<String> getExploreTypes() {
        return exploreTypes;
    }

    public void setExploreTypes(List<String> exploreTypes) {
        this.exploreTypes = exploreTypes;
    }
}
