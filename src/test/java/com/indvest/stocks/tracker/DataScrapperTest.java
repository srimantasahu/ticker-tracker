package com.indvest.stocks.tracker;

import com.opencsv.CSVReader;
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
    void nifty50Test() {

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

        String query = "INSERT INTO stocks.refdata(symbol, ltp, chng, percent_chng, open, high, low, prev_close, volume, value, high_52w, low_52w, percent_chng_30d, percent_chng_365d) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

}
