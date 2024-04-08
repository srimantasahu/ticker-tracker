package com.indvest.stocks.tracker.util;

import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.indvest.stocks.tracker.constant.AppConstant.DATE_FORMATTER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public final class CommonUtil {

    private CommonUtil() {
    }

    public static String getNSEFileName(String entity) {
        return String.join("-",
                "MW",
                entity.replaceAll(" ", "-"),
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + ".csv");
    }

    public static Integer parseInt(String text) {
        String numericText = text.replace(",", EMPTY).trim();
        return NumberUtils.isParsable(numericText) ? Integer.parseInt(numericText) : null;
    }

    public static Long parseLong(String text) {
        String numericText = text.replace(",", EMPTY).trim();
        return NumberUtils.isParsable(numericText) ? Long.parseLong(numericText) : null;
    }

    public static Double parseDouble(String text) {
        String numericText = text.replaceAll("[,%]", EMPTY).trim();
        return NumberUtils.isParsable(numericText) ? Double.parseDouble(numericText) : null;
    }

    public static LocalDate parseDate(String text) {
        String numericText = text.replaceAll("[()]", EMPTY).trim();
        return LocalDate.parse(text.replaceAll("[()]", EMPTY), DATE_FORMATTER);
    }
}
