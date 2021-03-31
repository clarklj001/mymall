package de.killbuqs.mymall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MyMallThirdPartyApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyMallThirdPartyApplication.class, args);
	}

}
