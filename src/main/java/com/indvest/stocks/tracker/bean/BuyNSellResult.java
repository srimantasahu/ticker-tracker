package com.indvest.stocks.tracker.bean;

public class BuyNSellResult {

    private String name;
    private String symbol;
    private String marketCap;
    private Double currentLtp;
    private Double price;
    private Integer qty;
    private Double ltp;
    private Double high52w;
    private Double low52w;
    private Double symbolPE;
    private Double faceVal;
    private Double earningsPerShare;
    private Double perChange30D;
    private Double perChange365D;
    private Double promoterHolding;
    private Double publicHolding;
    private String basicIndustry;
    private String sectoralIndex;
    private Integer rowNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public Double getCurrentLtp() {
        return currentLtp;
    }

    public void setCurrentLtp(Double currentLtp) {
        this.currentLtp = currentLtp;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getLtp() {
        return ltp;
    }

    public void setLtp(Double ltp) {
        this.ltp = ltp;
    }

    public Double getHigh52w() {
        return high52w;
    }

    public void setHigh52w(Double high52w) {
        this.high52w = high52w;
    }

    public Double getLow52w() {
        return low52w;
    }

    public void setLow52w(Double low52w) {
        this.low52w = low52w;
    }

    public Double getSymbolPE() {
        return symbolPE;
    }

    public void setSymbolPE(Double symbolPE) {
        this.symbolPE = symbolPE;
    }

    public Double getFaceVal() {
        return faceVal;
    }

    public void setFaceVal(Double faceVal) {
        this.faceVal = faceVal;
    }

    public Double getEarningsPerShare() {
        return earningsPerShare;
    }

    public void setEarningsPerShare(Double earningsPerShare) {
        this.earningsPerShare = earningsPerShare;
    }

    public Double getPerChange30D() {
        return perChange30D;
    }

    public void setPerChange30D(Double perChange30D) {
        this.perChange30D = perChange30D;
    }

    public Double getPerChange365D() {
        return perChange365D;
    }

    public void setPerChange365D(Double perChange365D) {
        this.perChange365D = perChange365D;
    }

    public Double getPromoterHolding() {
        return promoterHolding;
    }

    public void setPromoterHolding(Double promoterHolding) {
        this.promoterHolding = promoterHolding;
    }

    public Double getPublicHolding() {
        return publicHolding;
    }

    public void setPublicHolding(Double publicHolding) {
        this.publicHolding = publicHolding;
    }

    public String getBasicIndustry() {
        return basicIndustry;
    }

    public void setBasicIndustry(String basicIndustry) {
        this.basicIndustry = basicIndustry;
    }

    public String getSectoralIndex() {
        return sectoralIndex;
    }

    public void setSectoralIndex(String sectoralIndex) {
        this.sectoralIndex = sectoralIndex;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }
}
