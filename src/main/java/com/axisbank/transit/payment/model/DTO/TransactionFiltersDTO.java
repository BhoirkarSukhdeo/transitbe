package com.axisbank.transit.payment.model.DTO;

import java.util.Set;

public class TransactionFiltersDTO {
    private Set<String> statuses;
    private Set<String> categories;
    private Set<String> paymentTypes;

    public Set<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(Set<String> statuses) {
        this.statuses = statuses;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public Set<String> getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(Set<String> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }
}
