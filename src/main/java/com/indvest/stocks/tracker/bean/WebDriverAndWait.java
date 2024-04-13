package com.indvest.stocks.tracker.bean;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

public record WebDriverAndWait(WebDriver driver, FluentWait<WebDriver> fluentWait) {
}