package com.indvest.stocks.tracker.repository.impl;

import com.indvest.stocks.tracker.bean.RefData;
import com.indvest.stocks.tracker.repository.NSERepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
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
                "INSERT INTO stocks.refdata(symbol, ltp, chng, per_chng, open, high, low, prev_close, volume_sh, value_cr, high_52w, low_52w, per_chng_30d, per_chng_365d)",
                "VALUES(:symbol, :ltp, :chng, :per_chng, :open, :high, :low, :prev_close, :volume_sh, :value_cr, :high_52w, :low_52w, :per_chng_30d, :per_chng_365d)",
                "ON CONFLICT (symbol)",
                "DO UPDATE SET ltp = :ltp, chng = :chng, per_chng = :per_chng, open = :open, high = :high, low = :low, prev_close = :prev_close, volume_sh = :volume_sh, value_cr = :value_cr,",
                "high_52w = :high_52w, low_52w = :low_52w, per_chng_30d = :per_chng_30d, per_chng_365d = :per_chng_365d, file_updated_at = :file_updated_at");

        List<Map<String, Object>> refDataMap = refDataList.stream().map(refData -> {
            Map<String, Object> map = new HashMap<>();
            map.put("symbol", refData.get("SYMBOL"));
            map.put("ltp", Double.parseDouble(refData.get("LTP")));
            map.put("chng", NumberUtils.isParsable(refData.get("CHNG")) ? Double.parseDouble(refData.get("CHNG")) : -999D);
            map.put("per_chng", NumberUtils.isParsable(refData.get("PERCHNG")) ? Double.parseDouble(refData.get("PERCHNG")) : -999D);
            map.put("open", Double.parseDouble(refData.get("OPEN")));
            map.put("high", Double.parseDouble(refData.get("HIGH")));
            map.put("low", Double.parseDouble(refData.get("LOW")));
            map.put("prev_close", Double.parseDouble(refData.get("PREVCLOSE")));
            map.put("volume_sh", Long.parseLong(refData.get("VOLUME")));
            map.put("value_cr", Double.parseDouble(refData.get("VALUE")));
            map.put("high_52w", Double.parseDouble(refData.get("52WH")));
            map.put("low_52w", Double.parseDouble(refData.get("52WL")));
            map.put("per_chng_30d", NumberUtils.isParsable(refData.get("30DPERCHNG")) ? Double.parseDouble(refData.get("30DPERCHNG")) : -999D);
            map.put("per_chng_365d", NumberUtils.isParsable(refData.get("365DPERCHNG")) ? Double.parseDouble(refData.get("365DPERCHNG")) : -999D);
            map.put("file_updated_at", Timestamp.valueOf(LocalDateTime.now()));
            return map;
        }).collect(Collectors.toList());

        int[] updateResult = namedJdbcTemplate.batchUpdate(upsertQuery, SqlParameterSourceUtils.createBatch(refDataMap));

        log.debug("Update result: {}", updateResult);
        log.info("Update result count: {}, sum: {}", updateResult.length, Arrays.stream(updateResult).sum());
    }

    @Override
    public void save(RefData refData) {
        log.info("Saving RefData: {}", refData);

        final StringBuilder updateQuery = new StringBuilder("UPDATE stocks.refdata SET ");
        final Map<String, Object> refDataMap = new HashMap<>();

        if (refData.getBuyQty() != null) {
            updateQuery.append("buy_qty = :buy_qty, ");
            refDataMap.put("buy_qty", refData.getBuyQty());
        }
        if (refData.getSellQty() != null) {
            updateQuery.append("sell_qty = :sell_qty, ");
            refDataMap.put("sell_qty", refData.getSellQty());
        }
        if (refData.getSellQty() != null) {
            updateQuery.append("sell_qty = :sell_qty, ");
            refDataMap.put("sell_qty", refData.getSellQty());
        }
        if (refData.getTradeVolInLk() != null) {
            updateQuery.append("volume_sh = :volume_sh, ");
            refDataMap.put("volume_sh", refData.getTradeVolInLk() * 100_000);
        }
        if (refData.getTradeValInCr() != null) {
            updateQuery.append("value_cr = :value_cr, ");
            refDataMap.put("value_cr", refData.getTradeValInCr());
        }
        if (refData.getTotMarCapInCr() != null) {
            updateQuery.append("tot_mar_cap_cr = :tot_mar_cap_cr, ");
            refDataMap.put("tot_mar_cap_cr", refData.getTotMarCapInCr());
        }
        if (refData.getFfMarCapInCr() != null) {
            updateQuery.append("ff_mar_cap_cr = :ff_mar_cap_cr, ");
            refDataMap.put("ff_mar_cap_cr", refData.getFfMarCapInCr());
        }
        if (refData.getImpactCost() != null) {
            updateQuery.append("impact_cost = :impact_cost, ");
            refDataMap.put("impact_cost", refData.getImpactCost());
        }
        if (refData.getPerTradedQty() != null) {
            updateQuery.append("per_traded_qty = :per_traded_qty, ");
            refDataMap.put("per_traded_qty", refData.getPerTradedQty());
        }
        if (refData.getAppMarRate() != null) {
            updateQuery.append("app_mar_rate = :app_mar_rate, ");
            refDataMap.put("app_mar_rate", refData.getAppMarRate());
        }
        if (refData.getFaceValue() != null) {
            updateQuery.append("face_val = :face_val, ");
            refDataMap.put("face_val", refData.getFaceValue());
        }
        if (refData.getLow52() != null) {
            updateQuery.append("low_52w = :low_52w, ");
            refDataMap.put("low_52w", refData.getLow52());
        }
        if (refData.getLow52Dt() != null) {
            updateQuery.append("low_52w_dt = :low_52w_dt, ");
            refDataMap.put("low_52w_dt", Date.valueOf(refData.getLow52Dt()));
        }
        if (refData.getHigh52() != null) {
            updateQuery.append("high_52w = :high_52w, ");
            refDataMap.put("high_52w", refData.getHigh52());
        }
        if (refData.getHigh52Dt() != null) {
            updateQuery.append("high_52w_dt = :high_52w_dt, ");
            refDataMap.put("high_52w_dt", Date.valueOf(refData.getHigh52Dt()));
        }
        if (refData.getUpperBand() != null) {
            updateQuery.append("upper_band = :upper_band, ");
            refDataMap.put("upper_band", refData.getUpperBand());
        }
        if (refData.getLowerBand() != null) {
            updateQuery.append("lower_band = :lower_band, ");
            refDataMap.put("lower_band", refData.getLowerBand());
        }
        if (refData.getPriceBand() != null) {
            updateQuery.append("price_band = :price_band, ");
            refDataMap.put("price_band", refData.getPriceBand());
        }
        if (refData.getListedStatus() != null) {
            updateQuery.append("listed_status = :listed_status, ");
            refDataMap.put("listed_status", refData.getListedStatus());
        }
        if (refData.getListedDt() != null) {
            updateQuery.append("listed_dt = :listed_dt, ");
            refDataMap.put("listed_dt", Date.valueOf(refData.getListedDt()));
        }
        if (refData.getTradingStatus() != null) {
            updateQuery.append("trading_status = :trading_status, ");
            refDataMap.put("trading_status", refData.getTradingStatus());
        }
        if (refData.getAdjustedPE() != null) {
            updateQuery.append("adjusted_pe = :adjusted_pe, ");
            refDataMap.put("adjusted_pe", refData.getAdjustedPE());
        }
        if (refData.getSymbolPE() != null) {
            updateQuery.append("symbol_pe = :symbol_pe, ");
            refDataMap.put("symbol_pe", refData.getSymbolPE());
        }
        if (refData.getSectoralIndex() != null) {
            updateQuery.append("sect_index = :sect_index, ");
            refDataMap.put("sect_index", refData.getSectoralIndex());
        }
        if (refData.getBasicIndustry() != null) {
            updateQuery.append("basic_industry = :basic_industry, ");
            refDataMap.put("basic_industry", refData.getBasicIndustry());
        }
        if (refData.getBoardStatus() != null) {
            updateQuery.append("board_status = :board_status, ");
            refDataMap.put("board_status", refData.getBoardStatus());
        }
        if (refData.getTradingSegment() != null) {
            updateQuery.append("trading_segment = :trading_segment, ");
            refDataMap.put("trading_segment", refData.getTradingSegment());
        }
        if (refData.getSharesClass() != null) {
            updateQuery.append("shares_class = :shares_class, ");
            refDataMap.put("shares_class", refData.getSharesClass());
        }
        if (refData.getCorpActions() != null) {
            updateQuery.append("corp_actions = :corp_actions, ");
            refDataMap.put("corp_actions", refData.getCorpActions());
        }
        if (refData.getFinancialResults() != null) {
            updateQuery.append("fin_results = :fin_results, ");
            refDataMap.put("fin_results", refData.getFinancialResults());
        }
        if (refData.getShareholdingPatterns() != null) {
            updateQuery.append("holding_patterns = :holding_patterns, ");
            refDataMap.put("holding_patterns", refData.getShareholdingPatterns());
        }

        updateQuery.append("inst_updated_at = :inst_updated_at WHERE symbol = :symbol");
        refDataMap.put("inst_updated_at", Timestamp.valueOf(LocalDateTime.now()));
        refDataMap.put("symbol", refData.getSymbol());

        int updateResult = namedJdbcTemplate.update(updateQuery.toString(), refDataMap);

        log.info("Update result: {}", updateResult);
    }

}
