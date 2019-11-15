package com.MyQuizAppSocialSecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MyQuizAppSocialSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyQuizAppSocialSecurityApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public CookieCsrfTokenRepository geTokenRepository() {
		return new CookieCsrfTokenRepository().withHttpOnlyFalse();
	}
	
}
