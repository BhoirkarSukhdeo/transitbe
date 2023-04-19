package com.axisbank.transit.transitCardAPI.model.DTO;

public class UpdateChipBalanceResponseDTO {
    private String eBalanceLimit;
    private LimitTypeDetailDTO chipBalanceLimit;

    public String geteBalanceLimit() {
        return eBalanceLimit;
    }

    public void seteBalanceLimit(String eBalanceLimit) {
        this.eBalanceLimit = eBalanceLimit;
    }

    public LimitTypeDetailDTO getChipBalanceLimit() {
        return chipBalanceLimit;
    }

    public void setChipBalanceLimit(LimitTypeDetailDTO chipBalanceLimit) {
        this.chipBalanceLimit = chipBalanceLimit;
    }
}
