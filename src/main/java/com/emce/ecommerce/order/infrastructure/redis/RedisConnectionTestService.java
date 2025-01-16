package com.emce.ecommerce.order.infrastructure.redis;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RedisConnectionTestService {
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @Value("${spring.redis.host}")
//    public String test;
//
//    @PostConstruct
//    public void testRedisConnection() {
//        try {
//            // Test writing to Redis
////            log.error("testing REDISCUSSED" + test);
//            redisTemplate.opsForValue().set("testKey", "testValue");
//            System.out.println("Redis SET operation successful.");
//
//            // Test reading from Redis
//            String value = redisTemplate.opsForValue().get("testKey");
//            System.out.println("Redis GET operation successful. Value: " + value);
//
//            if ("testValue".equals(value)) {
//                System.out.println("Redis connection test PASSED!");
//            } else {
//                System.out.println("Redis connection test FAILED!");
//            }
//
//        } catch (Exception e) {
//            System.err.println("Failed to connect to Redis.");
//            e.printStackTrace();
//        }
//    }
}
