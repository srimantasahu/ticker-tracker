package com.indvest.stocks.tracker.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class CommonUtil {

    private CommonUtil(){
    }

    public static String getNSEFileName(String entity) {
        return String.join("-",
                "MW",
                entity.replaceAll(" ", "-"),
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + ".csv");
    }
}
