package com.axisbank.transit.transitCardAPI.model.request.updateCardLimit;

public class RequestBody {
    public String cardNumber;
    public String userId;
    public String password;
    public String language;
    public String mbrId;
    public String limitType;
    public String dailyAmount;
    public String weeklyAmount;
    public String monthlyAmount;
    public String yearlyAmount;
    public String dailyCount;
    public String weeklyCount;
    public String monthlyCount;
    public String yearlyCount;
    public String singleAmount;
    public String maximumSingleAmount;
    public String maximumDailyAmount;
    public String maximumWeeklyAmount;
    public String maximumMonthlyAmount;
    public String maximumYearlyAmount;
    public String maximumDailyCount;
    public String maximumWeeklyCount;
    public String maximumMonthlyCount;
    public String maximumYearlyCount;
    public String restrictEmvTransactionWithoutPin = "false";
    public String restrictOnlineContactlessTransaction = "false";
    public String restrictEcommerceTransaction = "false";
    public String restrictOfflineTransaction = "false";

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMbrId() {
        return mbrId;
    }

    public void setMbrId(String mbrId) {
        this.mbrId = mbrId;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public String getDailyAmount() {
        return dailyAmount;
    }

    public void setDailyAmount(String dailyAmount) {
        this.dailyAmount = dailyAmount;
    }

    public String getWeeklyAmount() {
        return weeklyAmount;
    }

    public void setWeeklyAmount(String weeklyAmount) {
        this.weeklyAmount = weeklyAmount;
    }

    public String getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(String monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public String getYearlyAmount() {
        return yearlyAmount;
    }

    public void setYearlyAmount(String yearlyAmount) {
        this.yearlyAmount = yearlyAmount;
    }

    public String getDailyCount() {
        return dailyCount;
    }

    public void setDailyCount(String dailyCount) {
        this.dailyCount = dailyCount;
    }

    public String getWeeklyCount() {
        return weeklyCount;
    }

    public void setWeeklyCount(String weeklyCount) {
        this.weeklyCount = weeklyCount;
    }

    public String getMonthlyCount() {
        return monthlyCount;
    }

    public void setMonthlyCount(String monthlyCount) {
        this.monthlyCount = monthlyCount;
    }

    public String getYearlyCount() {
        return yearlyCount;
    }

    public void setYearlyCount(String yearlyCount) {
        this.yearlyCount = yearlyCount;
    }

    public String getSingleAmount() {
        return singleAmount;
    }

    public void setSingleAmount(String singleAmount) {
        this.singleAmount = singleAmount;
    }

    public String getMaximumSingleAmount() {
        return maximumSingleAmount;
    }

    public void setMaximumSingleAmount(String maximumSingleAmount) {
        this.maximumSingleAmount = maximumSingleAmount;
    }

    public String getMaximumDailyAmount() {
        return maximumDailyAmount;
    }

    public void setMaximumDailyAmount(String maximumDailyAmount) {
        this.maximumDailyAmount = maximumDailyAmount;
    }

    public String getMaximumWeeklyAmount() {
        return maximumWeeklyAmount;
    }

    public void setMaximumWeeklyAmount(String maximumWeeklyAmount) {
        this.maximumWeeklyAmount = maximumWeeklyAmount;
    }

    public String getMaximumMonthlyCount() {
        return maximumMonthlyCount;
    }

    public void setMaximumMonthlyCount(String maximumMonthlyCount) {
        this.maximumMonthlyCount = maximumMonthlyCount;
    }

    public String getMaximumYearlyCount() {
        return maximumYearlyCount;
    }

    public void setMaximumYearlyCount(String maximumYearlyCount) {
        this.maximumYearlyCount = maximumYearlyCount;
    }

    public String getRestrictEmvTransactionWithoutPin() {
        return restrictEmvTransactionWithoutPin;
    }

    public void setRestrictEmvTransactionWithoutPin(String restrictEmvTransactionWithoutPin) {
        this.restrictEmvTransactionWithoutPin = restrictEmvTransactionWithoutPin;
    }

    public String getRestrictOnlineContactlessTransaction() {
        return restrictOnlineContactlessTransaction;
    }

    public void setRestrictOnlineContactlessTransaction(String restrictOnlineContactlessTransaction) {
        this.restrictOnlineContactlessTransaction = restrictOnlineContactlessTransaction;
    }

    public String getRestrictEcommerceTransaction() {
        return restrictEcommerceTransaction;
    }

    public void setRestrictEcommerceTransaction(String restrictEcommerceTransaction) {
        this.restrictEcommerceTransaction = restrictEcommerceTransaction;
    }

    public String getRestrictOfflineTransaction() {
        return restrictOfflineTransaction;
    }

    public void setRestrictOfflineTransaction(String restrictOfflineTransaction) {
        this.restrictOfflineTransaction = restrictOfflineTransaction;
    }

    public String getMaximumMonthlyAmount() {
        return maximumMonthlyAmount;
    }

    public void setMaximumMonthlyAmount(String maximumMonthlyAmount) {
        this.maximumMonthlyAmount = maximumMonthlyAmount;
    }

    public String getMaximumYearlyAmount() {
        return maximumYearlyAmount;
    }

    public void setMaximumYearlyAmount(String maximumYearlyAmount) {
        this.maximumYearlyAmount = maximumYearlyAmount;
    }

    public String getMaximumDailyCount() {
        return maximumDailyCount;
    }

    public void setMaximumDailyCount(String maximumDailyCount) {
        this.maximumDailyCount = maximumDailyCount;
    }

    public String getMaximumWeeklyCount() {
        return maximumWeeklyCount;
    }

    public void setMaximumWeeklyCount(String maximumWeeklyCount) {
        this.maximumWeeklyCount = maximumWeeklyCount;
    }
}
