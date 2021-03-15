package de.killbuqs.mall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MyMemberApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyMemberApplication.class, args);
	}

}
