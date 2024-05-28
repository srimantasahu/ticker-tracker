------------------------------------------------------------------------------------------------------------------------


-- Database: nsedata

-- DROP DATABASE IF EXISTS nsedata;

CREATE DATABASE nsedata
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'C'
    LC_CTYPE = 'C'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;


------------------------------------------------------------------------------------------------------------------------


-- SCHEMA: stocks

-- DROP SCHEMA IF EXISTS stocks ;

CREATE SCHEMA IF NOT EXISTS stocks
    AUTHORIZATION postgres;


------------------------------------------------------------------------------------------------------------------------


-- Table: stocks.refdata

-- DROP TABLE IF EXISTS stocks.refdata;

CREATE TABLE IF NOT EXISTS stocks.refdata
(
    symbol character varying(25) COLLATE pg_catalog."default" NOT NULL,
    name character varying(80),
    isin character(12),
    ltp double precision,
    low_52w double precision,
    high_52w double precision,
    per_chng_30d double precision,
    per_chng_365d double precision,
	adjusted_pe double precision,
    symbol_pe double precision,
    prev_close double precision,
    open double precision,
    low double precision,
    high double precision,
    chng double precision,
    per_chng double precision,
	buy_qty bigint,
    sell_qty bigint,
    per_traded_qty double precision,
    volume_lk double precision,
    value_cr double precision,
	tot_mar_cap_cr double precision,
    ff_mar_cap_cr double precision,
    impact_cost double precision,
    app_mar_rate double precision,
	face_val integer,
	upper_band double precision,
    lower_band double precision,
    price_band character varying(20),
	low_52w_dt date,
	high_52w_dt date,
    listed_status character varying(20),
    listed_dt date,
    trading_status character varying(20),
	sect_index character varying(50),
    shares_class character varying(20),
    basic_industry character varying(120),
    board_status character varying(50),
    trading_segment character varying(50),
    tot_income_cr double precision,
    net_pnl_cr double precision,
    earnings_share double precision,
    promoter_holding double precision,
    public_holding double precision,
    corp_actions text[],
    fin_results text[],
    holding_patterns text[],
	created_at timestamp default NOW(),
    file_updated_at timestamp default NOW(),
    inst_updated_at timestamp,
    status character varying(20) DEFAULT 'UNKNOWN',
    series character(2),
    category character varying(20),
    id bigserial NOT NULL,
    CONSTRAINT refdata_pk PRIMARY KEY (symbol)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS stocks.refdata

-- ALTER TABLE stocks.refdata ADD COLUMN status character varying(20) DEFAULT 'UNKNOWN'
-- ALTER TABLE stocks.refdata ALTER COLUMN basic_industry TYPE character varying(120)

-- ALTER TABLE stocks.refdata ADD COLUMN tot_income_cr double precision
-- ALTER TABLE stocks.refdata ADD COLUMN net_pnl_cr double precision;
-- ALTER TABLE stocks.refdata ADD COLUMN earnings_share double precision;
-- ALTER TABLE stocks.refdata ADD COLUMN promoter_holding double precision;
-- ALTER TABLE stocks.refdata ADD COLUMN public_holding double precision;
-- ALTER TABLE stocks.refdata ADD COLUMN name character varying(80);
-- ALTER TABLE stocks.refdata ALTER COLUMN name TYPE character varying(120);
-- ALTER TABLE stocks.refdata ADD COLUMN series character(2);
-- ALTER TABLE stocks.refdata ADD COLUMN category character varying(20);
-- ALTER TABLE stocks.refdata ALTER per_chng_30d DROP NOT NULL, ALTER per_chng_365d DROP NOT NULL;
-- ALTER TABLE stocks.refdata ALTER chng DROP NOT NULL, ALTER per_chng DROP NOT NULL;
-- ALTER TABLE stocks.refdata ALTER ltp DROP NOT NULL, ALTER low_52w DROP NOT NULL, ALTER high_52w DROP NOT NULL, ALTER open DROP NOT NULL, ALTER low DROP NOT NULL, ALTER high DROP NOT NULL;


------------------------------------------------------------------------------------------------------------------------


INSERT INTO stocks.refdata(
	symbol, ltp, chng, per_chng, open, high, low, prev_close, volume_lk, value_cr, high_52w, low_52w, per_chng_30d, per_chng_365d)
	VALUES ('TEST', 123.23, 2.1, 1.2, 120.5, 125.6, 122, 119.8, 1.2345, 123456.01, 159.4, 55.6, -18.2, 123.4)
ON CONFLICT (symbol) DO UPDATE SET ltp = 123.24, chng = 2.11;


COMMIT;


------------------------------------------------------------------------------------------------------------------------


select * from stocks.refdata;
select symbol, ltp from stocks.refdata where ltp < 200;


------------------------------------------------------------------------------------------------------------------------


-- Table: stocks.buynsell

-- DROP TABLE IF EXISTS stocks.buynsell;

CREATE TABLE IF NOT EXISTS stocks.buynsell
(
    symbol character varying(25) REFERENCES stocks.refdata,
    side character varying(5) NOT NULL,
    price double precision NOT NULL,
	qty bigint NOT NULL,
	face_val integer,
	ltp double precision NOT NULL,
	priority character varying(10) DEFAULT 'NA',
	updated_at timestamp default NOW(),
    id bigserial NOT NULL,
    CONSTRAINT buynsell_pk PRIMARY KEY (symbol, side, ltp)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS stocks.buynsell

-- ALTER TABLE stocks.buynsell ADD COLUMN face_val integer
-- update stocks.buynsell bns set face_val = (select face_val from stocks.refdata rd where bns.symbol = rd.symbol)
-- ALTER TABLE stocks.buynsell ADD COLUMN priority character varying(10)
-- update stocks.buynsell bns set priority = 'NA'


------------------------------------------------------------------------------------------------------------------------
SELECT ltp FROM stocks.refdata WHERE symbol = 'THYROCARE'

INSERT INTO stocks.buynsell(
	symbol, side, price, qty, ltp)
	VALUES ('INFY', 'BUY', 1400.0, 10, (SELECT COALESCE(ltp, 0) FROM stocks.refdata WHERE symbol = 'INFY'))
ON CONFLICT (symbol, side, ltp) DO UPDATE SET price = 123.24, qty = 12, updated_at = NOW();


INSERT INTO stocks.buynsell(
	symbol, side, price, qty, ltp)
	VALUES ('INFY', 'SELL', 1600.0, 5, (SELECT ltp FROM stocks.refdata WHERE symbol = 'INFY'))
ON CONFLICT (symbol, side, ltp) DO UPDATE SET price = 1600.0, qty = 4, updated_at = NOW();

COMMIT;


------------------------------------------------------------------------------------------------------------------------


select * from stocks.buynsell;
select * from stocks.buynsell order by side, updated_at;


------------------------------------------------------------------------------------------------------------------------


