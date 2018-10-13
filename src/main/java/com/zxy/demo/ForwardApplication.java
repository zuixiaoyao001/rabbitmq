package com.zxy.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ForwardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForwardApplication.class, args);
	}
}
