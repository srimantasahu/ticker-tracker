------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: sanity checks

-- select * from stocks.refdata

-- select * from stocks.refdata where symbol = 'DABUR'

-- select * from stocks.refdata where status = 'SKIPPED'

-- select status, count(*) from stocks.refdata group by status order by count desc

-- select rank_number, symbol, tot_mar_cap_cr from (select *, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number <= 10


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

-- microcap: pe<5, ltp<50, order: ltp

select sect_index, symbol, ltp, high_52w, low_52w, adjusted_pe, symbol_pe, earnings_share, promoter_holding, public_holding, per_chng_365d, per_chng_30d, tot_mar_cap_cr, face_val, price_band, buy_qty, sell_qty from stocks.refdata 
where 1=1
and ltp < 50
and (symbol_pe is null or symbol_pe < 5)
and (adjusted_pe is null or adjusted_pe < 5)
and tot_mar_cap_cr < (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 500)
order by 
ltp asc;


-- smallcap: pe<15, ltp<150, order: ltp-low_52w

select sect_index, symbol, ltp, high_52w, low_52w, adjusted_pe, symbol_pe, earnings_share, promoter_holding, public_holding, per_chng_365d, per_chng_30d, tot_mar_cap_cr, face_val, price_band, buy_qty, sell_qty from stocks.refdata 
where 1=1
and ltp < 150
and ltp < (low_52w + high_52w)/2 
and (symbol_pe is null or symbol_pe < 15)
and (adjusted_pe is null or adjusted_pe < 15)
and tot_mar_cap_cr < (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 250)
and tot_mar_cap_cr >= (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 500)
order by  
ltp - low_52w asc;


-- midcap: pe<25, ltp<250 and +eps, order: eps

select sect_index, symbol, ltp, high_52w, low_52w, adjusted_pe, symbol_pe, earnings_share, promoter_holding, public_holding, per_chng_365d, per_chng_30d, tot_mar_cap_cr, face_val, price_band, buy_qty, sell_qty from stocks.refdata 
where 1=1
and ltp < 250
and ltp < (low_52w + high_52w)/2 
and (symbol_pe is null or symbol_pe < 25) 
and (adjusted_pe is null or adjusted_pe < 25)
and (earnings_share is null or earnings_share > 0) 
and tot_mar_cap_cr < (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 100)
and tot_mar_cap_cr >= (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 250)
order by 
earnings_share desc;

-- largecap: pe<30, ltp<500 and +eps, order: 365d change

select sect_index, symbol, ltp, high_52w, low_52w, adjusted_pe, symbol_pe, earnings_share, promoter_holding, public_holding, per_chng_365d, per_chng_30d, tot_mar_cap_cr, face_val, price_band, buy_qty, sell_qty from stocks.refdata 
where 1=1
and ltp < 500
and ltp < (low_52w + high_52w)/2 
and (symbol_pe is null or symbol_pe < 30) 
and (adjusted_pe is null or adjusted_pe < 30)
and (earnings_share is null or earnings_share > 0) 
and tot_mar_cap_cr >= (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 100)
order by 
per_chng_365d asc;


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------