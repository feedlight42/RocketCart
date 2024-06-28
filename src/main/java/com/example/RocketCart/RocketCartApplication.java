package com.example.RocketCart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RocketCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(RocketCartApplication.class, args);
	}
}
