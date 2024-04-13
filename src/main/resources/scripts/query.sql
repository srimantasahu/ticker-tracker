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

-- Query: stocks near to low_52w with low pe value and high earnings per share

-- pe<20 and +eps, order by ltp

-- select sect_index, symbol, ltp, high_52w, low_52w, adjusted_pe, symbol_pe, earnings_share, promoter_holding, public_holding, per_chng_365d, per_chng_30d, face_val, price_band, buy_qty, sell_qty from stocks.refdata 
-- where symbol_pe < 20 and earnings_share > 0 and ltp < (low_52w + high_52w)/2 order by ltp asc, earnings_share desc, ltp asc, sect_index asc

-- pe<10 and +eps, order by eps

-- select sect_index, symbol, ltp, high_52w, low_52w, adjusted_pe, symbol_pe, earnings_share, promoter_holding, public_holding, per_chng_365d, per_chng_30d, face_val, price_band, buy_qty, sell_qty from stocks.refdata 
-- where symbol_pe < 10 and earnings_share > 0 and ltp < (low_52w + high_52w)/2 order by earnings_share desc, ltp asc, sect_index asc


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------