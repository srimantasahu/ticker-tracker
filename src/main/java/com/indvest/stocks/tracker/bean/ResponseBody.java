package com.indvest.stocks.tracker.bean;

import com.indvest.stocks.tracker.constant.Status;

import java.util.List;

public record ResponseBody(Object queryParams, Status status, List<?> results, String message) {
}
