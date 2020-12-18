/*
 * Copyright (c) 2020. TechTime Initiative Group Limited. All rights reserved.
 *
 *  The contents of this file have been approved for use by the author as a representative sample of the results
 *  of their work performed while employed by TechTime Initiative Group Limited.
 *  
 *  For all questions, please contact support@techtime.co.nz
 */

package it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.smoke.suite;

import org.testng.Reporter;
import org.testng.annotations.Test;

public class SmokeTest extends SmokeSuiteParentBase {

    @Test(priority = 7, description = "Test verifies that if App is successfully installed or not")
    private void appInstallationTest() {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for appInstallationTest @~~~~~~~~~%s", SUB_HEADING_YELLOW_COLOR, RESET_COLOR), true);
        verifyInstall(APP_KEY);
    }

    @Test(priority = 7, dependsOnMethods = {"appInstallationTest"}, description = "Test verifies if app is enabled or not")
    private void appEnableTest() {
        Reporter.log(String.format("%s~~~~~~~~~@ Printing logs for appEnableTest @~~~~~~~~~%s", SUB_HEADING_YELLOW_COLOR, RESET_COLOR), true);
        verifyAppStatus(APP_KEY);
    }

}
