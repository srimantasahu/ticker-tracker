package com.indvest.stocks.tracker.repository;


import com.indvest.stocks.tracker.bean.MarketType;
import com.indvest.stocks.tracker.bean.RefData;
import com.indvest.stocks.tracker.bean.RefDataResult;

import java.util.List;
import java.util.Map;

public interface NSERepository {

    void save(List<Map<String, String>> refDataList);

    void save(RefData refData);

    List<String> getInstruments(List<String> statuses);

    List<String> getInstruments(MarketType marketType);

    List<RefDataResult> getInstruments(String industry, MarketType marketType, String orderBy);
}
