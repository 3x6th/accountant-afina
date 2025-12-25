package ru.afina.accountant.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.afina.accountant.properties.RateLimiterProperties;

@Configuration
@Profile("prod")
@RequiredArgsConstructor
public class HazelcastProdConfig {

    private final RateLimiterProperties rateLimiterProperties;

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = createConfig();
        return Hazelcast.newHazelcastInstance(config);
    }

    private Config createConfig() {
        Config config = new Config();

        // Имя кластера для production
        config.setClusterName("accountant-cluster");
        config.setInstanceName("accountant-instance-prod");

        // Конфигурация сети для Docker production
        configureNetwork(config.getNetworkConfig());

        // Конфигурация для rate limiter
        configureRateLimiterMap(config);

        // Настройки производительности
        configurePerformanceProperties(config);

        // Конфигурация REST API
        configureRestApi(config);

        return config;
    }

    private void configureNetwork(NetworkConfig networkConfig) {
        // Порты для кластера
        networkConfig.setPort(5701)
                .setPortCount(10)
                .setPortAutoIncrement(true);

        // Настройки выхода (для Docker)
        networkConfig.addOutboundPortDefinition("5701-5710");

        // Интерфейсы - ТОЛЬКО подсеть Docker Compose
        InterfacesConfig interfacesConfig = networkConfig.getInterfaces();
        interfacesConfig.setEnabled(true)
                .addInterface("172.20.*.*");  // Ваша подсеть из docker-compose.yml

        // TCP/IP discovery (работает в Docker)
        JoinConfig joinConfig = networkConfig.getJoin();

        // Отключаем multicast
        joinConfig.getMulticastConfig().setEnabled(false);

        // Включаем TCP/IP discovery
        TcpIpConfig tcpIpConfig = joinConfig.getTcpIpConfig();
        tcpIpConfig.setEnabled(true)
                .addMember("accountant-app-1")
                .addMember("accountant-app-2")
                .setConnectionTimeoutSeconds(60);
    }

    private void configureRestApi(Config config) {
        ManagementCenterConfig mcConfig = new ManagementCenterConfig();
        mcConfig.setScriptingEnabled(true);
        config.setManagementCenterConfig(mcConfig);
    }

    private void configureRateLimiterMap(Config config) {
        MapConfig mapConfig = new MapConfig(rateLimiterProperties.getMapName());

        // Время жизни записи (совпадает с RateLimiterService)
        mapConfig.setTimeToLiveSeconds(rateLimiterProperties.getTimeToLiveSeconds());
        mapConfig.setMaxIdleSeconds(rateLimiterProperties.getMaxIdleSeconds());

        // Синхронная репликация для консистентности
        mapConfig.setBackupCount(1);

        // Read-your-writes consistency
        mapConfig.setReadBackupData(true);

        // Стратегия эвикции
        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setEvictionPolicy(EvictionPolicy.LRU)
                .setSize(10000)
                .setMaxSizePolicy(MaxSizePolicy.PER_NODE);
        mapConfig.setEvictionConfig(evictionConfig);

        // Оптимация для частых операций put
        mapConfig.setInMemoryFormat(InMemoryFormat.BINARY);

        // Улучшает производительность
        mapConfig.setStatisticsEnabled(false);

        config.addMapConfig(mapConfig);
    }

    private void configurePerformanceProperties(Config config) {
        // Логирование
        config.setProperty("hazelcast.logging.type", "slf4j");

        // Настройки потоков для production
        config.setProperty("hazelcast.io.thread.count", "4");
        config.setProperty("hazelcast.operation.thread.count", "4");
        config.setProperty("hazelcast.event.thread.count", "4");

        // Для Docker
        config.setProperty("hazelcast.socket.bind.any", "false");
        config.setProperty("hazelcast.socket.server.bind.any", "false");

        // Для лучшего обнаружения в Docker
        config.setProperty("hazelcast.initial.wait.seconds", "30");

        // Оптимизация для production
        config.setProperty("hazelcast.heartbeat.interval.seconds", "5");
        config.setProperty("hazelcast.max.no.heartbeat.seconds", "60");
    }

}
