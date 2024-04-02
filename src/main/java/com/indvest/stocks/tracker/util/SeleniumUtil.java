package com.indvest.stocks.tracker.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

public final class SeleniumUtil {

    private SeleniumUtil() {
    }

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

}
