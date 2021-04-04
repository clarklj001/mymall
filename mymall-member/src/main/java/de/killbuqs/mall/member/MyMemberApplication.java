package de.killbuqs.mall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "de.killbuqs.mall.member.feign")
public class MyMemberApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyMemberApplication.class, args);
	}

}
