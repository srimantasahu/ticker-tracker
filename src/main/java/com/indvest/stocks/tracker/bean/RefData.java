package com.indvest.stocks.tracker.bean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.StringJoiner;

public class RefData {

    private String symbol;
    private String name;
    private String isin;
    private String series;

    private Double ltp;
    private Double prevClose;
    private Double open;
    private Double high;
    private Double low;

    private Long buyQty;
    private Long sellQty;

    private Double tradeVolInLk;
    private Double tradeValInCr;
    private Double totMarCapInCr;
    private Double ffMarCapInCr;
    private Double impactCost;
    private Double perTradedQty;
    private Double appMarRate;
    private Integer faceValue;

    private Double high52;
    private LocalDate high52Dt;
    private Double low52;
    private LocalDate low52Dt;

    private Double upperBand;
    private Double lowerBand;
    private String priceBand;

    private String listedStatus;
    private LocalDate listedDt;
    private String tradingStatus;

    private Double adjustedPE;
    private Double symbolPE;

    private String sectoralIndex;
    private String basicIndustry;
    private String boardStatus;
    private String tradingSegment;
    private String sharesClass;

    private Double totIncomeInCr;
    private Double netPnLInCr;
    private Double earningsPerShare;

    private Double promoterSHP;
    private Double publicSHP;

    private String[] corpActions;
    private String[] financialResults;
    private String[] shareholdingPatterns;

    public RefData(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public Double getLtp() {
        return ltp;
    }

    public void setLtp(Double ltp) {
        this.ltp = ltp;
    }

    public Double getPrevClose() {
        return prevClose;
    }

    public void setPrevClose(Double prevClose) {
        this.prevClose = prevClose;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
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

    public Double getImpactCost() {
        return impactCost;
    }

    public void setImpactCost(Double impactCost) {
        this.impactCost = impactCost;
    }

    public Double getPerTradedQty() {
        return perTradedQty;
    }

    public void setPerTradedQty(Double perTradedQty) {
        this.perTradedQty = perTradedQty;
    }

    public Double getAppMarRate() {
        return appMarRate;
    }

    public void setAppMarRate(Double appMarRate) {
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

    public LocalDate getHigh52Dt() {
        return high52Dt;
    }

    public void setHigh52Dt(LocalDate high52Dt) {
        this.high52Dt = high52Dt;
    }

    public Double getLow52() {
        return low52;
    }

    public void setLow52(Double low52) {
        this.low52 = low52;
    }

    public LocalDate getLow52Dt() {
        return low52Dt;
    }

    public void setLow52Dt(LocalDate low52Dt) {
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

    public LocalDate getListedDt() {
        return listedDt;
    }

    public void setListedDt(LocalDate listedDt) {
        this.listedDt = listedDt;
    }

    public String getTradingStatus() {
        return tradingStatus;
    }

    public void setTradingStatus(String tradingStatus) {
        this.tradingStatus = tradingStatus;
    }

    public Double getAdjustedPE() {
        return adjustedPE;
    }

    public void setAdjustedPE(Double adjustedPE) {
        this.adjustedPE = adjustedPE;
    }

    public Double getSymbolPE() {
        return symbolPE;
    }

    public void setSymbolPE(Double symbolPE) {
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

    public String getSharesClass() {
        return sharesClass;
    }

    public void setSharesClass(String sharesClass) {
        this.sharesClass = sharesClass;
    }

    public Double getTotIncomeInCr() {
        return totIncomeInCr;
    }

    public void setTotIncomeInCr(Double totIncomeInCr) {
        this.totIncomeInCr = totIncomeInCr;
    }

    public Double getNetPnLInCr() {
        return netPnLInCr;
    }

    public void setNetPnLInCr(Double netPnLInCr) {
        this.netPnLInCr = netPnLInCr;
    }

    public Double getEarningsPerShare() {
        return earningsPerShare;
    }

    public void setEarningsPerShare(Double earningsPerShare) {
        this.earningsPerShare = earningsPerShare;
    }

    public Double getPromoterSHP() {
        return promoterSHP;
    }

    public void setPromoterSHP(Double promoterSHP) {
        this.promoterSHP = promoterSHP;
    }

    public Double getPublicSHP() {
        return publicSHP;
    }

    public void setPublicSHP(Double publicSHP) {
        this.publicSHP = publicSHP;
    }

    public String[] getCorpActions() {
        return corpActions;
    }

    public void setCorpActions(String[] corpActions) {
        this.corpActions = corpActions;
    }

    public String[] getFinancialResults() {
        return financialResults;
    }

    public void setFinancialResults(String[] financialResults) {
        this.financialResults = financialResults;
    }

    public String[] getShareholdingPatterns() {
        return shareholdingPatterns;
    }

    public void setShareholdingPatterns(String[] shareholdingPatterns) {
        this.shareholdingPatterns = shareholdingPatterns;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RefData.class.getSimpleName() + "[", "]")
                .add("symbol='" + symbol + "'")
                .add("name='" + name + "'")
                .add("isin='" + isin + "'")
                .add("series='" + series + "'")
                .add("ltp=" + ltp)
                .add("prevClose=" + prevClose)
                .add("open=" + open)
                .add("high=" + high)
                .add("low=" + low)
                .add("buyQty=" + buyQty)
                .add("sellQty=" + sellQty)
                .add("tradeVolInLk=" + tradeVolInLk)
                .add("tradeValInCr=" + tradeValInCr)
                .add("totMarCapInCr=" + totMarCapInCr)
                .add("ffMarCapInCr=" + ffMarCapInCr)
                .add("impactCost=" + impactCost)
                .add("perTradedQty=" + perTradedQty)
                .add("appMarRate=" + appMarRate)
                .add("faceValue=" + faceValue)
                .add("high52=" + high52)
                .add("high52Dt=" + high52Dt)
                .add("low52=" + low52)
                .add("low52Dt=" + low52Dt)
                .add("upperBand=" + upperBand)
                .add("lowerBand=" + lowerBand)
                .add("priceBand='" + priceBand + "'")
                .add("listedStatus='" + listedStatus + "'")
                .add("listedDt=" + listedDt)
                .add("tradingStatus='" + tradingStatus + "'")
                .add("adjustedPE=" + adjustedPE)
                .add("symbolPE=" + symbolPE)
                .add("sectoralIndex='" + sectoralIndex + "'")
                .add("basicIndustry='" + basicIndustry + "'")
                .add("boardStatus='" + boardStatus + "'")
                .add("tradingSegment='" + tradingSegment + "'")
                .add("sharesClass='" + sharesClass + "'")
                .add("totIncomeInCr=" + totIncomeInCr)
                .add("netPnLInCr=" + netPnLInCr)
                .add("earningsPerShare=" + earningsPerShare)
                .add("promoterSHP=" + promoterSHP)
                .add("publicSHP=" + publicSHP)
                .add("corpActions=" + Arrays.toString(corpActions))
                .add("financialResults=" + Arrays.toString(financialResults))
                .add("shareholdingPatterns=" + Arrays.toString(shareholdingPatterns))
                .toString();
    }
}
