-- View: stocks.v_refdata

-- DROP VIEW stocks.v_refdata;

CREATE OR REPLACE VIEW stocks.v_refdata
 AS
 SELECT symbol,
    name,
    isin,
        CASE
            WHEN tot_mar_cap_cr >= (( SELECT rt.tot_mar_cap_cr
               FROM ( SELECT refdata_1.tot_mar_cap_cr,
                        rank() OVER (ORDER BY refdata_1.tot_mar_cap_cr DESC) AS rank_number
                       FROM stocks.refdata refdata_1
                      WHERE refdata_1.tot_mar_cap_cr IS NOT NULL) rt
              WHERE rt.rank_number = 100)) THEN 'LARGE CAP'::text
            WHEN tot_mar_cap_cr >= (( SELECT rt.tot_mar_cap_cr
               FROM ( SELECT refdata_1.tot_mar_cap_cr,
                        rank() OVER (ORDER BY refdata_1.tot_mar_cap_cr DESC) AS rank_number
                       FROM stocks.refdata refdata_1
                      WHERE refdata_1.tot_mar_cap_cr IS NOT NULL) rt
              WHERE rt.rank_number = 250)) THEN 'MID CAP'::text
            WHEN tot_mar_cap_cr >= (( SELECT rt.tot_mar_cap_cr
               FROM ( SELECT refdata_1.tot_mar_cap_cr,
                        rank() OVER (ORDER BY refdata_1.tot_mar_cap_cr DESC) AS rank_number
                       FROM stocks.refdata refdata_1
                      WHERE refdata_1.tot_mar_cap_cr IS NOT NULL) rt
              WHERE rt.rank_number = 500)) THEN 'SMALL CAP'::text
            WHEN tot_mar_cap_cr >= (( SELECT rt.tot_mar_cap_cr
               FROM ( SELECT refdata_1.tot_mar_cap_cr,
                        rank() OVER (ORDER BY refdata_1.tot_mar_cap_cr DESC) AS rank_number
                       FROM stocks.refdata refdata_1
                      WHERE refdata_1.tot_mar_cap_cr IS NOT NULL) rt
              WHERE rt.rank_number = 750)) THEN 'MICRO CAP'::text
            ELSE 'NA'::text
        END AS "case",
    low_52w,
    ltp,
    high_52w,
    face_val,
    symbol_pe,
    earnings_share,
    tot_income_cr,
    net_pnl_cr,
    per_chng_365d,
    per_chng_30d,
    promoter_holding,
    public_holding,
    category,
    series,
    basic_industry,
    sect_index
   FROM stocks.refdata;

ALTER TABLE stocks.v_refdata
    OWNER TO postgres;

