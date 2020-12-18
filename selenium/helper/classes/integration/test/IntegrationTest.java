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
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.io.IOException;

public class IntegrationTest extends IntegrationSuiteParentBase {

    @Test(description = "Test to see if the upload file functionality works")
    protected void testCreateQrMacro() {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for testCreateQrMacro Test @~~~~~~~~~%s", SUB_HEADING_YELLOW_COLOR, RESET_COLOR), true);
        insertQrMacro();
    }

    @Test(description = "Test to see if the QR Code creates a valid URL in the image", dependsOnMethods = "testCreateQrMacro")
    protected void validateUrl() throws IOException, NotFoundException {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for validateUrl Test @~~~~~~~~~%s", SUB_HEADING_YELLOW_COLOR, RESET_COLOR), true);
        validateQrCodeUrl();
    }

    @Test(description = "Test to create QR macro with left alignment", dependsOnMethods = "testCreateQrMacro")
    protected void qrMacroAlignmentLeft() {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for qrMacroAlignmentLeft Test @~~~~~~~~~%s", SUB_HEADING_YELLOW_COLOR, RESET_COLOR), true);
        qrMacroAlignTest("left");
    }

    @Test(description = "Test to create QR macro with right alignment", dependsOnMethods = "testCreateQrMacro")
    protected void qrMacroAlignmentRight() {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for qrMacroAlignmentRight Test @~~~~~~~~~%s", SUB_HEADING_YELLOW_COLOR, RESET_COLOR), true);
        qrMacroAlignTest("right");
    }

    @Test(description = "Test to see if the Small size functionality works correctly", dependsOnMethods = "testCreateQrMacro")
    protected void qrCodeSizeSmallTest() {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for qrCodeSizeSmallTest Test @~~~~~~~~~%s", SUB_HEADING_YELLOW_COLOR, RESET_COLOR), true);
        qrSizeMacro("small", "100px");
    }

    @Test(description = "Test to see if the Medium size functionality works correctly", dependsOnMethods = "testCreateQrMacro")
    protected void qrCodeSizeMediumTest() {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for qrCodeSizeMediumTest Test @~~~~~~~~~%s", SUB_HEADING_YELLOW_COLOR, RESET_COLOR), true);
        qrSizeMacro("medium", "200px");
    }

    @Test(description = "Test to see if the Large size functionality works correctly", dependsOnMethods = "testCreateQrMacro")
    protected void qrCodeSizeLargeTest() {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for qrCodeSizeLargeTest Test @~~~~~~~~~%s", SUB_HEADING_YELLOW_COLOR, RESET_COLOR), true);
        qrSizeMacro("large", "300px");
    }

    @Test(description = "Test to create QR with a custom URL", dependsOnMethods = "testCreateQrMacro", enabled = false)
    protected void customUrlMacro() throws IOException, NotFoundException {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for customUrlMacro Test @~~~~~~~~~%s", SUB_HEADING_YELLOW_COLOR, RESET_COLOR), true);
        getQrCodeUrl("https://google.com");
    }

    @Test(description = "Test to see if a long URL will break QR Code functionality", dependsOnMethods = "testCreateQrMacro", enabled = false)
    protected void qrLongUrlTest() throws IOException, NotFoundException {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for qrLongUrlTest Test @~~~~~~~~~%s", SUB_HEADING_YELLOW_COLOR, RESET_COLOR), true);
        qrCodeLongUrl("http://llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogoch.co.uk/look_how_long_this_url_is/look_how_long_this_url_is/look_how_long_this_url_is/look_how_long_this_url_is/look_how_long_this_url_is/look_how_long_this_url_is/look_how_long_this_url_is/look_how_long_this_url_is");
    }
}
