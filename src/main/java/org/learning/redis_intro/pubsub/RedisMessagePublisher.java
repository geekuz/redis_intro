package org.learning.redis_intro.pubsub;

import org.learning.redis_intro.RedisIntroApplication; // New import for CHAT_CHANNEL
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate; // Changed to StringRedisTemplate
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedisMessagePublisher {

    private final StringRedisTemplate stringRedisTemplate; // Injected StringRedisTemplate

    @Autowired
    public RedisMessagePublisher(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void publish(String message) {
        log.info("Publishing message: '{}' to channel: '{}'", message, RedisIntroApplication.CHAT_CHANNEL);
        // StringRedisTemplate's convertAndSend uses StringRedisSerializer by default
        stringRedisTemplate.convertAndSend(RedisIntroApplication.CHAT_CHANNEL, message);
    }
}