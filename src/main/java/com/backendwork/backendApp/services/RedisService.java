package com.backendwork.backendApp.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, String value, long timeoutMinutes) {

        redisTemplate.opsForValue()
                .set(key, value, timeoutMinutes, TimeUnit.MINUTES);
    }

    public String get(String key) {

        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {

        redisTemplate.delete(key);
    }

    public boolean exists(String key) {

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}