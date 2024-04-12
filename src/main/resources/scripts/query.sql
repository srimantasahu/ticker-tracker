------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: sanity checks

-- select * from stocks.refdata

-- select * from stocks.refdata where symbol = 'DABUR'

-- select * from stocks.refdata where status = 'SKIPPED'

-- select status, count(*) from stocks.refdata group by status order by count desc


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: distinct values

-- select distinct sect_index from stocks.refdata where sect_index is not null

-- select distinct basic_industry from stocks.refdata where basic_industry is not null order by basic_industry asc


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: stocks near to low_52w

-- select * from stocks.refdata where ltp < (low_52w + high_52w)/2 order by ltp - low_52w


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: sectoral stocks near to low_52w

-- select * from stocks.refdata where ltp < (low_52w + high_52w)/2 and sect_index = 'NIFTY PHARMA' order by ltp


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: sectoral stocks near to low_52w with good pe value

-- select * from stocks.refdata where ltp < (low_52w + high_52w)/2 and sect_index = 'NIFTY PHARMA' and symbol_pe < 20 order by ltp - low_52w


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: sectoral stocks near to low_52w with low pe value and high earnings per share

-- select sect_index, symbol, ltp, high_52w, low_52w, symbol_pe, earnings_share, promoter_holding, per_chng_30d, per_chng_365d, price_band, face_val, buy_qty, sell_qty from stocks.refdata 
-- where ltp < (low_52w + high_52w)/2 and symbol_pe is null or symbol_pe < 10 order by sect_index asc, ltp asc, earnings_share desc


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------