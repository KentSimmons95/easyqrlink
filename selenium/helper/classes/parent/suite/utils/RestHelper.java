/*
 * Copyright (c) 2020. TechTime Initiative Group Limited. All rights reserved.
 *
 *  The contents of this file have been approved for use by the author as a representative sample of the results
 *  of their work performed while employed by TechTime Initiative Group Limited.
 *
 *  For all questions, please contact support@techtime.co.nz
 */

package it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.parent.suite.utils;

import com.atlassian.json.jsonorg.JSONArray;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONObject;

import it.org.techtime.confluence.plugins.easyqrlink.selenium.helper.classes.integration.test.SetupHelper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import javax.json.Json;
import javax.json.JsonObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;

public class RestHelper {

    private final static CloseableHttpClient CLIENT = HttpClients.createDefault();

    public static void createPage(String baseApplicationUrl, String baseApplicationUsername, String baseApplicationPassword, String pageName, String spaceKey) throws IOException {
        String requestUrl = String.format("%s/rest/api/content/", baseApplicationUrl);
        HttpPost httpPost = new HttpPost(requestUrl);
        String credentials = Base64.getEncoder().encodeToString(String.format("%s:%s", baseApplicationUsername, baseApplicationPassword).getBytes());
        JsonObject json = Json.createObjectBuilder()
                .add("type", "page")
                .add("title", pageName)
                .add("space", Json.createObjectBuilder()
                        .add("key", spaceKey))
                .add("body", Json.createObjectBuilder()
                        .add("storage", Json.createObjectBuilder()
                                .add("value", "Generic Page Text")
                                .add("representation", "storage")))
                .build();
        StringEntity entity = new StringEntity(json.toString());
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        try (CloseableHttpResponse response = CLIENT.execute(httpPost)) {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Reporter.log(String.format("The %s page has successfully been created ", pageName), true);
            } else {
                String responseBody = EntityUtils.toString(response.getEntity());
                Reporter.log(String.format("The %s page could not be created, the response is: %s", pageName, responseBody), true);
                throw new IllegalStateException(String.format("Unable to create %s, please try again with different configurations", pageName));
            }
        }
    }

    public static void deletePage(WebDriver driver, String baseApplicationUrl, String baseApplicationUsername, String baseApplicationPassword, String pageName, String spaceKey) throws IOException, NullPointerException {
        String requestUrl = String.format("%s/rest/api/content/%s", baseApplicationUrl, getPageId(baseApplicationUrl, baseApplicationUsername, baseApplicationPassword, spaceKey, pageName));
        HttpDelete httpDelete = new HttpDelete(requestUrl);
        String credentials = Base64.getEncoder().encodeToString(String.format("%s:%s", baseApplicationUsername, baseApplicationPassword).getBytes());
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
        httpDelete.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        try (CloseableHttpResponse response = CLIENT.execute(httpDelete)) {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                Reporter.log(String.format("The page %s has successfully been deleted: ", pageName), true);
                WebDriverWait wait = new WebDriverWait(driver, 30, 2000);
                wait.until(d -> {
                    try {
                        return !pageExists(baseApplicationUrl, baseApplicationUsername, baseApplicationPassword, spaceKey, pageName);
                    } catch (IOException e) {
                        Reporter.log(String.format("Caught IOException where the exception is %s", e), true);
                    }
                    return null;
                });
            } else {
                String responseBody = EntityUtils.toString(response.getEntity());
                Reporter.log(String.format("%s page could not be deleted, response is: %s", pageName, responseBody), true);
                throw new IllegalStateException(String.format("Unable to delete %s please try again with different configurations", pageName));
            }
        }
    }

    public static String getPageId(String baseApplicationUrl, String baseApplicationUsername, String baseApplicationPassword, String spaceKey, String pageTitle) throws IOException {
        String title = SetupHelper.encodeValue(pageTitle);
        String requestURL = String.format("%s/rest/api/content?title=%s&spaceKey=%s", baseApplicationUrl, title, spaceKey);
        HttpGet httpGet = new HttpGet(requestURL);
        String credentials = Base64.getEncoder().encodeToString(String.format("%s:%s", baseApplicationUsername, baseApplicationPassword).getBytes());
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        try (CloseableHttpResponse response = CLIENT.execute(httpGet)) {
            int responseCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Reporter.log(String.format("Cannot find page with name %s, Response body is: %s ", pageTitle, responseBody), true);
                return null;
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    if (jsonObject.has("results")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        JSONObject result = jsonArray.getJSONObject(0);
                        String pageId = result.getString("id");
                        Reporter.log(String.format("Page found where page name = %s and pageId = %s ", pageTitle, pageId), true);
                        return pageId;
                    } else {
                        Reporter.log(String.format("'results' key not found in jsonObject where response body is %s", responseBody), true);
                        return null;
                    }
                } catch (JSONException jsonException) {
                    Reporter.log(String.format("Page not found where exception is: %s", jsonException), true);
                    return null;
                }
            }
        }
    }

    public static boolean pageExists(String baseApplicationUrl, String baseApplicationUsername, String baseApplicationPassword, String spaceKey, String pageTitle) throws IOException {
        String pageId = getPageId(baseApplicationUrl, baseApplicationUsername, baseApplicationPassword, spaceKey, pageTitle);
        String requestURL = String.format("%s/rest/api/content/%s", baseApplicationUrl, pageId);
        HttpGet httpGet = new HttpGet(requestURL);
        String credentials = Base64.getEncoder().encodeToString(String.format("%s:%s", baseApplicationUsername, baseApplicationPassword).getBytes());
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        try (CloseableHttpResponse response = CLIENT.execute(httpGet)) {
            int responseCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Reporter.log(String.format("Page found where pageId is %s, pageTitle is %s", pageId, pageTitle), true);
                return true;
            } else {
                Reporter.log(String.format("Page not found where responseBody is : %s", responseBody) ,true);
                return false;
            }
        }
    }
}
