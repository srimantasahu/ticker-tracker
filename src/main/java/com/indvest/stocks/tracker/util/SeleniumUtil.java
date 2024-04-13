package com.indvest.stocks.tracker.util;

import com.indvest.stocks.tracker.bean.WebDriverAndWait;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class SeleniumUtil {
    public static final Predicate<String> isValidText = s -> isNotBlank(s) && !s.trim().equals("-");
    private static final Logger log = LoggerFactory.getLogger(SeleniumUtil.class);

    private SeleniumUtil() {
    }

    public static WebDriverAndWait getWebDriverAndWait(String downloadPath, boolean headless, int waitTimeout, int pollInterval) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        options.setExperimentalOption("prefs", Collections.singletonMap("download.default_directory", downloadPath));

        WebDriver driver = new ChromeDriver(options);
        FluentWait<WebDriver> wait = new FluentWait<>(driver);
        wait.withTimeout(Duration.ofMillis(waitTimeout));
        wait.pollingEvery(Duration.ofMillis(pollInterval));
        wait.ignoring(NoSuchElementException.class);

        return new WebDriverAndWait(driver, wait);
    }

    public static WebDriverAndWait getWebDriverAndWait(boolean headless, int waitTimeout, int pollInterval) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless");
        }

        WebDriver driver = new ChromeDriver(options);
        FluentWait<WebDriver> wait = new FluentWait<>(driver);
        wait.withTimeout(Duration.ofMillis(waitTimeout));
        wait.pollingEvery(Duration.ofMillis(pollInterval));
        wait.ignoring(NoSuchElementException.class);

        return new WebDriverAndWait(driver, wait);
    }

    public static void passivateWebDriver(WebDriverAndWait driverAndWait) {
        driverAndWait.driver().manage().deleteAllCookies();
    }

    public static void destroyWebDriver(WebDriverAndWait driverAndWait) {
        driverAndWait.driver().quit();
    }

    public static void waitUntil(FluentWait<WebDriver> wait, By by) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Exception e) {
            log.warn("Error waiting for element: {}", by);
        }
    }

    public static String getText(WebDriver driver, By by) {
        try {
            return driver.findElement(by).getText();
        } catch (Exception e) {
            log.warn("Couldn't find element for : {}", by.toString());
            return null;
        }
    }

}
