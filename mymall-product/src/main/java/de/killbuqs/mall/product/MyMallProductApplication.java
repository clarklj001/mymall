package de.killbuqs.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 
 * 1. 整合MyBatis
 *		<dependency>
 *			<groupId>com.baomidou</groupId>
 *			<artifactId>mybatis-plus-boot-starter</artifactId>
 *			<version>3.2.0</version>
 *		</dependency>
 * 2. 配置
 * 		配置数据源
 * 			导入数据库驱动 dependency
 * 			在application.yml配置数据源相关信息
 * 		配置MyBatis-Plus
 * 			@MapperScan
 * 			告诉MyBatisPlus 配置文件位置
 * 			
 *  逻辑删除
 *  	1) 配置全局的逻辑删除规则
 *  	2) 配置逻辑删除的组件Bean
 *  	3) 给Bean加上逻辑删除注解 @TableLogic
 *  
 *  JSR303 校验
 *  	1） Bean上添加校验注解
 *  	2) Controller方法上添加 @Valid
 *  	3）校验的Bean后紧跟一个BindingResult 封装了校验结果
 *  	4） @Validated 分组校验 (默认没有指定分组的校验注解，在分组校验情况下不生效)
 *      5) 自定义校验
 *      	编写一个自定义的校验注解
 *      	编写一个自定义的校验器并关联校验注解
 *  
 *  统一的异常处理
 *  @ControllerAdvice
 * 
 * @author jlong
 *
 */
@MapperScan("de.killbuqs.mall.product.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class MyMallProductApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyMallProductApplication.class, args);
	}

}
