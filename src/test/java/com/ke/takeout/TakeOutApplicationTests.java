package com.ke.takeout;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
class TakeOutApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void redisTest() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("name", "minghui", 10L, TimeUnit.SECONDS);
        System.out.println(valueOperations.get("name"));
        valueOperations.setIfAbsent("age", 18);

        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put("hash", "name", "minghui");
        hashOperations.put("hash", "age", 20);
        hashOperations.get("hash", "name");


    }

}
