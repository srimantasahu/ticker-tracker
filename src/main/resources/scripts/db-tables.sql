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
    id integer NOT NULL DEFAULT nextval('stocks.refdata_id_seq'::regclass),
    symbol character varying(25) COLLATE pg_catalog."default" NOT NULL,
    ltp double precision NOT NULL,
    chng double precision NOT NULL,
    percent_chng double precision NOT NULL,
    open double precision NOT NULL,
    high double precision NOT NULL,
    low double precision NOT NULL,
    prev_close double precision NOT NULL,
    volume bigint NOT NULL,
    value double precision NOT NULL,
    high_52w double precision,
    low_52w double precision,
    percent_chng_30d double precision,
    percent_chng_365d double precision,
    CONSTRAINT refdata_pk PRIMARY KEY (symbol)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS stocks.refdata



------------------------------------------------------------------------------------------------------------------------


