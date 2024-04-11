package com.indvest.stocks.tracker.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class SeleniumUtil {
    private static final Logger log = LoggerFactory.getLogger(SeleniumUtil.class);

    private SeleniumUtil() {
    }

    public static final Predicate<String> isValidText = s -> isNotBlank(s) && !s.trim().equals("-");

    public static WebDriver getWebDriver(String downloadPath, boolean headless) {
        Map<String, Object> chromePref = new HashMap<>();
        chromePref.put("download.default_directory", downloadPath);

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        options.setExperimentalOption("prefs", chromePref);

        return new ChromeDriver(options);
    }

    public static WebDriver getWebDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless");
        }

        return new ChromeDriver(options);
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
