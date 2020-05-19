package com.garry.springlifecycle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
//@ImportResource("application-context.xml")
public class SpringLifecycleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringLifecycleApplication.class, args);
	}

}
