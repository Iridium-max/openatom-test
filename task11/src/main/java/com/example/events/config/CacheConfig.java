package com.example.events.config;

import com.example.events.api.Contracts.EventView;
import com.example.events.api.Contracts.PageView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.*;
import java.time.Duration;

@Configuration
public class CacheConfig implements CachingConfigurer {
    @Bean
    RedisCacheManagerBuilderCustomizer redisCacheCustomizer(ObjectMapper json){
        return builder->builder.cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(60)).disableCachingNullValues()
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new Jackson2JsonRedisSerializer<>(json, json.getTypeFactory().constructParametricType(PageView.class, EventView.class)))));
    }
    @Override public CacheErrorHandler errorHandler(){
        // 缓存是可重建加速层；不可用时直接读数据库，版本号仍保护恢复后的读取。
        return new CacheErrorHandler(){
            private void warn(RuntimeException e){ LoggerFactory.getLogger(CacheConfig.class).warn("Cache unavailable: {}", e.getClass().getSimpleName()); }
            public void handleCacheGetError(RuntimeException e, Cache c, Object key){ warn(e); }
            public void handleCachePutError(RuntimeException e, Cache c, Object key, Object value){ warn(e); }
            public void handleCacheEvictError(RuntimeException e, Cache c, Object key){ warn(e); }
            public void handleCacheClearError(RuntimeException e, Cache c){ warn(e); }
        };
    }
}
