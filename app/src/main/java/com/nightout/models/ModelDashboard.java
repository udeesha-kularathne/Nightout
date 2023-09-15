package com.nightout.models;

public class ModelDashboard {
    private String marketingCost;
    private String laborCost;
    private String utilitiesCost;
    private String personnelCost;
    private String venueCost;
    private String materialCost;
    private String ticketCount;
    private String ticketPrice;
    private String salesIncome;
    private String sponsorIncome;

    private double totalProfit;
    private String category;
    private String month;

    private String uid;


    // Default constructor
    public ModelDashboard() {
        // Default constructor required for Firebase
    }

    // Constructor with parameters
    public ModelDashboard(
            String marketingCost, String laborCost, String utilitiesCost, String personnelCost,
            String venueCost, String materialCost, String ticketCount, String ticketPrice,
            String salesIncome, String sponsorIncome, double Profit,
            String category, String month, String uid) {
        this.marketingCost = marketingCost;
        this.laborCost = laborCost;
        this.utilitiesCost = utilitiesCost;
        this.personnelCost = personnelCost;
        this.venueCost = venueCost;
        this.materialCost = materialCost;
        this.ticketCount = ticketCount;
        this.ticketPrice = ticketPrice;
        this.salesIncome = salesIncome;
        this.sponsorIncome = sponsorIncome;
        this.category = category;
        this.totalProfit = Profit;
        this.month = month;
        this.uid = uid;
    }

    // Getter and setter methods for each field


    public String getMarketingCost() {
        return marketingCost;
    }

    public void setMarketingCost(String marketingCost) {
        this.marketingCost = marketingCost;
    }

    public String getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(String laborCost) {
        this.laborCost = laborCost;
    }

    public String getUtilitiesCost() {
        return utilitiesCost;
    }

    public void setUtilitiesCost(String utilitiesCost) {
        this.utilitiesCost = utilitiesCost;
    }

    public String getPersonnelCost() {
        return personnelCost;
    }

    public void setPersonnelCost(String personnelCost) {
        this.personnelCost = personnelCost;
    }

    public String getVenueCost() {
        return venueCost;
    }

    public void setVenueCost(String venueCost) {
        this.venueCost = venueCost;
    }

    public String getMaterialCost() {
        return materialCost;
    }

    public void setMaterialCost(String materialCost) {
        this.materialCost = materialCost;
    }

    public String getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(String ticketCount) {
        this.ticketCount = ticketCount;
    }

    public String getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(String ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getSalesIncome() {
        return salesIncome;
    }

    public void setSalesIncome(String salesIncome) {
        this.salesIncome = salesIncome;
    }

    public String getSponsorIncome() {
        return sponsorIncome;
    }

    public void setSponsorIncome(String sponsorIncome) {
        this.sponsorIncome = sponsorIncome;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
