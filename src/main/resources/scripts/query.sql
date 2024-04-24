------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: sanity checks

-- select * from stocks.refdata

-- select * from stocks.refdata where symbol = 'DABUR'

-- select * from stocks.refdata where status = 'SKIPPED' and inst_updated_at < NOW() - INTERVAL '1 HOUR'

-- select status, count(*) from stocks.refdata group by status order by count desc

-- select rank_number, symbol, tot_mar_cap_cr from (select *, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number <= 10

-- select count(*) from stocks.refdata where inst_updated_at < NOW() - INTERVAL '1 HOUR'


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: stocks near to low_52w

-- select * from stocks.refdata where ltp < (low_52w + high_52w)/2 order by ltp - low_52w


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: distinct values

-- select distinct sect_index from stocks.refdata where sect_index is not null order by sect_index

-- "NA"
-- "NIFTY 500"
-- "NIFTY AUTO"
-- "NIFTY BANK"
-- "NIFTY COMMODITIES"
-- "NIFTY FINANCIAL SERVICES"
-- "NIFTY FMCG"
-- "NIFTY INDIA CONSUMPTION"
-- "NIFTY INFRASTRUCTURE"
-- "NIFTY IT"
-- "NIFTY MEDIA"
-- "NIFTY METAL"
-- "NIFTY PHARMA"
-- "NIFTY REALTY"
-- "NIFTY SERVICES SECTOR"

-- select distinct basic_industry from stocks.refdata where basic_industry is not null order by basic_industry asc

-- "2/3 Wheelers"
-- "Abrasives & Bearings"
-- "Aerospace & Defense"
-- "Airline"
-- "Airport & Airport services"
-- "Aluminium"
-- "Amusement Parks/ Other Recreation"
-- "Animal Feed"
-- "Asset Management Company"
-- "Auto Components & Equipments"
-- "Auto Dealer"
-- "Biotechnology"
-- "Breweries & Distilleries"
-- "Business Process Outsourcing (BPO)/ Knowledge Process Outsourcing (KPO)"
-- "Cables - Electricals"
-- "Carbon Black"
-- "Castings & Forgings"
-- "Cement & Cement Products"
-- "Ceramics"
-- "Cigarettes & Tobacco Products"
-- "Civil Construction"
-- "Coal"
-- "Commercial Vehicles"
-- "Commodity Chemicals"
-- "Compressors Pumps & Diesel Engines"
-- "Computers - Software & Consulting"
-- "Computers Hardware & Equipments"
-- "Construction Vehicles"
-- "Consumer Electronics"
-- "Copper"
-- "Dairy Products"
-- "Depositories Clearing Houses and Other Intermediaries"
-- "Digital Entertainment"
-- "Diversified"
-- "Diversified Commercial Services"
-- "Diversified FMCG"
-- "Diversified Metals"
-- "Diversified Retail"
-- "Dyes And Pigments"
-- "E-Retail/ E-Commerce"
-- "Edible Oil"
-- "Education"
-- "Electrodes & Refractories"
-- "Exchange and Data Platform"
-- "Explosives"
-- "Ferro & Silica Manganese"
-- "Fertilizers"
-- "Film Production Distribution & Exhibition"
-- "Financial Institution"
-- "Financial Products Distributor"
-- "Financial Technology (Fintech)"
-- "Footwear"
-- "Furniture Home Furnishing"
-- "Garments & Apparels"
-- "Gas Transmission/Marketing"
-- "Gems Jewellery And Watches"
-- "General Insurance"
-- "Glass - Consumer"
-- "Glass - Industrial"
-- "Healthcare Research Analytics & Technology"
-- "Healthcare Service Provider"
-- "Heavy Electrical Equipment"
-- "Holding Company"
-- "Hospital"
-- "Hotels & Resorts"
-- "Household Appliances"
-- "Household Products"
-- "Houseware"
-- "Housing Finance Company"
-- "IT Enabled Services"
-- "Industrial Gases"
-- "Industrial Minerals"
-- "Industrial Products"
-- "Integrated Power Utilities"
-- "Internet & Catalogue Retail"
-- "Investment Company"
-- "Iron & Steel"
-- "Iron & Steel Products"
-- "LPG/CNG/PNG/LNG Supplier"
-- "Life Insurance"
-- "Logistics Solution Provider"
-- "Lubricants"
-- "Meat Products including Poultry"
-- "Media & Entertainment"
-- "Medical Equipment & Supplies"
-- "Microfinance Institutions"
-- "Non Banking Financial Company (NBFC)"
-- "Oil Exploration & Production"
-- "Other Agricultural Products"
-- "Other Bank"
-- "Other Beverages"
-- "Other Electrical Equipment"
-- "Other Financial Services"
-- "Other Industrial Products"
-- "Other Telecom Services"
-- "Other Textile Products"
-- "Packaged Foods"
-- "Packaging"
-- "Paints"
-- "Paper & Paper Products"
-- "Passenger Cars & Utility Vehicles"
-- "Personal Care"
-- "Pesticides & Agrochemicals"
-- "Petrochemicals"
-- "Pharmaceuticals"
-- "Pharmacy Retail"
-- "Plastic Products - Consumer"
-- "Plastic Products - Industrial"
-- "Plywood Boards/ Laminates"
-- "Port & Port services"
-- "Power - Transmission"
-- "Power Distribution"
-- "Power Generation"
-- "Power Trading"
-- "Print Media"
-- "Printing & Publication"
-- "Private Sector Bank"
-- "Public Sector Bank"
-- "Railway Wagons"
-- "Ratings"
-- "Refineries & Marketing"
-- "Residential Commercial Projects"
-- "Restaurants"
-- "Sanitary Ware"
-- "Ship Building & Allied Services"
-- "Shipping"
-- "Software Products"
-- "Speciality Retail"
-- "Specialty Chemicals"
-- "Sponge Iron"
-- "Stationary"
-- "Stockbroking & Allied"
-- "Sugar"
-- "TV Broadcasting & Software Production"
-- "Tea & Coffee"
-- "Telecom - Cellular & Fixed line services"
-- "Telecom - Equipment & Accessories"
-- "Telecom - Infrastructure"
-- "Tour Travel Related Services"
-- "Tractors"
-- "Trading & Distributors"
-- "Trading - Gas"
-- "Trading - Minerals"
-- "Trading - Textile Products"
-- "Tyres & Rubber Products"
-- "Water Supply & Management"
-- "Zinc"


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: sectoral stocks near to low_52w

-- select * from stocks.refdata where sect_index = 'NIFTY PHARMA' order by ltp


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: industry stocks near to low_52w

-- select * from stocks.refdata where basic_industry = 'Hospital' order by ltp


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: sectoral stocks near to low_52w with good pe value

-- select * from stocks.refdata where ltp < (low_52w + high_52w)/2 and sect_index = 'NIFTY PHARMA' and symbol_pe < 20 order by ltp - low_52w


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

-- Query: stocks near to low_52w with low pe value and high earnings per share

-- all: order: ltp

select sect_index, symbol, low_52w, ltp, high_52w, adjusted_pe, symbol_pe, earnings_share, face_val, promoter_holding, public_holding, per_chng_365d, per_chng_30d, tot_mar_cap_cr, price_band, buy_qty, sell_qty from stocks.refdata 
where 1=1
order by 
ltp asc;


-- microcap: pe<5, ltp<20, order: ltp

select sect_index, basic_industry, symbol, name, ltp, high_52w, low_52w, prev_close, adjusted_pe, symbol_pe, earnings_share, face_val, promoter_holding, public_holding, per_chng_365d, per_chng_30d, tot_mar_cap_cr, price_band, buy_qty, sell_qty from stocks.refdata 
where 1=1
-- and ltp < 100
and ltp < (low_52w + high_52w)/2 
and face_val >= 10
and promoter_holding > 60
-- and promoter_holding > 50
-- and (symbol_pe is null or symbol_pe < 5)
-- and (adjusted_pe is null or adjusted_pe < 5)
and tot_mar_cap_cr < (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 500)
order by 
sect_index, ltp asc;


-- smallcap: pe<15, ltp<100, order: ltp-low_52w

select sect_index, basic_industry, symbol, prev_close, ltp, high_52w, low_52w, adjusted_pe, symbol_pe, earnings_share, face_val, promoter_holding, public_holding, per_chng_365d, per_chng_30d, tot_mar_cap_cr, price_band, buy_qty, sell_qty from stocks.refdata 
where 1=1
and ltp < 500
and promoter_holding > 50
and ltp < (low_52w + high_52w)/2 
-- and (symbol_pe < 30 or adjusted_pe < 30)
-- and face_val >= 2
-- and earnings_share > 0
and tot_mar_cap_cr < (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 250)
and tot_mar_cap_cr >= (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 500)
order by  
sect_index, ltp asc;


-- midcap: pe<20, ltp<200 and +eps, order: eps

select sect_index, symbol, ltp, high_52w, low_52w, adjusted_pe, symbol_pe, earnings_share, promoter_holding, public_holding, per_chng_365d, per_chng_30d, tot_mar_cap_cr, face_val, price_band, buy_qty, sell_qty from stocks.refdata 
where 1=1
and ltp < 200
and ltp < (low_52w + high_52w)/2 
and (symbol_pe is null or symbol_pe < 20) 
and (adjusted_pe is null or adjusted_pe < 20)
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
and (symbol_pe is null or symbol_pe < 50) 
and (adjusted_pe is null or adjusted_pe < 50)
and (earnings_share is null or earnings_share > 0) 
and tot_mar_cap_cr >= (select tot_mar_cap_cr from (select tot_mar_cap_cr, rank() over (order by tot_mar_cap_cr desc) rank_number from stocks.refdata where tot_mar_cap_cr is not null) rt where rank_number = 100)
order by 
per_chng_365d asc;


------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------