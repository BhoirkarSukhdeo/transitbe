package com.axisbank.transit.core.service.impl;

import com.axisbank.transit.core.interceptors.HeaderRequestInterceptor;
import com.axisbank.transit.core.service.PushNotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class PushNotificationsServiceImpl implements PushNotificationsService {
    @Value("${app.firebase.server_key}")
    private String FIREBASE_SERVER_KEY;
    @Value("${app.firebase.url}")
    private String FIREBASE_API_URL;

    /**
     * Service to send push notifications to device(s)
     * @param entity
     * @return
     */
    @Async
    @Override
    public CompletableFuture<String> send(HttpEntity<String> entity) {

        RestTemplate restTemplate = new RestTemplate();
        log.info("Sending Push Notification");
        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
        restTemplate.setInterceptors(interceptors);
        log.info("PushNotificationData:{}", entity.getBody());
        String firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL, entity, String.class);
        log.info("Push Notification response: {}", firebaseResponse);
        return CompletableFuture.completedFuture(firebaseResponse);
    }
}
