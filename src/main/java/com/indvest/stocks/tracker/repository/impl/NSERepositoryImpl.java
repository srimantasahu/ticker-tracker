package com.indvest.stocks.tracker.repository.impl;

import com.indvest.stocks.tracker.repository.NSERepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class NSERepositoryImpl implements NSERepository {
    private static final Logger log = LoggerFactory.getLogger(NSERepositoryImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public void save(final List<Map<String, String>> refDataList) {
        log.info("Saving RefData list of size: {}", refDataList.size());

        final String upsertQuery = String.join(" ",
                "INSERT INTO stocks.refdata(symbol, ltp, chng, percent_chng, open, high, low, prev_close, volume_sh, value_cr, high_52w, low_52w, percent_chng_30d, percent_chng_365d)",
                "VALUES(:symbol, :ltp, :chng, :percent_chng, :open, :high, :low, :prev_close, :volume_sh, :value_cr, :high_52w, :low_52w, :percent_chng_30d, :percent_chng_365d)",
                "ON CONFLICT (symbol)",
                "DO UPDATE SET ltp = :ltp, chng = :chng, percent_chng = :percent_chng, open = :open, high = :high, low = :low, prev_close = :prev_close, volume_sh = :volume_sh, value_cr = :value_cr,",
                "high_52w = :high_52w, low_52w = :low_52w, percent_chng_30d = :percent_chng_30d, percent_chng_365d = :percent_chng_365d, updated_at = :updated_at");

        List<Map<String, Object>> refDataMap = refDataList.stream().map(refData -> {
            Map<String, Object> map = new HashMap<>();
            map.put("symbol", refData.get("SYMBOL"));
            map.put("ltp", Double.parseDouble(refData.get("LTP")));
            map.put("chng", NumberUtils.isParsable(refData.get("CHNG")) ? Double.parseDouble(refData.get("CHNG")) : -999D);
            map.put("percent_chng", NumberUtils.isParsable(refData.get("PERCHNG")) ? Double.parseDouble(refData.get("PERCHNG")) : -999D);
            map.put("open", Double.parseDouble(refData.get("OPEN")));
            map.put("high", Double.parseDouble(refData.get("HIGH")));
            map.put("low", Double.parseDouble(refData.get("LOW")));
            map.put("prev_close", Double.parseDouble(refData.get("PREVCLOSE")));
            map.put("volume_sh", Long.parseLong(refData.get("VOLUME")));
            map.put("value_cr", Double.parseDouble(refData.get("VALUE")));
            map.put("high_52w", Double.parseDouble(refData.get("52WH")));
            map.put("low_52w", Double.parseDouble(refData.get("52WL")));
            map.put("percent_chng_30d", NumberUtils.isParsable(refData.get("30DPERCHNG")) ? Double.parseDouble(refData.get("30DPERCHNG")) : -999D);
            map.put("percent_chng_365d", NumberUtils.isParsable(refData.get("365DPERCHNG")) ? Double.parseDouble(refData.get("365DPERCHNG")) : -999D);
            map.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
            return map;
        }).collect(Collectors.toList());

        int[] updateResult = namedJdbcTemplate.batchUpdate(upsertQuery, SqlParameterSourceUtils.createBatch(refDataMap));

        log.info("Update result: {}", updateResult);
        log.info("Update result count: {}, sum: {}", updateResult.length, Arrays.stream(updateResult).sum());
    }

}
