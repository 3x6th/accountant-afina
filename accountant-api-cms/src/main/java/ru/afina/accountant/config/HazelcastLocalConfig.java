package ru.afina.accountant.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class HazelcastLocalConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = createConfig();
        return Hazelcast.newHazelcastInstance(config);
    }

    private Config createConfig() {
        Config config = new Config();

        // Имя кластера для разработки
        config.setClusterName("accountant-cluster-dev");
        config.setInstanceName("accountant-instance-dev");

        // Простая конфигурация сети для разработки
        configureNetwork(config.getNetworkConfig());

        // Конфигурация для rate limiter в разработке
        configureRateLimiterMap(config);

        // Настройки производительности для разработки
        configurePerformanceProperties(config);

        return config;
    }

    private void configureNetwork(NetworkConfig networkConfig) {
        // Порты для разработки
        networkConfig.setPort(5701)
                .setPortCount(5)
                .setPortAutoIncrement(true);

        // TCP/IP discovery для локальной разработки
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);

        joinConfig.getTcpIpConfig()
                .setEnabled(true)
                .addMember("127.0.0.1")  // Только localhost для разработки
                .setConnectionTimeoutSeconds(30);
    }

    private void configureRateLimiterMap(Config config) {
        MapConfig mapConfig = new MapConfig("rate-limiter-store");

        // Время жизни записи
        mapConfig.setTimeToLiveSeconds(120);

        // Без репликации для разработки (упрощает отладку)
        mapConfig.setBackupCount(0);

        // Отключаем read-backup для разработки
        mapConfig.setReadBackupData(false);

        // Упрощенная стратегия эвикции
        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setEvictionPolicy(EvictionPolicy.LRU)
                .setSize(5000)  // Меньший размер для разработки
                .setMaxSizePolicy(MaxSizePolicy.PER_NODE);
        mapConfig.setEvictionConfig(evictionConfig);

        // Оптимация для разработки
        mapConfig.setInMemoryFormat(InMemoryFormat.BINARY);

        // Включаем статистику для отладки
        mapConfig.setStatisticsEnabled(true);

        config.addMapConfig(mapConfig);
    }

    private void configurePerformanceProperties(Config config) {
        // Логирование
        config.setProperty("hazelcast.logging.type", "slf4j");

        // Настройки потоков для разработки
        config.setProperty("hazelcast.io.thread.count", "2");  // Меньше потоков
        config.setProperty("hazelcast.operation.thread.count", "2");
        config.setProperty("hazelcast.event.thread.count", "1");
        config.setProperty("hazelcast.socket.bind.any", "true");  // Разрешаем любой интерфейс
        config.setProperty("hazelcast.socket.server.bind.any", "true");

        // Упрощенные настройки для разработки
        config.setProperty("hazelcast.heartbeat.interval.seconds", "10");
        config.setProperty("hazelcast.max.no.heartbeat.seconds", "120");
    }

}
