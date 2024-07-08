package com.indvest.stocks.tracker.repository.impl;

import com.indvest.stocks.tracker.bean.BuyNSell;
import com.indvest.stocks.tracker.bean.BuyNSellResult;
import com.indvest.stocks.tracker.bean.RefData;
import com.indvest.stocks.tracker.bean.RefDataResult;
import com.indvest.stocks.tracker.constant.InstrumentType;
import com.indvest.stocks.tracker.constant.MarketType;
import com.indvest.stocks.tracker.constant.Side;
import com.indvest.stocks.tracker.repository.NSERepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.indvest.stocks.tracker.constant.DbStatus.*;

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
    public void save(final List<Map<String, String>> refDataList, InstrumentType instrumentType) {
        log.info("Saving RefData list of size: {}, for instrument type: {}", refDataList.size(), instrumentType.name());

        final String upsertQuery = String.join(" ",
                "INSERT INTO stocks.refdata(symbol, ltp, chng, per_chng, open, high, low, prev_close, volume_lk, value_cr, high_52w, low_52w, per_chng_30d, per_chng_365d, category)",
                "VALUES(:symbol, :ltp, :chng, :per_chng, :open, :high, :low, :prev_close, :volume_lk, :value_cr, :high_52w, :low_52w, :per_chng_30d, :per_chng_365d, :category)",
                "ON CONFLICT (symbol)",
                "DO UPDATE SET ltp = :ltp, chng = :chng, per_chng = :per_chng, open = :open, high = :high, low = :low, prev_close = :prev_close, volume_lk = :volume_lk, value_cr = :value_cr,",
                "high_52w = :high_52w, low_52w = :low_52w, per_chng_30d = :per_chng_30d, per_chng_365d = :per_chng_365d, category = :category, file_updated_at = :file_updated_at");

        List<Map<String, Object>> refDataMap = refDataList.stream().map(refData -> {
            Map<String, Object> map = new HashMap<>();
            map.put("symbol", refData.get("SYMBOL"));
            map.put("ltp", Double.parseDouble(refData.get("LTP")));
            map.put("chng", NumberUtils.isParsable(refData.get("CHNG")) ? Double.parseDouble(refData.get("CHNG")) : -999D);
            map.put("per_chng", NumberUtils.isParsable(refData.get("PERCHNG")) ? Double.parseDouble(refData.get("PERCHNG")) : -999D);
            map.put("open", NumberUtils.isParsable(refData.get("OPEN")) ? Double.parseDouble(refData.get("OPEN")) : -999D);
            map.put("high", NumberUtils.isParsable(refData.get("HIGH")) ? Double.parseDouble(refData.get("HIGH")) : -999D);
            map.put("low", NumberUtils.isParsable(refData.get("LOW")) ? Double.parseDouble(refData.get("LOW")) : -999D);
            map.put("prev_close", Double.parseDouble(refData.get("PREVCLOSE")));
            map.put("volume_lk", NumberUtils.isParsable(refData.get("VOLUME")) ? Long.parseLong(refData.get("VOLUME")) / 100_000D : -999D);
            map.put("value_cr", NumberUtils.isParsable(refData.get("VALUE")) ? Double.parseDouble(refData.get("VALUE")) : -999D);
            map.put("high_52w", Double.parseDouble(refData.get("52WH")));
            map.put("low_52w", Double.parseDouble(refData.get("52WL")));
            map.put("per_chng_30d", NumberUtils.isParsable(refData.get("30DPERCHNG")) ? Double.parseDouble(refData.get("30DPERCHNG")) : -999D);
            map.put("per_chng_365d", NumberUtils.isParsable(refData.get("365DPERCHNG")) ? Double.parseDouble(refData.get("365DPERCHNG")) : -999D);
            map.put("category", instrumentType.name());
            map.put("file_updated_at", Timestamp.valueOf(LocalDateTime.now()));
            return map;
        }).collect(Collectors.toList());

        int[] updateResult = namedJdbcTemplate.batchUpdate(upsertQuery, SqlParameterSourceUtils.createBatch(refDataMap));

        log.debug("Update result: {}", updateResult);
        log.info("Update result for instrument type: {}, count: {}, sum: {}", instrumentType.name(), updateResult.length, Arrays.stream(updateResult).sum());
    }

    @Override
    public void save(RefData refData) {
        log.info("Saving RefData: {}", refData);

        final StringBuilder insertQuery = new StringBuilder("INSERT INTO stocks.refdata(symbol, ");
        final StringBuilder valuesQuery = new StringBuilder("VALUES(:symbol, ");
        final StringBuilder updateQuery = new StringBuilder("UPDATE SET ");
        final Map<String, Object> refDataMap = new HashMap<>();

        if (refData.getLtp() != null) {
            insertQuery.append("ltp, ");
            valuesQuery.append(":ltp, ");
            updateQuery.append("ltp = :ltp, ");
            refDataMap.put("ltp", refData.getLtp());
        }
        if (refData.getPrevClose() != null) {
            insertQuery.append("prev_close, ");
            valuesQuery.append(":prev_close, ");
            updateQuery.append("prev_close = :prev_close, ");
            refDataMap.put("prev_close", refData.getPrevClose());
        }
        if (refData.getOpen() != null) {
            insertQuery.append("open, ");
            valuesQuery.append(":open, ");
            updateQuery.append("open = :open, ");
            refDataMap.put("open", refData.getOpen());
        }
        if (refData.getHigh() != null) {
            insertQuery.append("high, ");
            valuesQuery.append(":high, ");
            updateQuery.append("high = :high, ");
            refDataMap.put("high", refData.getHigh());
        }
        if (refData.getLow() != null) {
            insertQuery.append("low, ");
            valuesQuery.append(":low, ");
            updateQuery.append("low = :low, ");
            refDataMap.put("low", refData.getLow());
        }
        if (refData.getBuyQty() != null) {
            insertQuery.append("buy_qty, ");
            valuesQuery.append(":buy_qty, ");
            updateQuery.append("buy_qty = :buy_qty, ");
            refDataMap.put("buy_qty", refData.getBuyQty());
        }
        if (refData.getSellQty() != null) {
            insertQuery.append("sell_qty, ");
            valuesQuery.append(":sell_qty, ");
            updateQuery.append("sell_qty = :sell_qty, ");
            refDataMap.put("sell_qty", refData.getSellQty());
        }
        if (refData.getTradeVolInLk() != null) {
            insertQuery.append("volume_lk, ");
            valuesQuery.append(":volume_lk, ");
            updateQuery.append("volume_lk = :volume_lk, ");
            refDataMap.put("volume_lk", refData.getTradeVolInLk());
        }
        if (refData.getTradeValInCr() != null) {
            insertQuery.append("value_cr, ");
            valuesQuery.append(":value_cr, ");
            updateQuery.append("value_cr = :value_cr, ");
            refDataMap.put("value_cr", refData.getTradeValInCr());
        }
        if (refData.getTotMarCapInCr() != null) {
            insertQuery.append("tot_mar_cap_cr, ");
            valuesQuery.append(":tot_mar_cap_cr, ");
            updateQuery.append("tot_mar_cap_cr = :tot_mar_cap_cr, ");
            refDataMap.put("tot_mar_cap_cr", refData.getTotMarCapInCr());
        }
        if (refData.getFfMarCapInCr() != null) {
            insertQuery.append("ff_mar_cap_cr, ");
            valuesQuery.append(":ff_mar_cap_cr, ");
            updateQuery.append("ff_mar_cap_cr = :ff_mar_cap_cr, ");
            refDataMap.put("ff_mar_cap_cr", refData.getFfMarCapInCr());
        }
        if (refData.getImpactCost() != null) {
            insertQuery.append("impact_cost, ");
            valuesQuery.append(":impact_cost, ");
            updateQuery.append("impact_cost = :impact_cost, ");
            refDataMap.put("impact_cost", refData.getImpactCost());
        }
        if (refData.getPerTradedQty() != null) {
            insertQuery.append("per_traded_qty, ");
            valuesQuery.append(":per_traded_qty, ");
            updateQuery.append("per_traded_qty = :per_traded_qty, ");
            refDataMap.put("per_traded_qty", refData.getPerTradedQty());
        }
        if (refData.getAppMarRate() != null) {
            insertQuery.append("app_mar_rate, ");
            valuesQuery.append(":app_mar_rate, ");
            updateQuery.append("app_mar_rate = :app_mar_rate, ");
            refDataMap.put("app_mar_rate", refData.getAppMarRate());
        }
        if (refData.getFaceValue() != null) {
            insertQuery.append("face_val, ");
            valuesQuery.append(":face_val, ");
            updateQuery.append("face_val = :face_val, ");
            refDataMap.put("face_val", refData.getFaceValue());
        }
        if (refData.getLow52() != null) {
            insertQuery.append("low_52w, ");
            valuesQuery.append(":low_52w, ");
            updateQuery.append("low_52w = :low_52w, ");
            refDataMap.put("low_52w", refData.getLow52());
        }
        if (refData.getLow52Dt() != null) {
            insertQuery.append("low_52w_dt, ");
            valuesQuery.append(":low_52w_dt, ");
            updateQuery.append("low_52w_dt = :low_52w_dt, ");
            refDataMap.put("low_52w_dt", Date.valueOf(refData.getLow52Dt()));
        }
        if (refData.getHigh52() != null) {
            insertQuery.append("high_52w, ");
            valuesQuery.append(":high_52w, ");
            updateQuery.append("high_52w = :high_52w, ");
            refDataMap.put("high_52w", refData.getHigh52());
        }
        if (refData.getHigh52Dt() != null) {
            insertQuery.append("high_52w_dt, ");
            valuesQuery.append(":high_52w_dt, ");
            updateQuery.append("high_52w_dt = :high_52w_dt, ");
            refDataMap.put("high_52w_dt", Date.valueOf(refData.getHigh52Dt()));
        }
        if (refData.getUpperBand() != null) {
            insertQuery.append("upper_band, ");
            valuesQuery.append(":upper_band, ");
            updateQuery.append("upper_band = :upper_band, ");
            refDataMap.put("upper_band", refData.getUpperBand());
        }
        if (refData.getLowerBand() != null) {
            insertQuery.append("lower_band, ");
            valuesQuery.append(":lower_band, ");
            updateQuery.append("lower_band = :lower_band, ");
            refDataMap.put("lower_band", refData.getLowerBand());
        }
        if (refData.getPriceBand() != null) {
            insertQuery.append("price_band, ");
            valuesQuery.append(":price_band, ");
            updateQuery.append("price_band = :price_band, ");
            refDataMap.put("price_band", refData.getPriceBand());
        }
        if (refData.getListedStatus() != null) {
            insertQuery.append("listed_status, ");
            valuesQuery.append(":listed_status, ");
            updateQuery.append("listed_status = :listed_status, ");
            refDataMap.put("listed_status", refData.getListedStatus());
        }
        if (refData.getListedDt() != null) {
            insertQuery.append("listed_dt, ");
            valuesQuery.append(":listed_dt, ");
            updateQuery.append("listed_dt = :listed_dt, ");
            refDataMap.put("listed_dt", Date.valueOf(refData.getListedDt()));
        }
        if (refData.getTradingStatus() != null) {
            insertQuery.append("trading_status, ");
            valuesQuery.append(":trading_status, ");
            updateQuery.append("trading_status = :trading_status, ");
            refDataMap.put("trading_status", refData.getTradingStatus());
        }
        if (refData.getAdjustedPE() != null) {
            insertQuery.append("adjusted_pe, ");
            valuesQuery.append(":adjusted_pe, ");
            updateQuery.append("adjusted_pe = :adjusted_pe, ");
            refDataMap.put("adjusted_pe", refData.getAdjustedPE());
        }
        if (refData.getSymbolPE() != null) {
            insertQuery.append("symbol_pe, ");
            valuesQuery.append(":symbol_pe, ");
            updateQuery.append("symbol_pe = :symbol_pe, ");
            refDataMap.put("symbol_pe", refData.getSymbolPE());
        }
        if (refData.getSectoralIndex() != null) {
            insertQuery.append("sect_index, ");
            valuesQuery.append(":sect_index, ");
            updateQuery.append("sect_index = :sect_index, ");
            refDataMap.put("sect_index", refData.getSectoralIndex());
        }
        if (refData.getBasicIndustry() != null) {
            insertQuery.append("basic_industry, ");
            valuesQuery.append(":basic_industry, ");
            updateQuery.append("basic_industry = :basic_industry, ");
            refDataMap.put("basic_industry", refData.getBasicIndustry());
        }
        if (refData.getBoardStatus() != null) {
            insertQuery.append("board_status, ");
            valuesQuery.append(":board_status, ");
            updateQuery.append("board_status = :board_status, ");
            refDataMap.put("board_status", refData.getBoardStatus());
        }
        if (refData.getTradingSegment() != null) {
            insertQuery.append("trading_segment, ");
            valuesQuery.append(":trading_segment, ");
            updateQuery.append("trading_segment = :trading_segment, ");
            refDataMap.put("trading_segment", refData.getTradingSegment());
        }
        if (refData.getSharesClass() != null) {
            insertQuery.append("shares_class, ");
            valuesQuery.append(":shares_class, ");
            updateQuery.append("shares_class = :shares_class, ");
            refDataMap.put("shares_class", refData.getSharesClass());
        }
        if (refData.getFinancialResults() != null) {
            insertQuery.append("fin_results, ");
            valuesQuery.append(":fin_results, ");
            updateQuery.append("fin_results = :fin_results, ");
            refDataMap.put("fin_results", refData.getFinancialResults());

            if (refData.getTotIncomeInCr() != null) {
                insertQuery.append("tot_income_cr, ");
                valuesQuery.append(":tot_income_cr, ");
                updateQuery.append("tot_income_cr = :tot_income_cr, ");
                refDataMap.put("tot_income_cr", refData.getTotIncomeInCr());
            }
            if (refData.getNetPnLInCr() != null) {
                insertQuery.append("net_pnl_cr, ");
                valuesQuery.append(":net_pnl_cr, ");
                updateQuery.append("net_pnl_cr = :net_pnl_cr, ");
                refDataMap.put("net_pnl_cr", refData.getNetPnLInCr());
            }
            if (refData.getEarningsPerShare() != null) {
                insertQuery.append("earnings_share, ");
                valuesQuery.append(":earnings_share, ");
                updateQuery.append("earnings_share = :earnings_share, ");
                refDataMap.put("earnings_share", refData.getEarningsPerShare());
            }
        }
        if (refData.getShareholdingPatterns() != null) {
            insertQuery.append("holding_patterns, ");
            valuesQuery.append(":holding_patterns, ");
            updateQuery.append("holding_patterns = :holding_patterns, ");
            refDataMap.put("holding_patterns", refData.getShareholdingPatterns());

            if (refData.getPromoterSHP() != null) {
                insertQuery.append("promoter_holding, ");
                valuesQuery.append(":promoter_holding, ");
                updateQuery.append("promoter_holding = :promoter_holding, ");
                refDataMap.put("promoter_holding", refData.getPromoterSHP());
            }
            if (refData.getPublicSHP() != null) {
                insertQuery.append("public_holding, ");
                valuesQuery.append(":public_holding, ");
                updateQuery.append("public_holding = :public_holding, ");
                refDataMap.put("public_holding", refData.getPublicSHP());
            }
        }
        if (refData.getCorpActions() != null) {
            insertQuery.append("corp_actions, ");
            valuesQuery.append(":corp_actions, ");
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

        if (refData.getName() != null) {
            insertQuery.append("name, ");
            valuesQuery.append(":name, ");
            updateQuery.append("name = :name, ");
            refDataMap.put("name", refData.getName());
        }
        if (refData.getIsin() != null) {
            insertQuery.append("isin, ");
            valuesQuery.append(":isin, ");
            updateQuery.append("isin = :isin, ");
            refDataMap.put("isin", refData.getIsin());
        }
        if (refData.getSeries() != null) {
            insertQuery.append("series, ");
            valuesQuery.append(":series, ");
            updateQuery.append("series = :series, ");
            refDataMap.put("series", refData.getSeries());
        }

        insertQuery.append("inst_updated_at, status)");
        valuesQuery.append(":inst_updated_at, :status)");
        updateQuery.append("inst_updated_at = :inst_updated_at, status = :status");
        refDataMap.put("inst_updated_at", Timestamp.valueOf(LocalDateTime.now()));
        refDataMap.put("symbol", refData.getSymbol());

        final String upsertQuery = String.join(" ",
                insertQuery,
                valuesQuery,
                "ON CONFLICT (symbol) DO",
                updateQuery);

        int updateResult = namedJdbcTemplate.update(upsertQuery, refDataMap);

        log.info("Symbol: {}, Status: {}, Update result: {}", refData.getSymbol(), refDataMap.get("status"), updateResult);
    }

    @Override
    public List<String> getInstruments(List<String> statuses) {
        final String symbolsQuery = "SELECT symbol FROM stocks.refdata WHERE status IN (:status) AND symbol NOT LIKE 'NIFTY%' AND (inst_updated_at IS null OR inst_updated_at < :updated_at)";
        final Map<String, Object> params = Map.of("status", statuses, "updated_at", Timestamp.valueOf(LocalDateTime.now().minusMinutes(refreshIntervalMins)));

        return namedJdbcTemplate.queryForList(symbolsQuery, params, String.class);
    }

    @Override
    public List<String> getInstruments(MarketType marketType) {
        final StringBuilder symbolsQuery = new StringBuilder("SELECT symbol FROM stocks.refdata WHERE symbol NOT LIKE 'NIFTY%' AND category IN (:category) AND (inst_updated_at IS null OR inst_updated_at < :updated_at)");
        final Map<String, Object> params = Map.of("category", InstrumentType.getCategory(marketType.name()), "updated_at", Timestamp.valueOf(LocalDateTime.now().minusMinutes(reloadIntervalMins)));

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
        final StringBuilder symbolsQuery = new StringBuilder("SELECT * FROM stocks.refdata WHERE symbol NOT LIKE 'NIFTY%' AND category IN (:category) AND basic_industry = :basic_industry");
        final Map<String, Object> params = Map.of("category", InstrumentType.getCategory(marketType.name()), "basic_industry", industry);

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

        symbolsQuery.append(" ORDER BY ").append(orderBy);

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

    @Override
    public void save(BuyNSell buyNSell) {

        final String upsertQuery = String.join(" ",
                "INSERT INTO stocks.buynsell(symbol, side, price, qty, ltp, face_val, priority)",
                "VALUES(:symbol, :side, :price, :qty, (SELECT ltp FROM stocks.refdata WHERE symbol = :symbol), (SELECT face_val FROM stocks.refdata WHERE symbol = :symbol), :priority)",
                "ON CONFLICT (symbol, side)",
                "DO UPDATE SET price = :price, qty = :qty, ltp = (SELECT ltp FROM stocks.refdata WHERE symbol = :symbol), face_val = (SELECT face_val FROM stocks.refdata WHERE symbol = :symbol), priority = :priority, updated_at = :updated_at");
        final Map<String, Object> dataMap = Map.of("symbol", buyNSell.symbol(),
                "side", buyNSell.side().name(),
                "price", buyNSell.price(),
                "qty", buyNSell.qty(),
                "priority", buyNSell.priority().name(),
                "updated_at", Timestamp.valueOf(LocalDateTime.now()));

        int updateResult = namedJdbcTemplate.update(upsertQuery, dataMap);
        log.info("Symbol: {}, Side: {}, Update result: {}", buyNSell.symbol(), buyNSell.side(), updateResult);
    }

    @Override
    public List<String> getBnSInstruments(Side side) {
        final String symbolsQuery = "SELECT distinct symbol FROM stocks.buynsell WHERE side = :side";
        final Map<String, Object> params = Map.of("side", side.name());

        return namedJdbcTemplate.queryForList(symbolsQuery, params, String.class);
    }

    @Override
    public List<BuyNSellResult> getBnSInstruments(Side side, Double range, String orderBy) {
        String symbolsQuery = "SELECT rd.*, rd.ltp as current_ltp, bns.* FROM stocks.buynsell bns, stocks.v_refdata rd " +
                "WHERE bns.symbol = rd.symbol AND bns.side = :side AND bns.price > (1 - :range) * rd.ltp ORDER BY " + orderBy;
        final Map<String, Object> params = Map.of("side", side.name(), "range", range);

        return namedJdbcTemplate.query(symbolsQuery, params, (rs, rowNum) -> {
            BuyNSellResult result = new BuyNSellResult();
            result.setRowNum(rowNum);
            result.setName(rs.getString("name"));
            result.setSymbol(rs.getString("symbol"));
            result.setMarketCap(rs.getString("cap"));
            result.setCurrentLtp(rs.getDouble("current_ltp"));
            result.setPrice(rs.getDouble("price"));
            result.setQty(rs.getInt("qty"));
            result.setLtp(rs.getDouble("ltp"));
            result.setPriority(rs.getString("priority"));
            result.setLow52w(rs.getDouble("low_52w"));
            result.setHigh52w(rs.getDouble("high_52w"));
            result.setSymbolPE(rs.getDouble("symbol_pe"));
            result.setFaceVal(rs.getDouble("face_val"));
            result.setEarningsPerShare(rs.getDouble("earnings_share"));
            result.setPerChange30D(rs.getDouble("per_chng_30d"));
            result.setPerChange365D(rs.getDouble("per_chng_365d"));
            result.setPromoterHolding(rs.getDouble("promoter_holding"));
            result.setPublicHolding(rs.getDouble("public_holding"));
            result.setBasicIndustry(rs.getString("basic_industry"));
            result.setSectoralIndex(rs.getString("sect_index"));
            return result;
        });
    }

}
