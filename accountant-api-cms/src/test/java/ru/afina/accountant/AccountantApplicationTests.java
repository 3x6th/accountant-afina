package ru.afina.accountant;

import com.hazelcast.core.HazelcastInstance;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import ru.afina.accountant.config.TestConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class AccountantApplicationTests {

//	@MockBean
//	HazelcastInstance hazelcastInstance;
//
//	@Bean
//	@Primary
//	public HazelcastInstance hazelcastInstance() {
//		// Создаем мок HazelcastInstance
//		return Mockito.mock(HazelcastInstance.class);
//	}

	@Test
	void contextLoads() {
	}

}
