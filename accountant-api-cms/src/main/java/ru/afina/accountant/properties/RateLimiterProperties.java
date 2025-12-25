package ru.afina.accountant.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    /**
     * Максимальное количество запросов за временное окно
     */
    private int maxRequests = 100;

    /**
     * Временное окно в секундах
     */
    private int timeWindowSeconds = 60;

    /**
     * Имя мапы в Hazelcast
     */
    private String mapName = "rate-limiter-store";

    /**
     * Время жизни записи в секундах
     */
    private int timeToLiveSeconds = 120;

    /**
     * Максимальное время простоя в секундах
     */
    private int maxIdleSeconds = 60;

}
