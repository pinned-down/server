package de.pinneddown.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
public class ServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	public Random random() {
		return new Random();
	}
}
