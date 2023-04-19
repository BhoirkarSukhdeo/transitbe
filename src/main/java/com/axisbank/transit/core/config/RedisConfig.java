package com.axisbank.transit.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class RedisConfig {
    @Value("${app.redis.host}")
    private String redisHost;
    @Value("${app.redis.port}")
    private int redisPort;
    @Value("${app.redis.db.index}")
    int redisDbIndex;

    @Value("${app.redis.sentinels.hosts}")
    String redisSentinelHosts;
    @Value("${app.redis.sentinel.master.name}")
    String redisMasterName;
    @Value("${app.redis.use-sentinels}")
    boolean useSentinels;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        if(useSentinels){
            Set<String> nodes = new HashSet<>(Arrays.asList(redisSentinelHosts.split(",")));
            RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration(redisMasterName, nodes);
            redisSentinelConfiguration.setDatabase(redisDbIndex);
            return new JedisConnectionFactory(redisSentinelConfiguration);
        }
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                redisHost, redisPort);
        redisStandaloneConfiguration.setDatabase(redisDbIndex);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    RedisTemplate< String, Object > redisTemplate() {
        final RedisTemplate< String, Object > template =  new RedisTemplate< String, Object >();
        template.setConnectionFactory( jedisConnectionFactory() );
        template.setKeySerializer( new StringRedisSerializer() );
        template.setHashValueSerializer( new GenericToStringSerializer< Object >( Object.class ) );
        template.setValueSerializer( new GenericToStringSerializer< Object >( Object.class ) );
        return template;
    }
}