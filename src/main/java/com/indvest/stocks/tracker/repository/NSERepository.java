package com.indvest.stocks.tracker.repository;


import com.indvest.stocks.tracker.bean.RefData;

import java.util.List;
import java.util.Map;

public interface NSERepository {

    void save(List<Map<String, String>> refDataList);

    void save(RefData refData);

    List<String> getAll();
}
