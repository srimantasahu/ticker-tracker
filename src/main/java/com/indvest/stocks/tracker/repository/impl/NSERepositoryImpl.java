package com.indvest.stocks.tracker.repository.impl;

import com.indvest.stocks.tracker.bean.*;
import com.indvest.stocks.tracker.repository.NSERepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.indvest.stocks.tracker.bean.DbStatus.*;

@Repository
public class NSERepositoryImpl implements NSERepository {
    private static final Logger log = LoggerFactory.getLogger(NSERepositoryImpl.class);

    @Value("${nse.refresh.interval.mins}")
    private long refreshIntervalMins;

    @Value("${nse.reload.interval.mins}")
    private long reloadIntervalMins;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public void save(final List<Map<String, String>> refDataList) {
        log.info("Saving RefData list of size: {}", refDataList.size());

        final String upsertQuery = String.join(" ",
                "INSERT INTO stocks.refdata(symbol, ltp, chng, per_chng, open, high, low, prev_close, volume_lk, value_cr, high_52w, low_52w, per_chng_30d, per_chng_365d)",
                "VALUES(:symbol, :ltp, :chng, :per_chng, :open, :high, :low, :prev_close, :volume_lk, :value_cr, :high_52w, :low_52w, :per_chng_30d, :per_chng_365d)",
                "ON CONFLICT (symbol)",
                "DO UPDATE SET ltp = :ltp, chng = :chng, per_chng = :per_chng, open = :open, high = :high, low = :low, prev_close = :prev_close, volume_lk = :volume_lk, value_cr = :value_cr,",
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
            map.put("volume_lk", Long.parseLong(refData.get("VOLUME")) / 100_000D);
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

        if (refData.getLtp() != null) {
            updateQuery.append("ltp = :ltp, ");
            refDataMap.put("ltp", refData.getLtp());
        }
        if (refData.getPrevClose() != null) {
            updateQuery.append("prev_close = :prev_close, ");
            refDataMap.put("prev_close", refData.getPrevClose());
        }
        if (refData.getOpen() != null) {
            updateQuery.append("open = :open, ");
            refDataMap.put("open", refData.getOpen());
        }
        if (refData.getHigh() != null) {
            updateQuery.append("high = :high, ");
            refDataMap.put("high", refData.getHigh());
        }
        if (refData.getLow() != null) {
            updateQuery.append("low = :low, ");
            refDataMap.put("low", refData.getLow());
        }
        if (refData.getBuyQty() != null) {
            updateQuery.append("buy_qty = :buy_qty, ");
            refDataMap.put("buy_qty", refData.getBuyQty());
        }
        if (refData.getSellQty() != null) {
            updateQuery.append("sell_qty = :sell_qty, ");
            refDataMap.put("sell_qty", refData.getSellQty());
        }
        if (refData.getTradeVolInLk() != null) {
            updateQuery.append("volume_lk = :volume_lk, ");
            refDataMap.put("volume_lk", refData.getTradeVolInLk());
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
        if (refData.getFinancialResults() != null) {
            updateQuery.append("fin_results = :fin_results, ");
            refDataMap.put("fin_results", refData.getFinancialResults());

            if (refData.getTotIncomeInCr() != null) {
                updateQuery.append("tot_income_cr = :tot_income_cr, ");
                refDataMap.put("tot_income_cr", refData.getTotIncomeInCr());
            }
            if (refData.getNetPnLInCr() != null) {
                updateQuery.append("net_pnl_cr = :net_pnl_cr, ");
                refDataMap.put("net_pnl_cr", refData.getNetPnLInCr());
            }
            if (refData.getEarningsPerShare() != null) {
                updateQuery.append("earnings_share = :earnings_share, ");
                refDataMap.put("earnings_share", refData.getEarningsPerShare());
            }
        }
        if (refData.getShareholdingPatterns() != null) {
            updateQuery.append("holding_patterns = :holding_patterns, ");
            refDataMap.put("holding_patterns", refData.getShareholdingPatterns());

            if (refData.getPromoterSHP() != null) {
                updateQuery.append("promoter_holding = :promoter_holding, ");
                refDataMap.put("promoter_holding", refData.getPromoterSHP());
            }
            if (refData.getPublicSHP() != null) {
                updateQuery.append("public_holding = :public_holding, ");
                refDataMap.put("public_holding", refData.getPublicSHP());
            }
        }
        if (refData.getCorpActions() != null) {
            updateQuery.append("corp_actions = :corp_actions, ");
            refDataMap.put("corp_actions", refData.getCorpActions());
        }

        if (refDataMap.isEmpty()) {
            log.error("Nothing scraped for symbol: {}", refData.getSymbol());
            refDataMap.put("status", SKIPPED.name());
        } else if (Stream.of(refData.getLtp(), refData.getTotMarCapInCr(), refData.getSymbolPE()).anyMatch(Objects::isNull)) {
            refDataMap.put("status", BASIC_MISSING.name());
        } else if (Stream.of(refData.getEarningsPerShare(), refData.getPublicSHP()).anyMatch(Objects::isNull)) {
            refDataMap.put("status", AUX_MISSING.name());
        } else {
            refDataMap.put("status", UPDATED.name());
        }

        if (refData.getIsin() != null) {
            updateQuery.append("name = :name, ");
            refDataMap.put("name", refData.getName());
        }

        if (refData.getIsin() != null) {
            updateQuery.append("isin = :isin, ");
            refDataMap.put("isin", refData.getIsin());
        }

        updateQuery.append("inst_updated_at = :inst_updated_at, status = :status WHERE symbol = :symbol");
        refDataMap.put("inst_updated_at", Timestamp.valueOf(LocalDateTime.now()));
        refDataMap.put("symbol", refData.getSymbol());

        int updateResult = namedJdbcTemplate.update(updateQuery.toString(), refDataMap);

        log.info("Symbol: {}, Status: {}, Update result: {}", refData.getSymbol(), refDataMap.get("status"), updateResult);
    }

    @Override
    public List<String> getInstruments(List<String> statuses) {
        final String symbolsQuery = "SELECT symbol FROM stocks.refdata WHERE status IN (:status) AND symbol NOT LIKE 'NIFTY%' AND inst_updated_at < (:updated_at)";
        final Map<String, Object> params = Map.of("status", statuses,"updated_at", Timestamp.valueOf(LocalDateTime.now().minusMinutes(refreshIntervalMins)));

        return namedJdbcTemplate.queryForList(symbolsQuery, params, String.class);
    }

    @Override
    public List<String> getInstruments(MarketType marketType) {
        final StringBuilder symbolsQuery = new StringBuilder("SELECT symbol FROM stocks.refdata WHERE symbol NOT LIKE 'NIFTY%' AND inst_updated_at < :updated_at");
        final Map<String, Object> params = Map.of("updated_at", Timestamp.valueOf(LocalDateTime.now().minusMinutes(reloadIntervalMins)));

        switch (marketType) {
            case LARGE_CAP -> {
                symbolsQuery.append(" AND tot_mar_cap_cr >= (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 100)");
            }
            case MID_CAP -> {
                symbolsQuery.append(" AND tot_mar_cap_cr < (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 100)");
                symbolsQuery.append(" AND tot_mar_cap_cr >= (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 250)");
            }
            case SMALL_CAP -> {
                symbolsQuery.append(" AND tot_mar_cap_cr < (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 250)");
                symbolsQuery.append(" AND tot_mar_cap_cr >= (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 500)");
            }
            case MICRO_CAP -> {
                symbolsQuery.append(" AND tot_mar_cap_cr < (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 500)");
            }
        }

        return namedJdbcTemplate.queryForList(symbolsQuery.toString(), params, String.class);
    }

    @Override
    public List<RefDataResult> getInstruments(String industry, MarketType marketType, String orderBy) {
        final StringBuilder symbolsQuery = new StringBuilder("SELECT * FROM stocks.refdata WHERE symbol NOT LIKE 'NIFTY%' AND basic_industry = :basic_industry");
        final Map<String, Object> params = Map.of("basic_industry", industry, "order_by", orderBy);

        switch (marketType) {
            case LARGE_CAP -> {
                symbolsQuery.append(" AND tot_mar_cap_cr >= (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 100)");
            }
            case MID_CAP -> {
                symbolsQuery.append(" AND tot_mar_cap_cr < (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 100)");
                symbolsQuery.append(" AND tot_mar_cap_cr >= (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 250)");
            }
            case SMALL_CAP -> {
                symbolsQuery.append(" AND tot_mar_cap_cr < (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 250)");
                symbolsQuery.append(" AND tot_mar_cap_cr >= (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 500)");
            }
            case MICRO_CAP -> {
                symbolsQuery.append(" AND tot_mar_cap_cr < (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 500)");
            }
        }

        symbolsQuery.append(" ORDER BY :order_by");

        return namedJdbcTemplate.query(symbolsQuery.toString(), params, (rs, rowNum) -> {
            RefDataResult result = new RefDataResult();
            result.setRowNum(rowNum);
            result.setSymbol(rs.getString("symbol"));
            result.setName(rs.getString("name"));
            result.setLtp(rs.getDouble("ltp"));
            result.setLow52w(rs.getDouble("low_52w"));
            result.setHigh52w(rs.getDouble("high_52w"));
            result.setAdjustedPE(rs.getDouble("adjusted_pe"));
            result.setSymbolPE(rs.getDouble("symbol_pe"));
            result.setTotalMarketCapInCr(rs.getDouble("tot_mar_cap_cr"));
            result.setEarningsPerShare(rs.getDouble("earnings_share"));
            result.setFaceVal(rs.getDouble("face_val"));
            result.setPerChange30D(rs.getDouble("per_chng_30d"));
            result.setPerChange365D(rs.getDouble("per_chng_365d"));
            result.setPromoterHolding(rs.getDouble("promoter_holding"));
            result.setPublicHolding(rs.getDouble("public_holding"));
            result.setSectoralIndex(rs.getString("sect_index"));
            return result;
        });
    }

}
