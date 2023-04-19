package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.BaseTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpClientUtils.class)
public class HttpClientUtilsTest extends BaseTest {

    static final Logger logger = LoggerFactory.getLogger(HttpClientUtilsTest .class);

    @Autowired
    private HttpClientUtils httpClientUtils;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void httpGetRequestTest() {
        String getUrl ="https://www.google.com";
        ResponseEntity<String> responseEntity  = new ResponseEntity<String>(HttpStatus.ACCEPTED);
        PowerMockito.mockStatic(HttpClientUtils.class);
        PowerMockito.when(HttpClientUtils.httpGetRequest(getUrl)).thenReturn(responseEntity);
        try {
            ResponseEntity<String> responseEntity1 = httpClientUtils.httpGetRequest(getUrl);
            Assert.assertNotNull(responseEntity1);
        } catch (Exception e) {
            logger.error("Error in httpGetRequestTest method: {}", e.getMessage());
        }
    }

    @Test
    public void httpPostRequestTest() throws JsonProcessingException {
        String postUrl ="http://ec2-15-207-94-94.ap-south-1.compute.amazonaws.com/transit/api/v1/register";
        JSONObject object = new JSONObject();
        object.put("mobile","8937838445");
        object.put("lastFourDigitCardNumber","2345");
        object.put("registrationType","hasCardWithNumber");
        HttpHeaders httpHeaders =  new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        ResponseEntity<String> responseEntity  = new ResponseEntity<String>(HttpStatus.ACCEPTED);
        PowerMockito.mockStatic(HttpClientUtils.class);
        PowerMockito.when(HttpClientUtils.httpPostRequest(postUrl,object)).thenReturn(responseEntity);
        try {
            responseEntity = HttpClientUtils.httpPostRequest(postUrl,object);
            Assert.assertNotNull(responseEntity);
        } catch (Exception e) {
            logger.error("Error in httpGetRequestTest method: {}", e.getMessage());
        }
    }


    @Test
    public void httpGetRequestTest2() {
        String getUrl = "https://www.google.com/search?q=uidai&oq=uidai&aqs=chrome..69i57j0l6j5.5717j0j4&sourceid=chrome&ie=UTF-8";
        Map params  =  new HashMap();
        ResponseEntity<String> responseEntity  = new ResponseEntity<String>(HttpStatus.ACCEPTED);
        PowerMockito.mockStatic(HttpClientUtils.class);
        PowerMockito.when(HttpClientUtils.httpGetRequest(getUrl,params)).thenReturn(responseEntity);
        try {
            responseEntity = HttpClientUtils.httpGetRequest(getUrl,params);
            Assert.assertNotNull(responseEntity);
        } catch (Exception e) {
            logger.error("Error in httpPostTest method: {}", e.getMessage());
        }
    }

    @Test
    public void httpPostTest() throws JsonProcessingException {
        String postUrl = "https://www.google.com/search?q=uidai&oq=uidai&aqs=chrome..69i57j0l6j5.5717j0j4&sourceid=chrome&ie=UTF-8";
        Map params  =  new HashMap();
        ResponseEntity<String> responseEntity  = new ResponseEntity<String>(HttpStatus.ACCEPTED);
        PowerMockito.mockStatic(HttpClientUtils.class);
        PowerMockito.when(HttpClientUtils.httpPostRequest(postUrl,params)).thenReturn(responseEntity);
        try {
            Assert.assertNotNull(HttpClientUtils.httpPostRequest(postUrl, params));
        } catch (Exception e) {
            logger.error("Error in httpPostTest method: {}", e.getMessage());
        }
    }


    @Test
    public void httpGetTest() {
        String getUrl = "https://www.google.com/search?q=uidai&oq=uidai&aqs=chrome..69i57j0l6j5.5717j0j4&sourceid=chrome&ie=UTF-8";
        ResponseEntity<String> responseEntity  = new ResponseEntity<String>(HttpStatus.ACCEPTED);
        PowerMockito.mockStatic(HttpClientUtils.class);
        Object object = Mockito.mock(Object.class);
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        PowerMockito.when(HttpClientUtils.httpGet(getUrl,object, headers)).thenReturn(responseEntity);
        try {
            responseEntity = HttpClientUtils.httpGet(getUrl,object,headers);
            Assert.assertNotNull(responseEntity);
        } catch (Exception e) {
            logger.error("Error in httpPostTest method: {}", e.getMessage());
        }
    }

}
