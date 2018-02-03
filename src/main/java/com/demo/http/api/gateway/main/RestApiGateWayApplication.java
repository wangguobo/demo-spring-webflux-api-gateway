package com.demo.http.api.gateway.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.config.EnableWebFlux;
/**
* http gateway main diver
* 
* @author wang guobo 王国波
*/
@SpringBootApplication
@EnableWebFlux
@ComponentScan("com.demo.http.api.gateway")
@MapperScan(value="com.demo.http.api.gateway.dao.mapper")
public class RestApiGateWayApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(RestApiGateWayApplication.class, args);
	}
}
