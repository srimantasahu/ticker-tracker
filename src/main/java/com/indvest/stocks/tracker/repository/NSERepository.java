package com.indvest.stocks.tracker.repository;


import java.util.List;
import java.util.Map;

public interface NSERepository {

    void save(List<Map<String, String>> refDataList);

}
