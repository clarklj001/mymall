package de.killbuqs.mall.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "de.killbuqs.mall.ware.feign")
public class MyMallWareApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyMallWareApplication.class, args);
	}

}
