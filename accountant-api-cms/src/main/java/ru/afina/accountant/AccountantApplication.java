package ru.afina.accountant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class AccountantApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountantApplication.class, args);
	}

}
