package com.axisbank.transit.core.shared.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SoapUtils {

    public static String getSoapResponse(String url, String requestXML, String httpMethod) throws Exception {
        log.debug("Url: {}\n Request Body: {}",url,requestXML);
        OkHttpClient client = new OkHttpClient().newBuilder().hostnameVerifier((s, sslSession) -> true)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20L, TimeUnit.SECONDS)
                .writeTimeout(20L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("text/xml");
        RequestBody body = RequestBody.create(mediaType, requestXML);
        Request request = new Request.Builder()
                .url(url)
                .method(httpMethod, body)
                .addHeader("Content-Type", "text/xml")
                .build();
        ResponseBody responseBody = client.newCall(request).execute().body();
        if (responseBody == null){
            log.error("Null Received from API response");
            throw new Exception("Null Received from API response");
        }
        String data = responseBody.string();
        log.info("Response Body: {}", data);
        return data;
    }
}
