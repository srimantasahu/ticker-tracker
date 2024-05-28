-- select basic_industry, count(*) from stocks.refdata group by basic_industry order by count(*), basic_industry;

-- select * from stocks.refdata where symbol in ('LTF', 'L&TFH');

-- select * from stocks.refdata where basic_industry = 'Hospital' order by category, ltp/face_val asc;

/*
select *
from stocks.v_refdata 
where 1=1
and basic_industry = 'Gas Transmission/Marketing'
-- and ltp < (low_52w + high_52w)/2 
-- and symbol IN ('MHHL', 'MWL', 'SPARC', 'SEQUENT', 'MARKSANS', 'YATHARTH', 'RTNPOWER', 'ADVENZYMES', 'PPLPHARMA', 'ASTERDM', 'ASHOKLEY', 'BIOCON', 'FORTIS', 'BERGEPAINT', 'SYNGENE', 'GMRINFRA', 'VEDL')
order by category, ltp/face_val asc;
*/

/*

select * from stocks.v_refdata 
where 1=1
and basic_industry = 'Software Products'
and ltp < 500
and cap in ('MID CAP', 'SMALL CAP', 'MICRO CAP')
order by ltp/face_val asc;

*/

-- select * from stocks.buynsell where symbol = 'INFY' order by side, updated_at;

select rd.cap, rd.face_val, rd.ltp as current_ltp, bns.* from stocks.buynsell bns, stocks.v_refdata rd
where bns.symbol = rd.symbol 
and bns.price > 0.8 * rd.ltp 
and rd.face_val > 2
and rd.cap in ('MID CAP', 'SMALL CAP', 'MICRO CAP')
order by (rd.ltp-price)/rd.face_val;


update stocks.buynsell bns set face_val = (select face_val from stocks.refdata rd where bns.symbol = rd.symbol)
