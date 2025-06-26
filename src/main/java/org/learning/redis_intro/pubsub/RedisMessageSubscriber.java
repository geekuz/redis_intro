package org.learning.redis_intro.pubsub;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.StringRedisSerializer; // New import

@Slf4j
@Component
public class RedisMessageSubscriber implements MessageListener {

    // Use a StringRedisSerializer to deserialize the incoming message body and channel pattern
    private final StringRedisSerializer stringSerializer = new StringRedisSerializer();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // Deserialize the message body using StringRedisSerializer
        String receivedMessage = stringSerializer.deserialize(message.getBody());
        // Deserialize the pattern (channel name)
        String channel = stringSerializer.deserialize(pattern);

        log.info("Received message: '{}' on channel: '{}'", receivedMessage, channel);
        // Here, you would typically process the received message, e.g.,
        // push it to a WebSocket, update a UI, trigger another service, etc.
    }
}