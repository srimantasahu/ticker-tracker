package com.indvest.stocks.tracker.bean;

import com.indvest.stocks.tracker.constant.Status;

import java.util.List;

public record ResponseBody(QueryParams queryParams, Status status, List<RefDataResult> results, String message) {
}
