package com.axisbank.transit.core.model.response;

public class TransitCardBalanceDTO {
    private String host;
    private String chip;
    private String total;

    public TransitCardBalanceDTO() {
    }

    public TransitCardBalanceDTO(String host, String chip, String total) {
        this.host = host;
        this.chip = chip;
        this.total = total;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getChip() {
        return chip;
    }

    public void setChip(String chip) {
        this.chip = chip;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
