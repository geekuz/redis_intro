// C:/Users/Javohir/IdeaProjects/redis_intro/src/main/java/org/learning/redis_intro/RedisIntroApplication.java
package org.learning.redis_intro;

import org.learning.redis_intro.pubsub.RedisMessageSubscriber; // New import
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate; // New import
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.listener.ChannelTopic; // New import
import org.springframework.data.redis.listener.RedisMessageListenerContainer; // New import
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter; // New import

@SpringBootApplication
public class RedisIntroApplication {

    // Define the channel name for Pub/Sub as a constant
    public static final String CHAT_CHANNEL = "my-chat-channel";

    /**
     * This is the central bean for Redis interactions for general data structures.
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

    /**
     * Configures a StringRedisTemplate for operations where keys and values are strings.
     * This is particularly useful for Pub/Sub where messages are often strings.
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    // --- Pub/Sub Configuration ---

    /**
     * Configures the RedisMessageListenerContainer which listens for messages on Redis channels.
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // Register our listener to listen on the defined channel
        container.addMessageListener(listenerAdapter, topic());
        return container;
    }

    /**
     * Adapts our custom RedisMessageSubscriber to the Spring Data Redis MessageListener interface.
     * This allows the container to invoke the onMessage method of our subscriber.
     */
    @Bean
    MessageListenerAdapter listenerAdapter(RedisMessageSubscriber subscriber) {
        // The default method name for MessageListenerAdapter is "handleMessage",
        // but our subscriber implements MessageListener directly, so "onMessage" is used.
        return new MessageListenerAdapter(subscriber);
    }

    /**
     * Defines the Redis channel topic.
     */
    @Bean
    ChannelTopic topic() {
        return new ChannelTopic(CHAT_CHANNEL);
    }

    public static void main(String[] args) {
        SpringApplication.run(RedisIntroApplication.class, args);
    }
}