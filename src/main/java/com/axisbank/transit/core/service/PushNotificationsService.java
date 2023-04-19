package com.axisbank.transit.core.service;

import org.springframework.http.HttpEntity;

import java.util.concurrent.CompletableFuture;

public interface PushNotificationsService {
    public CompletableFuture<String> send(HttpEntity<String> entity);
}
