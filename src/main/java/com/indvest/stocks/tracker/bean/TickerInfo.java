package com.indvest.stocks.tracker.bean;

import java.util.Date;

public class TickerInfo {

    private String symbol;
    private Long buyQty;
    private Long sellQty;

    private Double tradeVolInLk;
    private Double tradeValInCr;
    private Double totMarCapInCr;
    private Double ffMarCapInCr;
    private Float impactCost;
    private Float perTradedQty;
    private Float appMarRate;
    private Integer faceValue;

    private Double high52;
    private Date high52Dt;
    private Double low52;
    private Date low52Dt;

    private Double upperBand;
    private Double lowerBand;
    private String priceBand;

    private String listedStatus;
    private String listingDt;
    private String tradingStatus;

    private Float adjustedPE;
    private Float symbolPE;

    private String sectoralIndex;
    private String basicIndustry;
    private String boardStatus;
    private String tradingSegment;
    private String classOfShares;

    private String corpActions;
    private String financialResults;
    private String shareholdingPatterns;

    public TickerInfo(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getBuyQty() {
        return buyQty;
    }

    public void setBuyQty(Long buyQty) {
        this.buyQty = buyQty;
    }

    public Long getSellQty() {
        return sellQty;
    }

    public void setSellQty(Long sellQty) {
        this.sellQty = sellQty;
    }

    public Double getTradeVolInLk() {
        return tradeVolInLk;
    }

    public void setTradeVolInLk(Double tradeVolInLk) {
        this.tradeVolInLk = tradeVolInLk;
    }

    public Double getTradeValInCr() {
        return tradeValInCr;
    }

    public void setTradeValInCr(Double tradeValInCr) {
        this.tradeValInCr = tradeValInCr;
    }

    public Double getTotMarCapInCr() {
        return totMarCapInCr;
    }

    public void setTotMarCapInCr(Double totMarCapInCr) {
        this.totMarCapInCr = totMarCapInCr;
    }

    public Double getFfMarCapInCr() {
        return ffMarCapInCr;
    }

    public void setFfMarCapInCr(Double ffMarCapInCr) {
        this.ffMarCapInCr = ffMarCapInCr;
    }

    public Float getImpactCost() {
        return impactCost;
    }

    public void setImpactCost(Float impactCost) {
        this.impactCost = impactCost;
    }

    public Float getPerTradedQty() {
        return perTradedQty;
    }

    public void setPerTradedQty(Float perTradedQty) {
        this.perTradedQty = perTradedQty;
    }

    public Float getAppMarRate() {
        return appMarRate;
    }

    public void setAppMarRate(Float appMarRate) {
        this.appMarRate = appMarRate;
    }

    public Integer getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(Integer faceValue) {
        this.faceValue = faceValue;
    }

    public Double getHigh52() {
        return high52;
    }

    public void setHigh52(Double high52) {
        this.high52 = high52;
    }

    public Date getHigh52Dt() {
        return high52Dt;
    }

    public void setHigh52Dt(Date high52Dt) {
        this.high52Dt = high52Dt;
    }

    public Double getLow52() {
        return low52;
    }

    public void setLow52(Double low52) {
        this.low52 = low52;
    }

    public Date getLow52Dt() {
        return low52Dt;
    }

    public void setLow52Dt(Date low52Dt) {
        this.low52Dt = low52Dt;
    }

    public Double getUpperBand() {
        return upperBand;
    }

    public void setUpperBand(Double upperBand) {
        this.upperBand = upperBand;
    }

    public Double getLowerBand() {
        return lowerBand;
    }

    public void setLowerBand(Double lowerBand) {
        this.lowerBand = lowerBand;
    }

    public String getPriceBand() {
        return priceBand;
    }

    public void setPriceBand(String priceBand) {
        this.priceBand = priceBand;
    }

    public String getListedStatus() {
        return listedStatus;
    }

    public void setListedStatus(String listedStatus) {
        this.listedStatus = listedStatus;
    }

    public String getListingDt() {
        return listingDt;
    }

    public void setListingDt(String listingDt) {
        this.listingDt = listingDt;
    }

    public String getTradingStatus() {
        return tradingStatus;
    }

    public void setTradingStatus(String tradingStatus) {
        this.tradingStatus = tradingStatus;
    }

    public Float getAdjustedPE() {
        return adjustedPE;
    }

    public void setAdjustedPE(Float adjustedPE) {
        this.adjustedPE = adjustedPE;
    }

    public Float getSymbolPE() {
        return symbolPE;
    }

    public void setSymbolPE(Float symbolPE) {
        this.symbolPE = symbolPE;
    }

    public String getSectoralIndex() {
        return sectoralIndex;
    }

    public void setSectoralIndex(String sectoralIndex) {
        this.sectoralIndex = sectoralIndex;
    }

    public String getBasicIndustry() {
        return basicIndustry;
    }

    public void setBasicIndustry(String basicIndustry) {
        this.basicIndustry = basicIndustry;
    }

    public String getBoardStatus() {
        return boardStatus;
    }

    public void setBoardStatus(String boardStatus) {
        this.boardStatus = boardStatus;
    }

    public String getTradingSegment() {
        return tradingSegment;
    }

    public void setTradingSegment(String tradingSegment) {
        this.tradingSegment = tradingSegment;
    }

    public String getClassOfShares() {
        return classOfShares;
    }

    public void setClassOfShares(String classOfShares) {
        this.classOfShares = classOfShares;
    }

    public String getCorpActions() {
        return corpActions;
    }

    public void setCorpActions(String corpActions) {
        this.corpActions = corpActions;
    }

    public String getFinancialResults() {
        return financialResults;
    }

    public void setFinancialResults(String financialResults) {
        this.financialResults = financialResults;
    }

    public String getShareholdingPatterns() {
        return shareholdingPatterns;
    }

    public void setShareholdingPatterns(String shareholdingPatterns) {
        this.shareholdingPatterns = shareholdingPatterns;
    }
}
