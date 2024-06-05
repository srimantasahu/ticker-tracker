package com.indvest.stocks.tracker.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<String> getCategory(String type) {
        return switch (type) {
//            case "ALL" -> Arrays.stream(values()).map(Enum::name).collect(Collectors.toList());
            case "SME" -> List.of(SME.name());
            default -> List.of(EQUITY.name());
        };
    }


}
