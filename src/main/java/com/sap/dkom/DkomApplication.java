package com.sap.dkom;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DkomApplication {

	public static void main(String[] args) {
		SpringApplication.run(DkomApplication.class, args);
	}

}

