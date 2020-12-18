/*
 * Copyright (c) 2020. TechTime Initiative Group Limited. All rights reserved.
 *
 *  The contents of this file have been approved for use by the author as a representative sample of the results
 *  of their work performed while employed by TechTime Initiative Group Limited.
 *
 *  For all questions, please contact support@techtime.co.nz
 */

package it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.integration.test;

import com.google.zxing.NotFoundException;
import it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.parent.suite.utils.CheckElementVisibility;
import it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.parent.suite.utils.ParentSuiteBase;
import it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.parent.suite.utils.RestHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;

public class IntegrationSuiteParentBase extends ParentSuiteBase {

    private final String PAGE_NAME = "TestPage";
    private final String EDIT_PAGE = "editPageLink";
    private final String QR_IMAGE = ".conf-macro.output-block";
    private final String INSERT_BUTTON = ".button-panel-button.ok";
    private final String SPACE_KEY = "ds";
    private final String PAGE_URL = String.format("%s/display/%s/%s", baseApplicationUrl, SPACE_KEY, PAGE_NAME);

    @BeforeMethod(alwaysRun = true)
    protected void initialiseTestPage() throws IOException {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for IntegrationSuiteParentBase BeforeMethod method @~~~~~~~~~%s", HEADING_CYAN_COLOR, RESET_COLOR), true);
        if (RestHelper.pageExists(baseApplicationUrl, baseApplicationUsername, baseApplicationPassword, SPACE_KEY, PAGE_NAME)) {
            RestHelper.deletePage(driver, baseApplicationUrl, baseApplicationUsername, baseApplicationPassword, PAGE_NAME, SPACE_KEY);
        } else {
            Reporter.log(String.format("No page found where page name is: %s, creating the new test page", PAGE_NAME), true);
        }
        RestHelper.createPage(baseApplicationUrl, baseApplicationUsername, baseApplicationPassword, PAGE_NAME, SPACE_KEY);
        SetupHelper.navigateToEasyQrLink(driver, PAGE_URL, PAGE_NAME, EDIT_PAGE, LONG_WAIT, POLLING_WAIT);
    }

    @AfterMethod(alwaysRun = true)
    protected void cleanUpPage() throws IOException {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for IntegrationSuiteParentBase AfterMethod method @~~~~~~~~~%s", HEADING_CYAN_COLOR, RESET_COLOR), true);
        RestHelper.deletePage(driver, baseApplicationUrl, baseApplicationUsername, baseApplicationPassword, PAGE_NAME, SPACE_KEY);
    }

    public void insertQrMacro() {
        WebDriverWait webDriverWait = new WebDriverWait(driver, SHORT_WAIT);
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(INSERT_BUTTON))).click();
        Reporter.log(String.format("Clicked on the Insert Button where cssSelector is %s", INSERT_BUTTON), true);
        SetupHelper.publishPage(driver, EDIT_PAGE, LONG_WAIT, POLLING_WAIT);
        verifyComponentVisibility(QR_IMAGE);
    }

    public void qrCodeLongUrl(String longUrl) throws IOException, NotFoundException {
        getQrCodeUrl(longUrl);
    }

    public void validateQrCodeUrl() throws IOException, NotFoundException {
        insertQrMacro();
        String decodedUrl = SetupHelper.getDecodedUrl(driver, QR_IMAGE);
        Assert.assertTrue(SetupHelper.validateUrl(decodedUrl), String.format("The URL that's in the QR Code isn't a valid URL where the URL is = %s", decodedUrl));
    }

    public void qrMacroAlignTest(String alignment) {
        String macroAlignment = "macro-param-alignment";
        driver.findElement(By.id(macroAlignment)).click();
        Reporter.log(String.format("Click on the Alignment dropdown menu with the Id of: %s", macroAlignment), true);
        driver.findElement(By.cssSelector(String.format("#%s [value='%s']", macroAlignment, alignment))).click();
        Reporter.log(String.format("Click on the alignment value where value is equal to: %s", alignment), true);
        driver.findElement(By.cssSelector(INSERT_BUTTON)).click();
        Reporter.log(String.format("Click on the Insert Button where the cssSelector is: %s", INSERT_BUTTON), true);
        SetupHelper.publishPage(driver, EDIT_PAGE, LONG_WAIT, POLLING_WAIT);
        String qrAlignment = driver.findElement(By.cssSelector(QR_IMAGE)).getCssValue("float");
        Assert.assertEquals(alignment.toLowerCase(), qrAlignment.toLowerCase(), "The QR Macro alignment on the page does not match the alignment parameter given");
    }

    public void qrSizeMacro(String size, String pixels) {
        String macroSize = "macro-param-size";
        driver.findElement(By.id(macroSize)).click();
        Reporter.log(String.format("Click on the Size dropdown menu with the Id of: %s", macroSize), true);
        driver.findElement(By.cssSelector(String.format("#%s [value='%s']", macroSize, size))).click();
        Reporter.log(String.format("Click on the Size value where value is equal to: %s", size), true);
        driver.findElement(By.cssSelector(INSERT_BUTTON)).click();
        Reporter.log(String.format("Click on the Insert Button where the cssSelector is: %s", INSERT_BUTTON), true);
        SetupHelper.publishPage(driver, EDIT_PAGE, LONG_WAIT, POLLING_WAIT);
        String qrCodeSize = driver.findElement(By.cssSelector(QR_IMAGE)).getCssValue("width");
        Reporter.log(String.format("The value of the width of the QR Code on the page is: %s", qrCodeSize), true);
        Assert.assertEquals(qrCodeSize, pixels, "The size of QR code does not match the given pixel size parameter!");
    }

    protected void getQrCodeUrl(String customUrl) throws IOException, NotFoundException {
        SetupHelper.qrCustomUrlMacro(driver, customUrl, INSERT_BUTTON, EDIT_PAGE, LONG_WAIT, POLLING_WAIT);
        String decodedUrl = SetupHelper.getDecodedUrl(driver, QR_IMAGE);
        Assert.assertEquals(customUrl, decodedUrl, "The URL given does not match the decoded URL from the QR Code Image");
        Reporter.log(String.format("The given Url is: %s and the decodedUrl is: %s", customUrl, decodedUrl), true);
        Assert.assertTrue(SetupHelper.validateUrl(decodedUrl), String.format("The URL that's in the QR Code isn't a valid URL where the URL is = %s", decodedUrl));
    }

    protected void verifyComponentVisibility(String cssSelector) {
        Reporter.log(String.format("Check if element is present or not  where element cssSelector is: %s", cssSelector), true);
        boolean componentVisible = CheckElementVisibility.elementPresentByCssSelector(driver, cssSelector);

        Assert.assertTrue(componentVisible, "Test failed: Component not visible. Please review!");
    }
}
