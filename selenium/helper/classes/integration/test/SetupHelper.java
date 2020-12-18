/*
 * Copyright (c) 2020. TechTime Initiative Group Limited. All rights reserved.
 *
 *  The contents of this file have been approved for use by the author as a representative sample of the results
 *  of their work performed while employed by TechTime Initiative Group Limited.
 *
 *  For all questions, please contact support@techtime.co.nz
 */

package it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.integration.test;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.parent.suite.utils.CheckElementVisibility;
import org.apache.commons.validator.routines.UrlValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Reporter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

public class SetupHelper {

    public static void navigateToEasyQrLink(WebDriver driver, String pageUrl, String pageName, String editPage, int longWait, int pollingWait) {
        Wait<WebDriver> waitForLoading = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(longWait)).pollingEvery(Duration.ofSeconds(pollingWait)).ignoring(ElementNotVisibleException.class, ElementClickInterceptedException.class);
        String otherMacro = "[class*='dropdown-item content-macro']";
        driver.navigate().to(pageUrl);
        Reporter.log(String.format("Navigating to %s where the pageUrl is %s", pageName, pageUrl), true);
        waitForLoading.until(ExpectedConditions.elementToBeClickable(By.id(editPage))).click();
        Reporter.log("Click the edit button", true);
        waitForLoading.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#rte-button-insert"))).click();
        Reporter.log("Click the 'Insert More Content' button", true);
        driver.findElement(By.cssSelector(otherMacro)).click();
        Reporter.log("Click on the EasyQR Macro to Insert the QR code", true);
        driver.findElement(By.cssSelector("#macro-easyqrlink")).click();
        Reporter.log("Click the Easy QR Macro from the macro menu", true);
    }

    public static void qrCustomUrlMacro(WebDriver driver, String url, String insertButton, String editPage, int longWait, int pollingWait) {
        String macroCustomUrl = "macro-param-target";
        String customUrlBox = "macro-param-url";
        driver.findElement(By.id(macroCustomUrl)).click();
        Reporter.log(String.format("Click on the drop-down box to select which Url will be used where Id is: %s", macroCustomUrl), true);
        driver.findElement(By.cssSelector(String.format("#%s [value='custom URL']", macroCustomUrl))).click();
        Reporter.log(String.format("Click on the customUrl field where cssSelector is #%s [value='custom URL']", macroCustomUrl), true);
        driver.findElement(By.id(customUrlBox)).sendKeys(url);
        Reporter.log(String.format("Enter values into the customUrlBox where id is %s and the url is %s", customUrlBox, url), true);
        driver.findElement(By.cssSelector(insertButton)).click();
        Reporter.log(String.format("Click the Insert Button where cssSelector is %s", insertButton), true);
        SetupHelper.publishPage(driver, editPage, longWait, pollingWait);
    }

    public static String getDecodedUrl(WebDriver driver, String qrImage) throws IOException, NotFoundException {
        Reporter.log("Retrieving attributes from the QR Code", true);
        String src = driver.findElement(By.cssSelector(String.format("%s img", qrImage))).getAttribute("src");
        //Format the src URL so it is a valid Base64 String so it can be decoded into a URL
        String formatUrl = src.substring(src.indexOf(",") + 1);
        return decodeQrCode(formatUrl);
    }

    public static String decodeQrCode(Object qrCodeImage) throws IOException, NotFoundException {
        BufferedImage bufferedImage;
        if (((String) qrCodeImage).contains("http")) {
            bufferedImage = ImageIO.read((new URL((String) qrCodeImage)));
        } else {
            byte[] decoded = Base64.getDecoder().decode((String) qrCodeImage);
            bufferedImage = ImageIO.read(new ByteArrayInputStream(decoded));
        }
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        Reporter.log(String.format("The decoded URL is: %s", result.getText()), true);
        return result.getText();
    }

    public static void publishPage(WebDriver driver, String editPage, int longWait, int pollingWait) {
        Wait<WebDriver> waitForLoading = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(longWait)).pollingEvery(Duration.ofSeconds(pollingWait)).ignoring(ElementNotVisibleException.class, ElementClickInterceptedException.class);
        waitForLoading.until(ExpectedConditions.elementToBeClickable(By.id("rte-button-publish"))).click();
        Reporter.log("Publish/Update the changes for the page", true);
        waitForLoading.until(ExpectedConditions.visibilityOfElementLocated(By.id(editPage)));
        if (CheckElementVisibility.elementPresentById(driver, editPage)) {
            Reporter.log("Page published/updated successfully", true);
        } else {
            throw new IllegalStateException("Failed to click publish button. Please change the publish code and try again");
        }
    }

    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public static boolean validateUrl(String url) {
        UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
        return urlValidator.isValid(url);
    }

}
