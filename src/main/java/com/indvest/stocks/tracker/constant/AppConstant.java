package com.indvest.stocks.tracker.constant;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class AppConstant {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    private AppConstant() {
    }


}
