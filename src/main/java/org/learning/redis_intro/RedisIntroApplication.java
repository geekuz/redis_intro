// C:/Users/Javohir/IdeaProjects/redis_intro/src/main/java/org/learning/redis_intro/RedisIntroApplication.java
package org.learning.redis_intro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
public class RedisIntroApplication {

    /**
     * By removing the JedisConnectionFactory bean, we let Spring Boot
     * auto-configure the default (and recommended) LettuceConnectionFactory.
     */

    /**
     * This is the central bean for Redis interactions.
     * We inject the auto-configured RedisConnectionFactory.
     * We also explicitly set serializers for keys, values, hash keys, and hash values
     * for consistency and better readability in your Redis database.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String serializer for standard keys
        template.setKeySerializer(new StringRedisSerializer());

        // Use JDK serialization for standard values (good for storing complex objects)
        template.setValueSerializer(new JdkSerializationRedisSerializer());

        // Use String serializer for hash keys
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use JDK serialization for hash values
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());

        template.afterPropertiesSet(); // Ensures the template is initialized
        return template;
    }

    public static void main(String[] args) {
        SpringApplication.run(RedisIntroApplication.class, args);
    }
}
