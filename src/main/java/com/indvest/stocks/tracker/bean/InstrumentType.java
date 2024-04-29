package com.indvest.stocks.tracker.bean;

import org.apache.commons.lang3.StringUtils;

public enum InstrumentType {

    EQUITY,
    SME;

    public static InstrumentType get(String entity) {
        return switch (StringUtils.substringBefore(entity, StringUtils.SPACE)) {
            case "NIFTY" -> EQUITY;
            case "SME" -> SME;
            default -> throw new IllegalStateException("Unexpected value of entity: " + entity);
        };
    }



}
