package com.axisbank.transit.core.shared.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpClientUtils {

    private HttpClientUtils() {
        throw new AssertionError("Private Constructor, not be accessed via reflection");
    }

    public static RestTemplate getRestTemplate() {
        return new RestTemplate(getHttpComponentsClientHttpRequestFactory());
    }

    private static HttpComponentsClientHttpRequestFactory getHttpComponentsClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(15000);
        factory.setConnectTimeout(15000);
        factory.setConnectionRequestTimeout(15000);
        return factory;
    }

    public static ResponseEntity<String>  httpPostRequest(String url, Object data) throws JsonProcessingException {
        String stringReq = CommonUtils.convertObjectToJsonString(data);
        log.info("URL:{}\n Proceed Request:{}",url, stringReq);
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> res = httpPost(url,stringReq, headers);
        log.info("Proceed Resp:{}",res.getBody());
        return res;
    }

    public static void  httpPostRequestForFile(String url, String filePath) throws Exception {

        log.info("URL: "+url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("multipartFile", new FileSystemResource(filePath));
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity(form, headers);
        HttpClientUtils.getRestTemplate().postForEntity(url,requestEntity,String.class);
    }

    public static ResponseEntity<String>  httpGetRequest(String url){
        return httpGetRequest(url, new HashMap<>());
    }

    public static ResponseEntity<String>  httpGetRequest(String url, Map<String,?> params){
        return HttpClientUtils.getRestTemplate().getForEntity(url,
                String.class, params);
    }

    public static ResponseEntity<String> httpPost(String url, String request, HttpHeaders headers) {
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> httpEntity = new HttpEntity<>(request,headers);
        return  HttpClientUtils.getRestTemplate().postForEntity(url, httpEntity,String.class);
    }

    public static ResponseEntity<String> httpGet(String url, Object request, HttpHeaders headers) {
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity httpEntity = new HttpEntity<>(headers);
        return  HttpClientUtils.getRestTemplate().exchange(url, HttpMethod.GET,httpEntity,String.class, new HashMap<>());
    }

}
