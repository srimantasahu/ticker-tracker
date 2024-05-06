package com.indvest.stocks.tracker.bean;

import com.indvest.stocks.tracker.constant.Status;

public record Response(String entity, Status status, String message) {
}
