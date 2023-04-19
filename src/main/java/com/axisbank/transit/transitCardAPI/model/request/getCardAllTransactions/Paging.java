package com.axisbank.transit.transitCardAPI.model.request.getCardAllTransactions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Paging {

    @JsonProperty("Take")
    private String take;

    @JsonProperty("Skip")
    private String skip;

    @JsonProperty("OrderBy")
    private String orderBy;

    public Paging(String take, String skip, String orderBy) {
        this.take=take;
        this.skip=skip;
        this.orderBy=orderBy;
    }

    public String getTake() {
        return take;
    }

    public void setTake(String take) {
        this.take = take;
    }

    public String getSkip() {
        return skip;
    }

    public void setSkip(String skip) {
        this.skip = skip;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String toString() {
        return "Paging{" +
                "take='" + take + '\'' +
                ", skip='" + skip + '\'' +
                ", orderBy='" + orderBy + '\'' +
                '}';
    }
}
