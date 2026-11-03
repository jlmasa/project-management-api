package com.jlmasa.pms_backend;

import org.springframework.boot.SpringApplication;

public class TestPmsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(PmsBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
