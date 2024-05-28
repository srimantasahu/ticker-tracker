package com.indvest.stocks.tracker.repository;


import com.indvest.stocks.tracker.bean.BuyNSell;
import com.indvest.stocks.tracker.bean.BuyNSellResult;
import com.indvest.stocks.tracker.bean.RefData;
import com.indvest.stocks.tracker.bean.RefDataResult;
import com.indvest.stocks.tracker.constant.InstrumentType;
import com.indvest.stocks.tracker.constant.MarketType;
import com.indvest.stocks.tracker.constant.Side;

import java.util.List;
import java.util.Map;

public interface NSERepository {

    void save(List<Map<String, String>> refDataList, InstrumentType instrumentType);

    void save(RefData refData);

    List<String> getInstruments(List<String> statuses);

    List<String> getInstruments(MarketType marketType);

    List<RefDataResult> getInstruments(String industry, MarketType marketType, String orderBy);

    void save(BuyNSell buyNSell);

    List<String> getBnSInstruments(Side side);

    List<BuyNSellResult> getBnSInstruments(Side side, Double range, String orderBy);
}
