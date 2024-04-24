package com.indvest.stocks.tracker.bean;

import java.util.List;

public record ResponseBody(QueryParams queryParams, Status status, List<RefDataResult> results, String message) {
}
