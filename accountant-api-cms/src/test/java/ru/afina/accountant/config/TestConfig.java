package ru.afina.accountant.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        // Создаем реальный embedded Hazelcast для тестов
        Config config = new Config();
        config.setClusterName("test-cluster");
        config.setInstanceName("test-instance");

        // Минимальная конфигурация
        config.getNetworkConfig()
                .setPort(5701)
                .setPortAutoIncrement(true);

        config.getNetworkConfig().getJoin()
                .getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin()
                .getTcpIpConfig().setEnabled(false);

        // Настройка для rate limiter
        config.getMapConfig("rate-limiter-store")
                .setBackupCount(0)
                .setStatisticsEnabled(false);

        return Hazelcast.newHazelcastInstance(config);
    }
}
