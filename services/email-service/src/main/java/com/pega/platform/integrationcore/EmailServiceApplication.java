package com.pega.platform.integrationcore;

import com.pega.platform.integrationcore.client.email.EmailClient;
import com.pega.platform.integrationcore.client.email.internal.javamail.JavaEmailClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class EmailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailServiceApplication.class, args);
	}

	@Bean @Scope("prototype")
	public EmailClient createJavaMailClient() {
		return new JavaEmailClient();
	}
}
