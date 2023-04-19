package com.axisbank.transit.core.shared.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisClient {
    @Autowired
    private RedisTemplate< String, Object > template;

    public String getValue(String key ){
        try {
            return Objects.requireNonNull(template.opsForValue().get(key)).toString();
        } catch (Exception ex) {
            log.error("Failed to fetch redis key: {}", ex.getMessage());
            return null;
        }
    }

    public void setValue(String key, String value ) {
        template.opsForValue().set( key, value );
    }

    public void setValue(String key, String value, long expiry) {
        template.opsForValue().set( key, value );
        template.expire( key, expiry, TimeUnit.SECONDS );
    }

    public void deleteKey(String key) {
        template.delete(key);
    }

    public Long getTtl(String key){
        try {
            return template.getExpire(key);
        } catch (NullPointerException ex){
            log.error("Failed To get TTL for key: {}",ex.getMessage());
            return null;
        }
    }
    public void deletePattern(String pattern){
        Set<String> keys = getPattern(pattern);
        if(keys!=null){
            template.delete(keys);
        }
    }
    public Set<String> getPattern(String pattern){
        return template.keys(pattern);
    }
}
