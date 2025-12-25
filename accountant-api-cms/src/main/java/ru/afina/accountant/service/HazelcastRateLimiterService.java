package ru.afina.accountant.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.*;
import org.springframework.stereotype.Service;
import ru.afina.accountant.properties.RateLimiterProperties;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class HazelcastRateLimiterService {

    private final HazelcastInstance hazelcastInstance;
    private final RateLimiterProperties rateLimiterProperties;

//    private static final int MAX_REQUESTS = 100;      // 100 запросов
//    private static final int TIME_WINDOW = 60;        // за 60 секунд
//    private static final String MAP_NAME = "rate-limiter-store";

    // Эта мапа видна всем инстансам приложения
    private IMap<String, RateLimitData> getRateLimitMap() {
        return hazelcastInstance.getMap(rateLimiterProperties.getMapName());
    }

    /**
     * Проверяем можно ли выполнить запрос для IP
     * Этот метод вызывается на ЛЮБОМ из 2 инстансов
     */
    public boolean isAllowed(String clientIp) {
        IMap<String, RateLimitData> rateMap = getRateLimitMap();
        LocalDateTime now = LocalDateTime.now();

        RateLimitData data = rateMap.get(clientIp);

        // Первый запрос от этого IP
        if (data == null) {
            data = new RateLimitData(1, now);
            // Сохраняем в Hazelcast с TTL
            rateMap.put(clientIp, data, rateLimiterProperties.getTimeWindowSeconds(), TimeUnit.SECONDS);
            return true;
        }

        // Проверяем не истекло ли окно времени
        if (data.getFirstRequestTime().plusSeconds(rateLimiterProperties.getTimeWindowSeconds()).isBefore(now)) {
            // Окно истекло - сбрасываем счетчик
            data = new RateLimitData(1, now);
            rateMap.put(clientIp, data, rateLimiterProperties.getTimeWindowSeconds(), TimeUnit.SECONDS);
            return true;
        }

        // Проверяем не превышен ли лимит
        if (data.getRequestCount() >= rateLimiterProperties.getMaxRequests()) {
            return false;
        }

        // Увеличиваем счетчик (все инстансы увидят это изменение)
        data.increment();
        rateMap.put(clientIp, data, rateLimiterProperties.getTimeWindowSeconds(), TimeUnit.SECONDS);
        return true;
    }

    public RateLimitStatus getStatus(String clientIp) {
        IMap<String, RateLimitData> rateMap = getRateLimitMap();
        RateLimitData data = rateMap.get(clientIp);

        if (data == null) {
            return new RateLimitStatus(clientIp,
                                 0,
                                        rateLimiterProperties.getMaxRequests(),
                                 true,
                                        rateLimiterProperties.getTimeWindowSeconds());
        }

        int remaining = rateLimiterProperties.getMaxRequests() - data.getRequestCount();
        boolean allowed = remaining > 0;

        return new RateLimitStatus(clientIp, data.getRequestCount(),
                remaining, allowed, rateLimiterProperties.getTimeWindowSeconds());
    }

    // Класс данных должен быть Serializable для Hazelcast
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RateLimitData implements java.io.Serializable {
        private int requestCount;
        private LocalDateTime firstRequestTime;

        private void increment() {
            this.requestCount ++;
        }
    }

    @Value
    public static class RateLimitStatus {
        String ip;
        int currentRequests;
        int remainingRequests;
        boolean allowed;
        int timeWindowSeconds;
    }
}
