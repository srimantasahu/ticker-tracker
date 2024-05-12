-- select basic_industry, count(*) from stocks.refdata group by basic_industry order by count(*), basic_industry;

-- select * from stocks.refdata where symbol = 'MADHUSUDAN';

-- select * from stocks.buynsell where symbol = 'INFY' order by side, updated_at;

-- select * from stocks.refdata where basic_industry = 'Hospital' order by category, ltp/face_val asc;

select *
from stocks.v_refdata 
where 1=1
and basic_industry = 'Film Production Distribution & Exhibition'
-- and ltp < (low_52w + high_52w)/2 
-- and symbol IN ('MHHL', 'MWL', 'SPARC', 'SEQUENT', 'MARKSANS', 'YATHARTH', 'RTNPOWER', 'ADVENZYMES', 'PPLPHARMA', 'ASTERDM', 'ASHOKLEY', 'BIOCON', 'FORTIS', 'BERGEPAINT', 'SYNGENE', 'GMRINFRA', 'VEDL')
order by category, ltp/face_val asc;


