package com.mariamatvienko.olx_lab;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OlxLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(OlxLabApplication.class, args);
		WebDriverManager.chromedriver().setup();
	}

}
