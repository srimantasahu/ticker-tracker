package com.indvest.stocks.tracker.bean;

import com.indvest.stocks.tracker.constant.Side;

public record BuyNSell(String symbol, Side side, Double price, Long qty) {
}
