# 📈 Ticker Tracker

**Ticker Tracker** is a Spring Boot application that scrapes equity stock data from the **NSE India website** using **Selenium WebDriver**, exposes it via **REST APIs**, and persists the data to a **PostgreSQL** database.

---

## 🚀 Features

- 🔍 Scrapes equity data from [https://www.nseindia.com](https://www.nseindia.com)
- 🤖 Uses Selenium with headless Chrome/Firefox for automation
- ☕ Built with Spring Boot and RESTful web services
- 🗄️ Persists scraped data into PostgreSQL
- 📦 Modular, testable architecture (Service, DAO, Controller layers)
- 🕒 Scheduled scraping support (optional via Spring Scheduler)

---

## 📦 Tech Stack

| Layer       | Technology                  |
|-------------|-----------------------------|
| Backend     | Spring Boot (3.x), REST API |
| Scraping    | Selenium WebDriver          |
| Database    | PostgreSQL                  |
| ORM         | Spring JDBCTemplate         |
| Build Tool  | Maven                       |
| Others      | Lombok, SLF4J, JUnit 5      |

---

## 🧾 Sample API

- `GET /api/tickers`  
  Returns all stored equity data

- `GET /api/tickers/{symbol}`  
  Returns the latest data for a specific stock symbol

- `POST /api/scrape`  
  Triggers on-demand scraping and saves results to DB

---

## 🧱 Database Schema

```sql
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
);
