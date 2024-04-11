package com.indvest.stocks.tracker.service;

import com.google.common.net.UrlEscapers;
import com.indvest.stocks.tracker.bean.RefData;
import com.indvest.stocks.tracker.bean.Status;
import com.indvest.stocks.tracker.bean.StatusMessage;
import com.indvest.stocks.tracker.repository.NSERepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.stream.Stream;

import static com.indvest.stocks.tracker.bean.DbStatus.*;
import static com.indvest.stocks.tracker.bean.Status.INVALID;
import static com.indvest.stocks.tracker.bean.Status.SUCCESS;
import static com.indvest.stocks.tracker.util.CommonUtil.*;
import static com.indvest.stocks.tracker.util.SeleniumUtil.*;
import static org.apache.commons.lang3.StringUtils.*;

@Service
public class NSEService {
    private static final Logger log = LoggerFactory.getLogger(NSEService.class);

    @Value("${nse.download.path}")
    private String downloadPath;

    @Value("${nse.download.wait.timeout}")
    private int dwldWaitTimeout;

    @Value("${nse.download.poll.interval}")
    private int dwldPollInterval;

    @Value("${nse.extract.wait.timeout}")
    private int extWaitTimeout;

    @Value("${nse.extract.poll.interval}")
    private int extPollInterval;

    @Autowired
    private NSERepository nseRepository;

    public StatusMessage downloadStocksData(String entity) {
        if (isBlank(entity)) {
            return new StatusMessage(INVALID, "Require a value for entity");
        }

        WebDriver driver = getWebDriver(downloadPath, false);

        try {
            // connecting to the target web page
            driver.get("https://www.nseindia.com/market-data/live-equity-market?symbol=" + UrlEscapers.urlFragmentEscaper().escape(entity));

            FluentWait<WebDriver> wait = new FluentWait<>(driver);
            wait.withTimeout(Duration.ofMillis(dwldWaitTimeout));
            wait.pollingEvery(Duration.ofMillis(dwldPollInterval));
            wait.ignoring(NoSuchElementException.class);

            Thread.sleep(dwldWaitTimeout);

            wait.until(d -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("dwldcsv")));

            final String expectedFileName = downloadPath + getNSEFileName(entity);

            log.info("Checking old file for: {}", expectedFileName);

            File file = new File(expectedFileName);
            if (file.exists()) {
                log.info("Deleted existing file: {}", file.delete());
            }

            WebElement dwldcsv = driver.findElement(By.linkText("Download (.csv)"));

            dwldcsv.click();

            Thread.sleep(dwldWaitTimeout);

            log.info("Clicked on download csv");

            wait.until(d -> new File(expectedFileName).exists());

            log.info("Downloaded csv file exists in path");
        } catch (Exception e) {
            log.error("Error message: {}", e.getMessage(), e);
            return new StatusMessage(Status.ERROR, e.getMessage());
        } finally {
            driver.quit();
        }

        return new StatusMessage(SUCCESS, "File downloaded successfully");
    }

    public StatusMessage storeStocksData(String entity) {
        if (isBlank(entity)) {
            return new StatusMessage(INVALID, "Require a value for entity");
        }

        final String expectedFileName = downloadPath + getNSEFileName(entity);

        try {
            List<Map<String, String>> refDataList = loadRefData(expectedFileName);
            log.info("Extracted ref data list size: {}", refDataList.size());
            nseRepository.save(refDataList);
        } catch (Exception e) {
            log.error("Error message: {}", e.getMessage());
            return new StatusMessage(Status.ERROR, e.getMessage());
        }

        return new StatusMessage(SUCCESS, "Ref data stored successfully");
    }

    public StatusMessage loadStocksData(String symbol) {
        log.info("Loading Ref data for: {}", symbol);
        if (isBlank(symbol)) {
            return new StatusMessage(INVALID, "Require a value for symbol");
        }
        try {
            final WebDriver driver = getWebDriver(downloadPath, false);
            final FluentWait<WebDriver> wait = new FluentWait<>(driver);
            wait.withTimeout(Duration.ofMillis(extWaitTimeout));
            wait.pollingEvery(Duration.ofMillis(extPollInterval));
            wait.ignoring(NoSuchElementException.class);

            try {
                final RefData refData = extractRefData(symbol, driver, wait);
                nseRepository.save(refData);
            } catch (Exception e) {
                log.error("Some error occurred for symbol: {}", symbol, e);
            } finally {
                driver.quit();
            }
        } catch (Exception e) {
            log.error("Error message: {}", e.getMessage());
            return new StatusMessage(Status.ERROR, e.getMessage());
        }

        return new StatusMessage(SUCCESS, "Ref data loaded successfully");
    }

    public StatusMessage refreshStocksData(String status) {
        log.info("Refreshing instrument ref data");
        if (Stream.of(Status.values()).noneMatch(sts -> sts.name().equals(status))) {
            return new StatusMessage(INVALID, "Require a valid status");
        }
        try {
            final List<String> statuses = new ArrayList<>();

            switch (Status.valueOf(status)) {
                case FAILED -> Stream.of(SKIPPED, PARTIAL).map(Enum::toString).forEach(statuses::add);
                case ALL -> Stream.of(SKIPPED, PARTIAL, COMPLETED, UNKNOWN).map(Enum::toString).forEach(statuses::add);
            }

            log.info("Refreshing instrument data for statuses: {}", statuses);

            final List<String> instruments = nseRepository.getInstruments(statuses);

            if (CollectionUtils.isNotEmpty(instruments)) {
                log.info("Refreshing Ref data for: {} instruments", instruments.size());
                final WebDriver driver = getWebDriver(false);
                final FluentWait<WebDriver> wait = new FluentWait<>(driver);
                wait.withTimeout(Duration.ofMillis(extWaitTimeout));
                wait.pollingEvery(Duration.ofMillis(extPollInterval));
                wait.ignoring(NoSuchElementException.class);

                try {
                    for (int i = 0; i < instruments.size(); i++) {
                        try {
                            final RefData refData = extractRefData(instruments.get(i), driver, wait);
                            nseRepository.save(refData);
                            log.info("Saved instruments count: {}", i + 1);
                        } catch (Exception e) {
                            log.error("Some error occurred for symbol: {}", instruments.get(i), e);
                        } finally {
                            driver.manage().deleteAllCookies();
                        }
                    }
                } finally {
                    driver.quit();
                }
            }
        } catch (Exception e) {
            log.error("Error message: {}", e.getMessage());
            return new StatusMessage(Status.ERROR, e.getMessage());
        }

        return new StatusMessage(SUCCESS, "Ref data refreshed successfully");
    }

    private RefData extractRefData(String symbol, WebDriver driver, FluentWait<WebDriver> wait) throws Exception {
        final RefData refData = new RefData(symbol);

        try {
            // connecting to the target web page
            driver.get("https://www.nseindia.com/get-quotes/equity?symbol=" + UrlEscapers.urlFragmentEscaper().escape(symbol));

            waitUntil(wait, By.xpath("//*[@id=\"tabletopSHP\"]/table/tbody"));

            final Predicate<String> isValidText = s -> isNotBlank(s) && !s.trim().equals("-");

            log.info("--------------------------------------------------------------------------------------------------------");

            String text = getText(driver, By.xpath("//*[@id=\"quoteName\"]"));

            log.info("Instrument Name & ISIN: {}", text);
            if (isValidText.test(text))
                refData.setIsin(StringUtils.substringBetween(text, "(", ")"));

            text = getText(driver, By.xpath("//*[@id=\"orderBuyTq\"]"));
            log.info("buy qty: {}", text);
            if (isValidText.test(text))
                refData.setBuyQty(parseLong(text));

            text = getText(driver, By.xpath("//*[@id=\"orderSellTq\"]"));
            log.info("sell qty: {}", text);
            if (isValidText.test(text))
                refData.setSellQty(parseLong(text));

            text = getText(driver, By.xpath("//*[@id=\"orderBookTradeVol\"]"));
            log.info("trade vol in lk: {}", text);
            if (isValidText.test(text))
                refData.setTradeVolInLk(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"orderBookTradeVal\"]"));
            log.info("trade value in cr: {}", text);
            if (isValidText.test(text))
                refData.setTradeValInCr(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"orderBookTradeTMC\"]"));
            log.info("total market cap in cr: {}", text);
            if (isValidText.test(text))
                refData.setTotMarCapInCr(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"orderBookTradeFFMC\"]"));
            log.info("free float market cap in cr: {}", text);
            if (isValidText.test(text))
                refData.setFfMarCapInCr(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"orderBookTradeIC\"]"));
            log.info("impact cost: {}", text);
            if (isValidText.test(text))
                refData.setImpactCost(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"orderBookDeliveryTradedQty\"]"));
            log.info("percent traded qty: {}", text);
            if (isValidText.test(text))
                refData.setPerTradedQty(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"orderBookAppMarRate\"]"));
            log.info("applicable margin rate: {}", text);
            if (isValidText.test(text))
                refData.setAppMarRate(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"mainFaceValue\"]"));
            log.info("face value: {}", text);
            if (isValidText.test(text))
                refData.setFaceValue(parseInt(text));

            text = getText(driver, By.xpath("//*[@id=\"week52highVal\"]"));
            log.info("52 week high: {}", text);
            if (isValidText.test(text))
                refData.setHigh52(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"week52HighDate\"]"));
            log.info("52 week high date: {}", text);
            if (isValidText.test(text))
                refData.setHigh52Dt(parseDate(text));

            text = getText(driver, By.xpath("//*[@id=\"week52lowVal\"]"));
            log.info("52 week low: {}", text);
            if (isValidText.test(text))
                refData.setLow52(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"week52LowDate\"]"));
            log.info("52 week low date: {}", text);
            if (isValidText.test(text))
                refData.setLow52Dt(parseDate(text));

            text = getText(driver, By.xpath("//*[@id=\"upperbandVal\"]"));
            log.info("upper band: {}", text);
            if (isValidText.test(text))
                refData.setUpperBand(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"lowerbandVal\"]"));
            log.info("lower band: {}", text);
            if (isValidText.test(text))
                refData.setLowerBand(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"pricebandVal\"]"));
            log.info("price band: {}", text);
            if (isValidText.test(text))
                refData.setPriceBand(text);

            text = getText(driver, By.xpath("//*[@id=\"status\"]/../td[2]"));
            log.info("status listed: {}", text);
            if (isValidText.test(text))
                refData.setListedStatus(text);

            text = getText(driver, By.xpath("//*[@id=\"TradingStatus\"]/../td[2]"));
            log.info("trading status: {}", text);
            if (isValidText.test(text))
                refData.setTradingStatus(text);

            text = getText(driver, By.xpath("//*[@id=\"Date_of_Listing\"]/../td[2]"));
            log.info("listing date: {}", text);
            if (isValidText.test(text))
                refData.setListedDt(parseDate(text));

            text = getText(driver, By.xpath("//*[@id=\"SectoralIndxPE\"]/../td[2]"));
            log.info("adjusted P/E: {}", text);
            if (isValidText.test(text))
                refData.setAdjustedPE(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"Symbol_PE\"]/../td[2]"));
            log.info("symbol P/E: {}", text);
            if (isValidText.test(text))
                refData.setSymbolPE(parseDouble(text));

            text = getText(driver, By.xpath("//*[@id=\"Sectoral_Index\"]/../td[2]"));
            log.info("sectoral index: {}", text);
            if (isValidText.test(text))
                refData.setSectoralIndex(text);

            text = getText(driver, By.xpath("//*[@id=\"BasicIndustry\"]/../../td[2]"));
            log.info("basic industry: {}", text);
            if (isValidText.test(text))
                refData.setBasicIndustry(text);

            text = getText(driver, By.xpath("//*[@id=\"BoardStatus\"]/../td[2]"));
            log.info("board status: {}", text);
            if (isValidText.test(text))
                refData.setBoardStatus(text);

            text = getText(driver, By.xpath("//*[@id=\"TradingSegment\"]/../td[2]"));
            log.info("trading segment: {}", text);
            if (isValidText.test(text))
                refData.setTradingSegment(text);

            text = getText(driver, By.xpath("//*[@id=\"ClassShares\"]/../td[2]"));
            log.info("class of shares: {}", text);
            if (isValidText.test(text))
                refData.setSharesClass(text);

            text = getText(driver, By.xpath("//*[@id=\"topCorpActionTable\"]"));
            log.info("corp actions: \n{}", text);
            if (isValidText.test(text))
                refData.setCorpActions(text.split("\n"));

            text = getText(driver, By.xpath("//*[@id=\"topFinancialResultsTable\"]"));
            log.info("financial results \n{}", text);
            if (isValidText.test(text))
                refData.setFinancialResults(text.split("\n"));

            text = getText(driver, By.xpath("//*[@id=\"tabletopSHP\"]"));
            log.info("shareholding patterns: \n{}", text);
            if (isValidText.test(text))
                refData.setShareholdingPatterns(text.split("\n"));

            log.info("Extracted instrument info successfully");
        } catch (Exception e) {
            log.error("Error while extracting instrument info: {}", e.getMessage(), e);
            throw e;
        }

        return refData;
    }

    private List<Map<String, String>> loadRefData(String expectedFileName) throws CsvValidationException, IOException {
        List<Map<String, String>> refDataList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(expectedFileName))) {
            List<String> headers = new ArrayList<>();
            String[] nextLine = reader.readNext();

            if (nextLine != null) { // header row
                for (String header : nextLine) {
                    String fHeader = header.replace("%", "PER").replaceAll("\\W|(?i)(shares|Crores)", EMPTY);
                    headers.add(fHeader.contains("365DPERCHNG") ? "365DPERCHNG" : fHeader);
                }
                // Read and process each line of the CSV file
                while ((nextLine = reader.readNext()) != null) {
                    if (nextLine.length != headers.size()) {
                        throw new ArrayIndexOutOfBoundsException(String.format("Headers: %d and Columns: %d - not matching!", headers.size(), nextLine.length));
                    }
                    Map<String, String> rowMap = new HashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        rowMap.put(headers.get(i), nextLine[i].replace(",", EMPTY));
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
