package de.killbuqs.mymall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,  TomcatMetricsAutoConfiguration.class })
public class MyMallGatewayApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyMallGatewayApplication.class, args);
	}

}
