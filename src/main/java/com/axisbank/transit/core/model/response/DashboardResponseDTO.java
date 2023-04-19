package com.axisbank.transit.core.model.response;

import com.axisbank.transit.userDetails.model.DTO.UserConfigurationDTO;

import java.util.List;

public class DashboardResponseDTO {
    private TransitCardBalanceDTO balance;//getCardAllLimitAndBalanceInfo
    private String username;
    private String typeOfCard;// ask
    private boolean cardLinked;
    private TransitCardDetailsDTO cardDetails;//
    private boolean block;
    private List<BannerDTO> banner;
    private List<QuickBookDTO> quickBook;
    private double exploreRadius = 4000;
    private String blockType;
    private UserConfigurationDTO sharedPreference;
    private AppConfigDTO appConfig;

    public DashboardResponseDTO() {
    }

    public DashboardResponseDTO(TransitCardBalanceDTO balance, String username, String typeOfCard, boolean cardLinked, TransitCardDetailsDTO cardDetails, boolean block, List<BannerDTO> banner, List<QuickBookDTO> quickBook, double exploreRadius, UserConfigurationDTO sharedPreference) {
        this.balance = balance;
        this.username = username;
        this.typeOfCard = typeOfCard;
        this.cardLinked = cardLinked;
        this.cardDetails = cardDetails;
        this.block = block;
        this.banner = banner;
        this.quickBook = quickBook;
        this.exploreRadius = exploreRadius;
        this.sharedPreference= sharedPreference;
    }

    public TransitCardBalanceDTO getBalance() {
        return balance;
    }

    public void setBalance(TransitCardBalanceDTO balance) {
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTypeOfCard() {
        return typeOfCard;
    }

    public void setTypeOfCard(String typeOfCard) {
        this.typeOfCard = typeOfCard;
    }

    public boolean isCardLinked() {
        return cardLinked;
    }

    public void setCardLinked(boolean cardLinked) {
        this.cardLinked = cardLinked;
    }

    public TransitCardDetailsDTO getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(TransitCardDetailsDTO cardDetails) {
        this.cardDetails = cardDetails;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public List<BannerDTO> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerDTO> banner) {
        this.banner = banner;
    }

    public List<QuickBookDTO> getQuickBook() {
        return quickBook;
    }

    public void setQuickBook(List<QuickBookDTO> quickBook) {
        this.quickBook = quickBook;
    }

    public double getExploreRadius() {
        return exploreRadius;
    }

    public void setExploreRadius(double exploreRadius) {
        this.exploreRadius = exploreRadius;
    }

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public UserConfigurationDTO getSharedPreference() {
        return sharedPreference;
    }

    public void setSharedPreference(UserConfigurationDTO sharedPreference) {
        this.sharedPreference = sharedPreference;
    }

    public AppConfigDTO getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(AppConfigDTO appConfig) {
        this.appConfig = appConfig;
    }
}
