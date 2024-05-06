-- select basic_industry, count(*) from stocks.refdata group by basic_industry order by count(*), basic_industry;

-- select * from stocks.refdata where basic_industry = 'Hospital' order by category, ltp/face_val asc;


select *
from stocks.v_refdata 
where 1=1
and basic_industry = 'Hospital'
-- and symbol = 'BERGEPAINT'
order by category, ltp/face_val asc;



