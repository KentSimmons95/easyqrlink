/*
 * Copyright (c) 2020. TechTime Initiative Group Limited. All rights reserved.
 *
 *  The contents of this file have been approved for use by the author as a representative sample of the results
 *  of their work performed while employed by TechTime Initiative Group Limited.
 *  
 *  For all questions, please contact support@techtime.co.nz
 */

package it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.smoke.suite;

import it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.parent.suite.utils.CheckElementVisibility;
import it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.parent.suite.utils.ParentSuiteBase;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.util.Strings;

import java.time.Duration;

public class SmokeSuiteParentBase extends ParentSuiteBase {

    @BeforeClass(alwaysRun = true)
    protected void navigateToUpmAndInitialiseAppKey() {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for SmokeSuiteParentBase BeforeSuite method @~~~~~~~~~%s", HEADING_CYAN_COLOR, RESET_COLOR), true);
        if (Strings.isNotNullAndNotEmpty(baseApplicationUrl)) {
            String manageAllAppsUrl = String.format("%s/plugins/servlet/upm/manage/all", baseApplicationUrl);
            Reporter.log(String.format("Navigate to Manage All Apps Page where navigation url is: %s", manageAllAppsUrl), true);
            driver.navigate().to(manageAllAppsUrl);
            WebDriverWait waitForLoading = new WebDriverWait(driver, LONG_WAIT);
            Reporter.log("Wait for Manage Apps panel to load", true);
            waitForLoading.until(ExpectedConditions.visibilityOfElementLocated(By.id("upm-manage-plugins-user-installed"))); // Manage Apps panel
            Reporter.log("Wait for Upload Apps link to appear", true);
            waitForLoading.until(ExpectedConditions.visibilityOfElementLocated(By.id("upm-manage-plugins-system"))); //Upload App link
        } else {
            Reporter.log(String.format("Failed to navigate to UPM where baseApplicationUrl is %s", baseApplicationUrl), true);
        }
    }

    protected void verifyInstall(String appKey) {
        boolean appInstalledSuccessfully = false;
        String pluginList = "//div[@class='upm-manage-plugin-list']";
        Reporter.log("Check if any apps are installed or not", true);
        boolean anyAppsInstalled = CheckElementVisibility.elementPresent(driver, pluginList);
        if (anyAppsInstalled) {
            String appPanel = String.format("//div[@data-key='%s']", appKey); // This panel hold all details of the app
            Reporter.log(String.format("Check if app panel with app key(%s) exist or not where app panel xpath is %s", appKey, appPanel), true);
            appInstalledSuccessfully = CheckElementVisibility.elementPresent(driver, appPanel);
        }
        Reporter.log(String.format("%s%s installed successfully = %s where App key is %s%s", SUB_HEADING_YELLOW_COLOR, getAppName(appKey), appInstalledSuccessfully, appKey, RESET_COLOR), true);

        Assert.assertTrue(anyAppsInstalled && appInstalledSuccessfully);
    }

    protected void verifyAppStatus(String appKey) {
        boolean appEnabled = false;
        String appName = getAppName(appKey);
        String expectedButtonActionStatus = "disable";
        Reporter.log("Check if app settings are visible", true);
        expandAppSettings(appKey, appName);
        int totalNumberOfButtons = driver.findElements(By.xpath(String.format("//div[@data-key='%s']//a[@data-action]", appKey))).size();

        // Select right button to get 'data-action' because enabling plugin will increase the number of buttons which will give you wrong 'action-data'
        String enableDisableButtonXpath = String.format("(//div[@data-key='%s']//a[@data-action])[%s]", appKey, totalNumberOfButtons);
        Reporter.log(String.format("Get data-action status from Enable/Disable button where button xpath is %s", enableDisableButtonXpath), true);
        String actualButtonActionStatus = driver.findElement(By.xpath(enableDisableButtonXpath)).getAttribute("data-action");
        Reporter.log("Get total number of modules from UI", true);
        String appModules = driver.findElement(By.xpath("//span[contains(@class,'upm-count-enabled')]")).getText(); // returns you the whole String of Module message
        int actualNumberOfModulesEnabled = Integer.parseInt(appModules.split(" ")[0]); // returns you the specific number of enabled modules
        Reporter.log(String.format("Check if actual data-action status is same as expected where actual status is %s and expected is %s", actualButtonActionStatus, expectedButtonActionStatus), true);
        boolean enableDisableButtonStatus = StringUtils.containsIgnoreCase(actualButtonActionStatus, expectedButtonActionStatus);
        Reporter.log(String.format("Check if total number of enabled modules are greater than 0 where actual number of enabled modules are %s", actualNumberOfModulesEnabled), true);
        boolean modulesEnabled = actualNumberOfModulesEnabled > 0;
        if (enableDisableButtonStatus && modulesEnabled) {
            appEnabled = true;
        }
        Reporter.log(String.format("%s%s enabled = %s where App key is %s%s", SUB_HEADING_YELLOW_COLOR, appName, appEnabled, appKey, RESET_COLOR), true);

        Assert.assertTrue(appEnabled);
    }

    private void expandAppSettings(String appKey, String appName) {
        Wait<WebDriver> waitForAppSettingsToLoad = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(LONG_WAIT)).pollingEvery(Duration.ofSeconds(POLLING_WAIT));
        String appDetails = String.format("//div[@data-key='%s']//div[@class='upm-details loaded']", appKey);
        if (!CheckElementVisibility.elementPresent(driver, appDetails)) {
            Reporter.log(String.format("Click on %s to expand settings where expander xpath is: %s", appName, appDetails), true);
            driver.findElement(By.xpath(String.format("//div[@data-key='%s']", appKey))).click(); // expand app settings
            WebElement expandSettings = driver.findElement(By.xpath(appDetails));
            Reporter.log("Wait for app settings to load", true);
            waitForAppSettingsToLoad.until(ExpectedConditions.visibilityOf(expandSettings));
        } else {
            Reporter.log(String.format("%s App settings already visible", appName), true);
        }
    }

    private String getAppName(String appKey) {
        String appPanel = String.format("[data-key='%s']", appKey); // This panel hold all details of the app
        Reporter.log(String.format("Get app name where appPanel css selector is %s", appPanel), true);
        String appName = driver.findElement(By.cssSelector(String.format("%s .upm-plugin-name", appPanel))).getText();
        Reporter.log(String.format("App name is %s", appName), true);
        return appName;
    }
}
