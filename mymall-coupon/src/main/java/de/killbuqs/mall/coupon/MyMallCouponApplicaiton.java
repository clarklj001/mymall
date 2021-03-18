package de.killbuqs.mall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 使用nacos配置中心
 * 		导入依赖
 * 		创建bootsrap.properties 并加入配置
 * 		给nacos中心添加配置文件名称(Data Id)
 * 		Controller添加@RefreshScope 激活动态获取配置  @Value 获得值
 * 
 * 命名空间
 * 		默认(public)
 * 			每一个微服务之间互相隔离，每一个微服务都
 * 			命名空间的选择通过bootstrap.properties的 spring.cloud.nacos.config.namespace=<Namespace ID>
 *      
 * 配置集
 * 
 * 配置集ID
 * 
 * 配置分组
 * 		同一个配置文件可以有不同的分组
 * 			默认为DEFAULT_GROUP
 * 			相对开发(dev),测试(test),生产(prod)可以创建相应的分组做环境的隔离
 * 			可以添加分组 dev,test,prod
 * 			
 * 每个微服务创建自己的命名空间， 使用配置分组做环境的隔离
 * 
 * 同时加载多个配置集!!!
 * 
 * @author jlong
 * 
 *
 */
@SpringBootApplication
public class MyMallCouponApplicaiton {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyMallCouponApplicaiton.class, args);
	}

}
