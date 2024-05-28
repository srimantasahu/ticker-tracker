package com.indvest.stocks.tracker.bean;

import com.indvest.stocks.tracker.constant.Status;

import java.util.List;

public record StatusBody(Status status, String message, List<?> results) {
}
