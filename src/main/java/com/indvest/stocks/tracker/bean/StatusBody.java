package com.indvest.stocks.tracker.bean;

import java.util.List;

public record StatusBody(Status status, String message, List<RefDataResult> results) {
}
