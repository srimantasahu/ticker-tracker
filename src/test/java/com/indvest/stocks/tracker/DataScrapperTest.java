package com.indvest.stocks.tracker;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Disabled
public class DataScrapperTest {

    @Test
    void nifty50ExtractionTest() {

        System.out.println("Starting...");

        // defining the options to run Chrome in headless mode
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");

        // initializing a Selenium WebDriver ChromeDriver instance
        // to run Chrome in headless mode
        WebDriver driver = new ChromeDriver(options);

        // connecting to the target web page
        driver.get("https://www.nseindia.com/market-data/live-equity-market?symbol=NIFTY%2050");

        try {
            //Declare and initialise a fluent wait
            FluentWait<WebDriver> wait = new FluentWait<>(driver);
            //Specify the timout of the wait
            wait.withTimeout(Duration.ofMillis(20000));
            //Specify polling time
            wait.pollingEvery(Duration.ofMillis(500));
            //Specify what exceptions to ignore
            wait.ignoring(NoSuchElementException.class);

            wait.until(d -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

            //This is how we specify the condition to wait on.
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"equityStockTable\"]")));

            // retrieving the list of product HTML elements
            WebElement table = driver.findElement(By.xpath("//*[@id=\"equityStockTable\"]"));

            List<WebElement> rowsList = table.findElements(By.tagName("tr"));

            System.out.println("No of rows: " + rowsList.size());

            for (WebElement row : rowsList) {
                List<WebElement> columnsList = row.findElements(By.xpath("td"));

                System.out.println("No of cols: " + columnsList.size());

                for (WebElement column : columnsList) {
                    System.out.println("column text" + column.getText() + ", ");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // ...
            driver.quit();
        }

    }

    @Test
    void nifty50DownloadTest() {
        System.out.println("Starting...");

       String expectedFileName = "/Users/srimantasahu/Downloads/" + "MW-NIFTY-50-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + ".csv";

        // defining the options to run Chrome in headless mode
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");

        Map<String, Object> chromePref = new HashMap<>();

        chromePref.put("download.default_directory", "/Users/srimantasahu/Downloads/");

        options.setExperimentalOption("prefs", chromePref);

        // initializing a Selenium WebDriver ChromeDriver instance
        // to run Chrome in headless mode

        WebDriver driver = new ChromeDriver(options);

        try {
            // connecting to the target web page
            driver.get("https://www.nseindia.com/market-data/live-equity-market?symbol=NIFTY%2050");


            //Declare and initialise a fluent wait
            FluentWait<WebDriver> wait = new FluentWait<>(driver);
            //Specify the timout of the wait
            wait.withTimeout(Duration.ofMillis(30000));
            //Specify polling time
            wait.pollingEvery(Duration.ofMillis(500));
            //Specify what exceptions to ignore
            wait.ignoring(NoSuchElementException.class);

            wait.until(d -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

            //This is how we specify the condition to wait on.
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("dwldcsv")));

            File file = new File(expectedFileName);
            if (file.exists()) {
                System.out.println("Deleted existing file: " + file.delete());
            }

            // retrieving the list of product HTML elements
            WebElement dwldcsv = driver.findElement(By.linkText("Download (.csv)"));

            dwldcsv.click();

            System.out.println("Clicked on download csv");

            Thread.sleep(5000);

            File dwldfile = new File(expectedFileName);

            // Start downloading here.
            wait.until(d -> dwldfile.exists());

            System.out.println("Downloaded csv file exists in path");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // ...
            driver.quit();
        }

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
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("CSV validation error: " + e.getMessage());
        }

        System.out.println(refDataList);

        System.out.println("Inserting data to postgres...");


        String url = "jdbc:postgresql://localhost:5432/nsedata";
        String user = "postgres";
        String password = "password";

        String query = "INSERT INTO stocks.refdata(symbol, ltp, chng, percent_chng, open, high, low, prev_close, volume_sh, value_cr, high_52w, low_52w, percent_chng_30d, percent_chng_365d) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {

            for (Map<String, String> refData : refDataList) {
                pst.setString(1, refData.get("SYMBOL"));
                pst.setDouble(2, Double.parseDouble(refData.get("LTP")));
                pst.setDouble(3, Double.parseDouble(refData.get("CHNG")));
                pst.setDouble(4, Double.parseDouble(refData.get("PERCHNG")));
                pst.setDouble(5, Double.parseDouble(refData.get("OPEN")));
                pst.setDouble(6, Double.parseDouble(refData.get("HIGH")));
                pst.setDouble(7, Double.parseDouble(refData.get("LOW")));
                pst.setDouble(8, Double.parseDouble(refData.get("PREVCLOSE")));
                pst.setDouble(9, Long.parseLong(refData.get("VOLUME")));
                pst.setDouble(10, Double.parseDouble(refData.get("VALUE")));
                pst.setDouble(11, Double.parseDouble(refData.get("52WH")));
                pst.setDouble(12, Double.parseDouble(refData.get("52WL")));
                pst.setDouble(13, Double.parseDouble(refData.get("30DPERCHNG")));
                pst.setDouble(14, Double.parseDouble(refData.get("365DPERCHNG")));
                pst.addBatch();
            }
            pst.executeBatch();

            System.out.println("Batch insertion completed!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Postgres insertion error: " + e.getMessage());
        }

    }

    @Test
    void niftyTotalMarketDownloadTest() {
        System.out.println("Starting...");

        String expectedFileName = "/Users/srimantasahu/Downloads/" + "MW-NIFTY-TOTAL-MARKET-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + ".csv";

        // defining the options to run Chrome in headless mode
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");

        Map<String, Object> chromePref = new HashMap<>();

        chromePref.put("download.default_directory", "/Users/srimantasahu/Downloads/");

        options.setExperimentalOption("prefs", chromePref);

        // initializing a Selenium WebDriver ChromeDriver instance
        // to run Chrome in headless mode

        WebDriver driver = new ChromeDriver(options);

        try {
            // connecting to the target web page
            driver.get("https://www.nseindia.com/market-data/live-equity-market?symbol=NIFTY%20TOTAL%20MARKET");


            //Declare and initialise a fluent wait
            FluentWait<WebDriver> wait = new FluentWait<>(driver);
            //Specify the timout of the wait
            wait.withTimeout(Duration.ofMillis(30000));
            //Specify polling time
            wait.pollingEvery(Duration.ofMillis(500));
            //Specify what exceptions to ignore
            wait.ignoring(NoSuchElementException.class);

            wait.until(d -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

            //This is how we specify the condition to wait on.
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("dwldcsv")));

            File file = new File(expectedFileName);
            if (file.exists()) {
                System.out.println("Deleted existing file: " + file.delete());
            }

            // retrieving the list of product HTML elements
            WebElement dwldcsv = driver.findElement(By.linkText("Download (.csv)"));

            dwldcsv.click();

            System.out.println("Clicked on download csv");

            Thread.sleep(5000);

            File dwldfile = new File(expectedFileName);

            // Start downloading here.
            wait.until(d -> dwldfile.exists());

            System.out.println("Downloaded csv file exists in path");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // ...
            driver.quit();
        }

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
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("CSV validation error: " + e.getMessage());
        }

        System.out.println(refDataList);

        System.out.println("Inserting data to postgres...");


        String url = "jdbc:postgresql://localhost:5432/nsedata";
        String user = "postgres";
        String password = "password";

        String query = "INSERT INTO stocks.refdata(symbol, ltp, chng, percent_chng, open, high, low, prev_close, volume_sh, value_cr, high_52w, low_52w, percent_chng_30d, percent_chng_365d) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {

            for (Map<String, String> refData : refDataList) {
                pst.setString(1, refData.get("SYMBOL"));
                pst.setDouble(2, Double.parseDouble(refData.get("LTP")));
                pst.setDouble(3, NumberUtils.isParsable(refData.get("CHNG")) ? Double.parseDouble(refData.get("CHNG")) : -999D);
                pst.setDouble(4, NumberUtils.isParsable(refData.get("PERCHNG")) ? Double.parseDouble(refData.get("PERCHNG")) : -999D);
                pst.setDouble(5, Double.parseDouble(refData.get("OPEN")));
                pst.setDouble(6, Double.parseDouble(refData.get("HIGH")));
                pst.setDouble(7, Double.parseDouble(refData.get("LOW")));
                pst.setDouble(8, Double.parseDouble(refData.get("PREVCLOSE")));
                pst.setDouble(9, Long.parseLong(refData.get("VOLUME")));
                pst.setDouble(10, Double.parseDouble(refData.get("VALUE")));
                pst.setDouble(11, Double.parseDouble(refData.get("52WH")));
                pst.setDouble(12, Double.parseDouble(refData.get("52WL")));
                pst.setDouble(13, NumberUtils.isParsable(refData.get("30DPERCHNG")) ? Double.parseDouble(refData.get("30DPERCHNG")) : -999D);
                pst.setDouble(14, NumberUtils.isParsable(refData.get("365DPERCHNG")) ? Double.parseDouble(refData.get("365DPERCHNG")) : -999D);
                pst.addBatch();
            }
            pst.executeBatch();

            System.out.println("Batch insertion completed!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Postgres insertion error: " + e.getMessage());
        }

    }

    @Test
    void niftyExtractTickerInfoTest() {

        System.out.println("Starting...");

        // defining the options to run Chrome in headless mode
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");

        // initializing a Selenium WebDriver ChromeDriver instance
        // to run Chrome in headless mode
        WebDriver driver = new ChromeDriver(options);

        // connecting to the target web page
        driver.get("https://www.nseindia.com/get-quotes/equity?symbol=DABUR");

        try {
            //Declare and initialise a fluent wait
            FluentWait<WebDriver> wait = new FluentWait<>(driver);
            //Specify the timout of the wait
            wait.withTimeout(Duration.ofMillis(20000));
            //Specify polling time
            wait.pollingEvery(Duration.ofMillis(500));
            //Specify what exceptions to ignore
            wait.ignoring(NoSuchElementException.class);

            wait.until(d -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"orderbk\"]")));

            Thread.sleep(5000);

            System.out.println("order book buy qty: " + driver.findElement(By.xpath("//*[@id=\"orderBuyTq\"]")).getText());
            System.out.println("order book sell qty: " + driver.findElement(By.xpath("//*[@id=\"orderSellTq\"]")).getText());

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"Trade_Information_pg\"]")));

            System.out.println("order book trade vol in lk: " + driver.findElement(By.xpath("//*[@id=\"orderBookTradeVol\"]")).getText());
            System.out.println("order book trade value in cr: " + driver.findElement(By.xpath("//*[@id=\"orderBookTradeVal\"]")).getText());
            System.out.println("order book total market cap in cr: " + driver.findElement(By.xpath("//*[@id=\"orderBookTradeTMC\"]")).getText());
            System.out.println("order book free float market cap in cr: " + driver.findElement(By.xpath("//*[@id=\"orderBookTradeFFMC\"]")).getText());
            System.out.println("order book impact cost: " + driver.findElement(By.xpath("//*[@id=\"orderBookTradeIC\"]")).getText());
            System.out.println("order book percent traded qty: " + driver.findElement(By.xpath("//*[@id=\"orderBookDeliveryTradedQty\"]")).getText());
            System.out.println("order book applicable margin rate: " + driver.findElement(By.xpath("//*[@id=\"orderBookAppMarRate\"]")).getText());
            System.out.println("order book face value: " + driver.findElement(By.xpath("//*[@id=\"mainFaceValue\"]")).getText());


            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"priceInformationHeading\"]")));

            System.out.println("52 week high: " + driver.findElement(By.xpath("//*[@id=\"week52highVal\"]")).getText());
            System.out.println("52 week high date: " + driver.findElement(By.xpath("//*[@id=\"week52HighDate\"]")).getText());
            System.out.println("52 week low: " + driver.findElement(By.xpath("//*[@id=\"week52lowVal\"]")).getText());
            System.out.println("52 week low date: " + driver.findElement(By.xpath("//*[@id=\"week52LowDate\"]")).getText());
            System.out.println("upper band: " + driver.findElement(By.xpath("//*[@id=\"upperbandVal\"]")).getText());
            System.out.println("lower band: " + driver.findElement(By.xpath("//*[@id=\"lowerbandVal\"]")).getText());
            System.out.println("price band: " + driver.findElement(By.xpath("//*[@id=\"pricebandVal\"]")).getText());

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"Securities_Info_New\"]")));

            System.out.println("status listed: " + driver.findElement(By.xpath("//*[@id=\"status\"]/../td[2]")).getText());
            System.out.println("trading status: " + driver.findElement(By.xpath("//*[@id=\"TradingStatus\"]/../td[2]")).getText());
            System.out.println("listing date: " + driver.findElement(By.xpath("//*[@id=\"Date_of_Listing\"]/../td[2]")).getText());
            System.out.println("adjusted P/E: " + driver.findElement(By.xpath("//*[@id=\"SectoralIndxPE\"]/../td[2]")).getText());
            System.out.println("symbol P/E: " + driver.findElement(By.xpath("//*[@id=\"Symbol_PE\"]/../td[2]")).getText());
            System.out.println("sectoral index: " + driver.findElement(By.xpath("//*[@id=\"Sectoral_Index\"]/../td[2]")).getText());
            System.out.println("basic industry: " + driver.findElement(By.xpath("//*[@id=\"BasicIndustry\"]/../../td[2]")).getText());


            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"more_securities_info_table\"]")));

            System.out.println("board status: " + driver.findElement(By.xpath("//*[@id=\"BoardStatus\"]/../td[2]")).getText());
            System.out.println("trading segment: " + driver.findElement(By.xpath("//*[@id=\"TradingSegment\"]/../td[2]")).getText());
            System.out.println("class of shares: " + driver.findElement(By.xpath("//*[@id=\"ClassShares\"]/../td[2]")).getText());


            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"topCorpActionTable\"]")));

            // last value only
            System.out.println("corp actions: \n" + driver.findElement(By.xpath("//*[@id=\"topCorpActionTable\"]")).getText());

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"topFinancialResultsTable\"]")));

            // last value only
            System.out.println("financial results \n" + driver.findElement(By.xpath("//*[@id=\"topFinancialResultsTable\"]")).getText());

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"tabletopSHP\"]")));

            // last value only
            System.out.println("shareholding patterns: \n" + driver.findElement(By.xpath("//*[@id=\"tabletopSHP\"]")).getText());


            /*// retrieving the list of product HTML elements
            WebElement table = driver.findElement(By.xpath("//*[@id=\"orderBuyTq\"]"));

            List<WebElement> rowsList = table.findElements(By.tagName("tr"));

            System.out.println("No of rows: " + rowsList.size());

            for (WebElement row : rowsList) {
                List<WebElement> columnsList = row.findElements(By.xpath("td"));

                System.out.println("No of cols: " + columnsList.size());

                for (WebElement column : columnsList) {
                    System.out.println("column text" + column.getText() + ", ");
                }
            }*/
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // ...
            driver.quit();
        }

    }

}
