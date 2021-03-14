package de.killbuqs.mall.coupon;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Mapper
@SpringBootApplication
public class MyMallCouponApplicaiton {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyMallCouponApplicaiton.class, args);
	}


}
