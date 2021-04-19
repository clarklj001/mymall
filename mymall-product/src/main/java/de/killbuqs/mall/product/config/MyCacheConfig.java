package de.killbuqs.mall.product.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableConfigurationProperties(CacheProperties.class)
@Configuration
@EnableCaching
public class MyCacheConfig {
	
	/**
	 * 配置文件中的值生效
	 * @ConfigurationProperties(prefix = "spring.cache")
	 * public CacheProperties
	 * 
	 * 需要使用 @EnableConfigurationProperties(CacheProperties.class)
	 * 
	 * @return
	 */
	@Bean
	RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {

		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

//		config = config.entryTtl(null);

		config = config.serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()));

//		config = config.serializeValuesWith(SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));
		config = config.serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

		// 将配置文件中的配置生效
		// org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration.determineConfiguration(ClassLoader)
		Redis redisProperties = cacheProperties.getRedis();
		if (redisProperties.getTimeToLive() != null) {
			config = config.entryTtl(redisProperties.getTimeToLive());
		}
		if (redisProperties.getKeyPrefix() != null) {
			config = config.prefixKeysWith(redisProperties.getKeyPrefix());
		}
		if (!redisProperties.isCacheNullValues()) {
			config = config.disableCachingNullValues();
		}
		if (!redisProperties.isUseKeyPrefix()) {
			config = config.disableKeyPrefix();
		}
		
		return config;
	}

}
