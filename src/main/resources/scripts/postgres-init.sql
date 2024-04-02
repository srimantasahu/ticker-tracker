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
    id bigserial NOT NULL,
    symbol character varying(25) COLLATE pg_catalog."default" NOT NULL,
    ltp double precision NOT NULL,
    chng double precision NOT NULL,
    percent_chng double precision NOT NULL,
    open double precision NOT NULL,
    high double precision NOT NULL,
    low double precision NOT NULL,
    prev_close double precision,
    volume_sh bigint NOT NULL,
    value_cr double precision NOT NULL,
    high_52w double precision,
    low_52w double precision,
    percent_chng_30d double precision,
    percent_chng_365d double precision,
    updated_at timestamp default NOW(),
    CONSTRAINT refdata_pk PRIMARY KEY (symbol)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS stocks.refdata


------------------------------------------------------------------------------------------------------------------------


INSERT INTO stocks.refdata(
	symbol, ltp, chng, percent_chng, open, high, low, prev_close, volume_sh, value_cr, high_52w, low_52w, percent_chng_30d, percent_chng_365d)
	VALUES ('MSN', 123.23, 2.1, 1.2, 120.5, 125.6, 122, 119.8, 123456, 123456789.01, 159.4, 55.6, -18.2, 123.4)
ON CONFLICT (symbol) DO UPDATE SET ltp = 123.24, chng = 2.11;


COMMIT;


------------------------------------------------------------------------------------------------------------------------


select * from stocks.refdata;
select symbol, ltp from stocks.refdata where ltp < 200;


------------------------------------------------------------------------------------------------------------------------