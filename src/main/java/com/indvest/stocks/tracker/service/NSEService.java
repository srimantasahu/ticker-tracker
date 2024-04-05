package com.indvest.stocks.tracker.service;

import com.google.common.net.UrlEscapers;
import com.indvest.stocks.tracker.bean.RefData;
import com.indvest.stocks.tracker.bean.Status;
import com.indvest.stocks.tracker.bean.StatusMessage;
import com.indvest.stocks.tracker.repository.NSERepository;
import com.indvest.stocks.tracker.util.CommonUtil;
import com.indvest.stocks.tracker.util.SeleniumUtil;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
public class NSEService {
    private static final Logger log = LoggerFactory.getLogger(NSEService.class);

    @Value("${nse.download.path}")
    private String downloadPath;

    @Value("${nse.download.wait.timeout}")
    private int waitTimeout;

    @Value("${nse.download.poll.interval}")
    private int pollInterval;

    @Autowired
    private NSERepository nseRepository;

    public StatusMessage downloadStocksData(String entity) {
        if (StringUtils.isBlank(entity)) {
            return new StatusMessage(Status.INVALID_INPUT, "Require a value for entity");
        }

        WebDriver driver = SeleniumUtil.getWebDriver(downloadPath, false);

        try {
            // connecting to the target web page
            driver.get("https://www.nseindia.com/market-data/live-equity-market?symbol=" + UrlEscapers.urlFragmentEscaper().escape(entity));

            FluentWait<WebDriver> wait = new FluentWait<>(driver);
            wait.withTimeout(Duration.ofMillis(waitTimeout));
            wait.pollingEvery(Duration.ofMillis(pollInterval));
            wait.ignoring(NoSuchElementException.class);

            wait.until(d -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("dwldcsv")));

            final String expectedFileName = downloadPath + CommonUtil.getNSEFileName(entity);

            log.info("Checking old file for: {}", expectedFileName);

            File file = new File(expectedFileName);
            if (file.exists()) {
                log.info("Deleted existing file: {}", file.delete());
            }

            WebElement dwldcsv = driver.findElement(By.linkText("Download (.csv)"));

            //FIXME: check why it's not ready for execution everytime
            Thread.sleep(5000);

            dwldcsv.click();

            log.info("Clicked on download csv");

            wait.until(d -> new File(expectedFileName).exists());

            log.info("Downloaded csv file exists in path");
        } catch (Exception e) {
            log.error("Error message: {}", e.getMessage(), e);
            return new StatusMessage(Status.ERROR, e.getMessage());
        } finally {
            driver.quit();
        }

        return new StatusMessage(Status.SUCCESS, "File downloaded successfully");
    }

    public StatusMessage storeStocksData(String entity) {
        if (StringUtils.isBlank(entity)) {
            return new StatusMessage(Status.INVALID_INPUT, "Require a value for entity");
        }


        final String expectedFileName = downloadPath + CommonUtil.getNSEFileName(entity);

        try {
            List<Map<String, String>> refDataList = loadRefData(expectedFileName);
            log.info("Extracted ref data list size: {}", refDataList.size());

            nseRepository.save(refDataList);
        } catch (Exception e) {
            log.error("Error message: {}", e.getMessage());
            return new StatusMessage(Status.ERROR, e.getMessage());
        }

        return new StatusMessage(Status.SUCCESS, "Ref data stored successfully");
    }

    public StatusMessage loadStocksData(String symbol) {
        if (StringUtils.isBlank(symbol)) {
            return new StatusMessage(Status.INVALID_INPUT, "Require a value for symbol");
        }

        try {
            final RefData refData = extractRefData(symbol);

            nseRepository.save(refData);
        } catch (Exception e) {
            log.error("Error message: {}", e.getMessage());
            return new StatusMessage(Status.ERROR, e.getMessage());
        }

        return new StatusMessage(Status.SUCCESS, "Ref data loaded successfully");
    }

    private RefData extractRefData(String symbol) throws Exception {
        final RefData refData = new RefData(symbol);
        
        WebDriver driver = SeleniumUtil.getWebDriver(downloadPath, false);
        
        try {
            // connecting to the target web page
            driver.get("https://www.nseindia.com/get-quotes/equity?symbol=" + UrlEscapers.urlFragmentEscaper().escape(symbol));

            FluentWait<WebDriver> wait = new FluentWait<>(driver);
            wait.withTimeout(Duration.ofMillis(waitTimeout));
            wait.pollingEvery(Duration.ofMillis(pollInterval));
            wait.ignoring(NoSuchElementException.class);

            wait.until(d -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"orderbk\"]")));

            Thread.sleep(5000);

            String text = driver.findElement(By.xpath("//*[@id=\"orderBuyTq\"]")).getText();

//            Predicate<String> valid = (s) ->
            
            log.info("order book buy qty: {}", text);
//            if (valid(text))
//            refData.put("BUYQTY", text);

            log.info("order book sell qty: {}", driver.findElement(By.xpath("//*[@id=\"orderSellTq\"]")).getText());

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"Trade_Information_pg\"]")));

            log.info("order book trade vol in lk: {}", driver.findElement(By.xpath("//*[@id=\"orderBookTradeVol\"]")).getText());
            log.info("order book trade value in cr: {}", driver.findElement(By.xpath("//*[@id=\"orderBookTradeVal\"]")).getText());
            log.info("order book total market cap in cr: {}", driver.findElement(By.xpath("//*[@id=\"orderBookTradeTMC\"]")).getText());
            log.info("order book free float market cap in cr: {}", driver.findElement(By.xpath("//*[@id=\"orderBookTradeFFMC\"]")).getText());
            log.info("order book impact cost: {}", driver.findElement(By.xpath("//*[@id=\"orderBookTradeIC\"]")).getText());
            log.info("order book percent traded qty: {}", driver.findElement(By.xpath("//*[@id=\"orderBookDeliveryTradedQty\"]")).getText());
            log.info("order book applicable margin rate: {}", driver.findElement(By.xpath("//*[@id=\"orderBookAppMarRate\"]")).getText());
            log.info("order book face value: {}", driver.findElement(By.xpath("//*[@id=\"mainFaceValue\"]")).getText());


            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"priceInformationHeading\"]")));

            log.info("52 week high: {}", driver.findElement(By.xpath("//*[@id=\"week52highVal\"]")).getText());
            log.info("52 week high date: {}", driver.findElement(By.xpath("//*[@id=\"week52HighDate\"]")).getText());
            log.info("52 week low: {}", driver.findElement(By.xpath("//*[@id=\"week52lowVal\"]")).getText());
            log.info("52 week low date: {}", driver.findElement(By.xpath("//*[@id=\"week52LowDate\"]")).getText());
            log.info("upper band: {}", driver.findElement(By.xpath("//*[@id=\"upperbandVal\"]")).getText());
            log.info("lower band: {}", driver.findElement(By.xpath("//*[@id=\"lowerbandVal\"]")).getText());
            log.info("price band: {}", driver.findElement(By.xpath("//*[@id=\"pricebandVal\"]")).getText());

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"Securities_Info_New\"]")));

            log.info("status listed: {}", driver.findElement(By.xpath("//*[@id=\"status\"]/../td[2]")).getText());
            log.info("trading status: {}", driver.findElement(By.xpath("//*[@id=\"TradingStatus\"]/../td[2]")).getText());
            log.info("listing date: {}", driver.findElement(By.xpath("//*[@id=\"Date_of_Listing\"]/../td[2]")).getText());
            log.info("adjusted P/E: {}", driver.findElement(By.xpath("//*[@id=\"SectoralIndxPE\"]/../td[2]")).getText());
            log.info("symbol P/E: {}", driver.findElement(By.xpath("//*[@id=\"Symbol_PE\"]/../td[2]")).getText());
            log.info("sectoral index: {}", driver.findElement(By.xpath("//*[@id=\"Sectoral_Index\"]/../td[2]")).getText());
            log.info("basic industry: {}", driver.findElement(By.xpath("//*[@id=\"BasicIndustry\"]/../../td[2]")).getText());


            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"more_securities_info_table\"]")));

            log.info("board status: {}", driver.findElement(By.xpath("//*[@id=\"BoardStatus\"]/../td[2]")).getText());
            log.info("trading segment: {}", driver.findElement(By.xpath("//*[@id=\"TradingSegment\"]/../td[2]")).getText());
            log.info("class of shares: {}", driver.findElement(By.xpath("//*[@id=\"ClassShares\"]/../td[2]")).getText());


            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"topCorpActionTable\"]")));

            // last value only
            log.info("corp actions: \n{}", driver.findElement(By.xpath("//*[@id=\"topCorpActionTable\"]")).getText());

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"topFinancialResultsTable\"]")));

            // last value only
            log.info("financial results \n{}", driver.findElement(By.xpath("//*[@id=\"topFinancialResultsTable\"]")).getText());

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"tabletopSHP\"]")));

            // last value only
            log.info("shareholding patterns: \n{}", driver.findElement(By.xpath("//*[@id=\"tabletopSHP\"]")).getText());

            log.info("Extracted instrument info successfully");
        } finally {
            driver.quit();
        }

        return refData;
    }

    public StatusMessage reloadStocksData() {

        //todo : all -> go to db and get al instruments otherwise verify presence of instrument

        return new StatusMessage(Status.SUCCESS, "Ref data reloaded successfully");
    }

    private List<Map<String, String>> loadRefData(String expectedFileName) throws CsvValidationException, IOException {
        List<Map<String, String>> refDataList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(expectedFileName))) {
            List<String> headers = new ArrayList<>();
            String[] nextLine = reader.readNext();

            if (nextLine != null) { // header row
                for (String header : nextLine) {
                    String fHeader = header.replace("%", "PER").replaceAll("\\W|(?i)(shares|Crores)", "");
                    headers.add(fHeader.contains("365DPERCHNG") ? "365DPERCHNG" : fHeader);
                }
                // Read and process each line of the CSV file
                while ((nextLine = reader.readNext()) != null) {
                    if (nextLine.length != headers.size()) {
                        throw new ArrayIndexOutOfBoundsException(String.format("Headers: %d and Columns: %d - not matching!", headers.size(), nextLine.length));
                    }
                    Map<String, String> rowMap = new HashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        rowMap.put(headers.get(i), nextLine[i].replace(",", ""));
                    }
                    refDataList.add(rowMap);
                }
            }
        } catch (IOException | CsvValidationException e) {
            log.error("CSV file parsing error: {}", e.getMessage(), e);
            throw e;
        }

        return refDataList;
    }
}
