package com.sbs.exam2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class Exam2Application {

	public static void main(String[] args) {
		SpringApplication.run(Exam2Application.class, args);
	}

}
