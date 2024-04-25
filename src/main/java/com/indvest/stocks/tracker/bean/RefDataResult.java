package com.indvest.stocks.tracker.bean;

public class RefDataResult {

    //symbol, name, low_52w, ltp, high_52w, adjusted_pe, symbol_pe, tot_mar_cap_cr, earnings_share, face_val, per_chng_365d, per_chng_30d, promoter_holding, public_holding, sect_index

    private String name;
    private String symbol;
    private Double ltp;
    private Double low52w;
    private Double high52w;
    private Double adjustedPE;
    private Double symbolPE;
    private Double earningsPerShare;
    private Double faceVal;
    private Double perChange30D;
    private Double perChange365D;
    private Double promoterHolding;
    private Double publicHolding;
    private Double totalMarketCapInCr;
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

    public Double getLtp() {
        return ltp;
    }

    public void setLtp(Double ltp) {
        this.ltp = ltp;
    }

    public Double getLow52w() {
        return low52w;
    }

    public void setLow52w(Double low52w) {
        this.low52w = low52w;
    }

    public Double getHigh52w() {
        return high52w;
    }

    public void setHigh52w(Double high52w) {
        this.high52w = high52w;
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

    public Double getEarningsPerShare() {
        return earningsPerShare;
    }

    public void setEarningsPerShare(Double earningsPerShare) {
        this.earningsPerShare = earningsPerShare;
    }

    public Double getFaceVal() {
        return faceVal;
    }

    public void setFaceVal(Double faceVal) {
        this.faceVal = faceVal;
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

    public Double getTotalMarketCapInCr() {
        return totalMarketCapInCr;
    }

    public void setTotalMarketCapInCr(Double totalMarketCapInCr) {
        this.totalMarketCapInCr = totalMarketCapInCr;
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
