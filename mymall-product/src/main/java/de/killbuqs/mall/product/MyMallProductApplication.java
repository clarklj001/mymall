package de.killbuqs.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

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
 *  模板引擎
 *  	1）thymeleaf-starter 关闭缓存
 *  	2）静态资源都放在static文件夹下就可以按照路径直接访问
 *  	3）页面放在templates下，直接访问
 *  		SpringBoot 默认找 index.html  WebMvcAutoConfiguration
 *  	4) 开发环境 设置application.properties   spring.thymeleaf.cache: false 添加 devtools依赖
 * 
 * 整合redis
 * 	1）spring-boot-starter-data-redis
 * 	2）配置redis host, port
 *  3）使用 StringRedisTemplate 进行操作
 *  
 * 整合redisson作为分布式锁等功能框架
 * 	1） 引入依赖 https://github.com/redisson/redisson
 * 	2） 配置redisson 
 * 
 * 整合SpringCache简化缓存开发
 * 	1） spring-boot-starter-cache, spring-boot-starter-data-redis
 * 	2) 配置
 * 		2.1）缓存自动配置 CacheAutoConfiguration, RedisCacheConfiguration @EnableCaching
 * 		2.2) 配置文件
 * 	3）测试 
 * 		@Cacheable: Triggers cache population.
 *		@CacheEvict: Triggers cache eviction. 修改数据后缓存清除
 *		@CachePut: Updates the cache without interfering with the method execution. 修改数据后放入缓存 （双写模式）
 *		@Caching: Regroups multiple cache operations to be applied on a method.
 *		@CacheConfig: Shares some common cache-related settings at class-level.
 *	4) 原理
 *		CacheAutoConfiguration 导入了 RedisCacheConfiguration -> 自动配置了缓存管理器 RedisCacheManager
 *		-> 初始化缓存 -> 如果redisCacheConfiguration有就用以有的，没有的话用默认的（cacheProperties）
 *		-> 想改缓存的配置，只需要给容器中放一个RedisCacheConfiguration即可，
 *		-> 就会应用到当前RedisCacheManager管理的所有缓存分区中
 * 
 * 	Spring-Cache的不足
 * 	1） 读模式
 * 		// 处理缓存穿透: 空结果缓存 spring.cache.redis.cache-null-values=true
 *		// 处理缓存击穿: 加锁 @Cachable sync=true 加了一个本地锁
 *		// 处理缓存雪崩： 设置加随机值的过期时间 发生在极端情况下，十几万key同时失效，十几万访问同时发生
 *	2） 写模式(缓存与数据库一致)
 *		1)读写加锁 应用于读多写少
 *		2）引入Canal，感知到MySQL的更新去更新数据库
 *		3）读多写多，直接去数据库查询
 *
 *	总结 常规数据（读多写少，即时性，一致性要求不高的数据），完全可以使用Spring-Cache
 *	特殊数据：必须特殊设计
 *
 * @author jlong
 *
 */

@MapperScan("de.killbuqs.mall.product.dao")
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "de.killbuqs.mall.product.feign")
public class MyMallProductApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyMallProductApplication.class, args);
	}

}
