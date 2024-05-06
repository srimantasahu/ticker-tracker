package com.indvest.stocks.tracker.bean;

import com.indvest.stocks.tracker.constant.Status;

public record StatusMessage(Status status, String message) {
}
