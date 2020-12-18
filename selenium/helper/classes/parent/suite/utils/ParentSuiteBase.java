/*
 * Copyright (c) 2020. TechTime Initiative Group Limited. All rights reserved.
 *
 *  The contents of this file have been approved for use by the author as a representative sample of the results
 *  of their work performed while employed by TechTime Initiative Group Limited.
 *
 *  For all questions, please contact support@techtime.co.nz
 */

package it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.parent.suite.utils;

import org.openqa.selenium.WebDriver;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class ParentSuiteBase {

    protected WebDriver driver;
    protected String baseApplicationUrl;
    protected String browser;
    protected int defaultTimeOut;
    protected boolean headless;
    protected String baseApplicationUsername;
    protected String baseApplicationPassword;
    protected int POLLING_WAIT = 1;
    protected int LONG_WAIT = 10;
    protected int SHORT_WAIT = 5;
    protected static final String RESET_COLOR = "\033[0m";
    protected static final String HEADING_CYAN_COLOR = "\033[0;96m";
    protected static final String SUB_HEADING_YELLOW_COLOR = "\u001B[33m";
    //    protected final String APP_NAME = "EasyQRLink";
    protected final String APP_KEY = "org.techtime.plugins.confluence.easyqrlink";
    private final String BROWSER = "browser";
    private final String BROWSER_VALUE = "chrome";
    private final String BASE_APPLICATION_URL = "baseApplicationUrl";
    public final String BASE_APPLICATION_VALUE = "http://localhost:1990/confluence";
    private final String DEFAULT_TIMEOUT = "defaultTimeout";
    private final String DEFAULT_TIMEOUT_VALUE = "30";
    private final String USERNAME = "username";
    private final String USERNAME_VALUE = "admin";
    private final String PASSWORD = "password";
    private final String PASSWORD_VALUE = "admin";
    private final String SYNC_LOGIN = "syncLogin";
    private final String SYNC_LOGIN_VALUE = "//a[@aria-haspopup='true' and contains(@title,'" + USERNAME_VALUE + "')]";
    private final String LICENSE_KEY = "licenseKey";
    private final String LICENSE_KEY_VALUE = "AAABCA0ODAoPeNpdj01PwkAURffzKyZxZ1IyUzARkllQ24gRaQMtGnaP8VEmtjPNfFT59yJVFyzfu\n" +
            "bkn796Ux0Bz6SmbUM5nbDzj97RISxozHpMUnbSq88poUaLztFEStUN6MJZ2TaiVpu/YY2M6tI6sQ\n" +
            "rtHmx8qd74EZ+TBIvyUU/AoYs7jiE0jzknWQxMuifA2IBlUbnQ7AulVjwN9AaU9atASs69O2dNFU\n" +
            "4wXJLc1aOUGw9w34JwCTTZoe7RPqUgep2X0Vm0n0fNut4gSxl/Jcnj9nFb6Q5tP/Ueu3L+0PHW4g\n" +
            "hZFmm2zZV5k6/95CbR7Y9bYGo/zGrV3Ir4jRbDyCA6vt34DO8p3SDAsAhQnJjLD5k9Fr3uaIzkXK\n" +
            "f83o5vDdQIUe4XequNCC3D+9ht9ZYhNZFKmnhc=X02dh";
    private final String HEADLESS = "headless";
    private final String HEADLESS_VALUE = "false";

    @BeforeSuite(alwaysRun = true)
    @Parameters({BROWSER, BASE_APPLICATION_URL, DEFAULT_TIMEOUT, USERNAME, PASSWORD, SYNC_LOGIN, LICENSE_KEY, HEADLESS})
    protected void loginAndApplyLicense(@Optional(BROWSER_VALUE) String browser, @Optional(BASE_APPLICATION_VALUE) String baseApplicationUrl,
                                        @Optional(DEFAULT_TIMEOUT_VALUE) int defaultTimeout, @Optional(USERNAME_VALUE) String username,
                                        @Optional(PASSWORD_VALUE) String password, @Optional(SYNC_LOGIN_VALUE) String syncLogin,
                                        @Optional(LICENSE_KEY_VALUE) String pluginLicense, @Optional(HEADLESS_VALUE) boolean headless) {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for ParentSuiteBase BeforeSuite method @~~~~~~~~~%s", HEADING_CYAN_COLOR, RESET_COLOR), true);
        this.baseApplicationUsername = username;
        Reporter.log(String.format("Initialise baseApplicationUsername where baseApplicationUsername = %s", this.baseApplicationUsername), true);
        Reporter.log("Initialise baseApplicationPassword", true);
        this.baseApplicationPassword = password;
        Reporter.log("Initialise WebDriver", true);
        this.driver = WebDriverHelper.invokeBrowserAndReturnDriver(browser, defaultTimeout, headless);
        this.baseApplicationUrl = baseApplicationUrl.toLowerCase();
        Reporter.log(String.format("Initialise baseApplicationUrl where baseApplicationUrl = %s", this.baseApplicationUrl), true);
        WebDriverHelper.login(driver, this.baseApplicationUrl, this.baseApplicationUsername, this.baseApplicationPassword, syncLogin);
        this.browser = browser;
        Reporter.log(String.format("Initialise browser where browser = %s", this.browser), true);
        this.defaultTimeOut = defaultTimeout;
        Reporter.log(String.format("Initialise defaultTimeOut where defaultTimeOut = %s", this.defaultTimeOut), true);
        this.headless = headless;
        Reporter.log(String.format("Initialise headless where headless = %s", this.headless), true);
    }

    @AfterSuite(alwaysRun = true)
    protected void quitDriver() {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for ParentSuiteBase AfterSuite method @~~~~~~~~~%s", HEADING_CYAN_COLOR, RESET_COLOR), true);
        WebDriverHelper.closeBrowser(driver);
    }
}
