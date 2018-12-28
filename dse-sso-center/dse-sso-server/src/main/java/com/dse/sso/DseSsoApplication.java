package com.dse.sso;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dse.sso.mapper")
public class DseSsoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DseSsoApplication.class, args);
	}

}

