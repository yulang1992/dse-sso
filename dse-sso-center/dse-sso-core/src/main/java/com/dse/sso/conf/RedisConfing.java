//package com.dse.sso.conf;
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.PropertyAccessor;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.interceptor.CacheErrorHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * redis缓存配置中心
// */
//@Configuration
//@EnableCaching
//public class RedisConfing {
//
//   private static final Logger logger =LoggerFactory.getLogger(RedisConfing.class);
//
//	/**
//	 * 创建缓存管理器
//	 */
//	@Bean
//	public CacheManager getRedisCacheManager(RedisTemplate<Object,Object> redisTemplate) {
//		RedisCacheManager cacheManager =new RedisCacheManager(redisTemplate);
//		//cacheManager.setCacheNames("DSE-SSO-CENTER"); //设置缓存名称
//		Map<String, Long> expires = new HashMap<String, Long>();
//		expires.put("user", 6000L);
//		expires.put("city", 600L);
//		cacheManager.setExpires(expires);
//		cacheManager.setDefaultExpiration(60 * 60 * 24 * 7);  //设置缓存默认时间  单位 秒
//		return cacheManager;
//	}
//
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Bean
//	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
//
//		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
//		redisTemplate.setConnectionFactory(factory);
//
//		//使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
//		Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
//
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//		serializer.setObjectMapper(mapper);
//
//		redisTemplate.setValueSerializer(serializer);
//		// 使用StringRedisSerializer来序列化和反序列化redis的key值
//		redisTemplate.setKeySerializer(new StringRedisSerializer());
//		redisTemplate.afterPropertiesSet();
//
//		return redisTemplate;
//	}
//
//	@Bean
//    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
//        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
//        stringRedisTemplate.setConnectionFactory(factory);
//        return stringRedisTemplate;
//    }
//
//
//	/**
//	 * [异常处理]
//	 * @version [版本, 2017-04-12]
//	 */
//	@Bean
//	public CacheErrorHandler errorHandler() {
//		CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
//
//			public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
//				logger.error("redis异常：key=[{}]", key, e);
//			}
//
//
//			public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
//				logger.error("redis异常：key=[{}]", key, e);
//			}
//
//
//			public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
//				logger.error("redis异常：key=[{}]", key, e);
//			}
//
//
//			public void handleCacheClearError(RuntimeException e, Cache cache) {
//				logger.error("redis异常：", e);
//			}
//		};
//		return cacheErrorHandler;
//	}
//}