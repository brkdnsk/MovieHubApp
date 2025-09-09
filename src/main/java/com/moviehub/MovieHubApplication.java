package com.moviehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.moviehub")  // TÜM paketleri açıkça tara
public class MovieHubApplication {
	public static void main(String[] args) {
		SpringApplication.run(MovieHubApplication.class, args);
	}
}
