package com.nebulak.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

// New required imports
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.security.jackson2.SecurityJackson2Modules; 

@Configuration
public class RedisCacheConfig {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // 1. Configure the ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        
        // Register Spring Security Modules (required for UserDetails and other security types)
        SecurityJackson2Modules.getModules(getClass().getClassLoader())
                              .forEach(mapper::registerModule);

        // 2. Configure Default Typing and Allowlisting
        mapper.activateDefaultTyping(
            // Use LaissezFaireSubTypeValidator to allow the allowlist/denylist mechanism 
            // to be managed by the application/Spring Security.
            LaissezFaireSubTypeValidator.instance, 
            ObjectMapper.DefaultTyping.NON_FINAL, 
            JsonTypeInfo.As.PROPERTY
        );
        
        // 3. Create the serializer with the configured mapper
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = 
            new GenericJackson2JsonRedisSerializer(mapper);
        
        // 4. Return the cache configuration
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60)) 
                .disableCachingNullValues() 
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer));
    }
}