package com.spring_boot_sec_app;

import com.spring_boot_sec_app.service.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringBootSecAppApplication {
	@Autowired
	EmailServiceImpl emailService;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecAppApplication.class, args);
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@EventListener(ApplicationReadyEvent.class)

	public void sendmail() throws Exception {
		emailService.sendEmail("kenechukwubanego@gmail.com", "This is subject", "This is body of email");
	}
}
