package com.gymcrm.gym_crm_discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class GymCrmDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymCrmDiscoveryApplication.class, args);
	}

}
