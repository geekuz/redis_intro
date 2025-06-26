// C:/Users/Javohir/IdeaProjects/redis_intro/src/main/java/org/learning/redis_intro/controller/RedisController.java
package org.learning.redis_intro.controller;

import org.learning.redis_intro.RedisIntroApplication; // New import for CHAT_CHANNEL
import org.learning.redis_intro.model.Product;
import org.learning.redis_intro.pubsub.RedisMessagePublisher; // New import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired // Inject the new publisher
    private RedisMessagePublisher messagePublisher;

    // Define constants for our Redis keys to avoid magic strings
    private static final String PRODUCT_HASH_KEY = "Product";
    private static final String MESSAGE_QUEUE_KEY = "MessageQueue";
    private static final String TAGS_SET_KEY = "Tags";
    private static final String LEADERBOARD_KEY = "Leaderboard";

    // --- 1. Implementation using HASH (for storing objects) ---

    @PostMapping("/products")
    public Product saveProduct(@RequestBody Product product) {
        redisTemplate.opsForHash().put(PRODUCT_HASH_KEY, String.valueOf(product.getId()), product);
        return product;
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable int id) {
        return (Product) redisTemplate.opsForHash().get(PRODUCT_HASH_KEY, String.valueOf(id));
    }

    // --- 2. Implementation using STRING (for simple caching) ---

    @PostMapping("/cache")
    public String cacheValue(@RequestParam String key,
                             @RequestParam String value,
                             @RequestParam(required = false,defaultValue = "5") Long ttl) { // Added optional TTL parameter
        // opsForValue() is for standard Redis String operations.
        if (ttl != null) {
            // If a TTL (in seconds) is provided, set the key with an expiration.
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
            return "Value cached successfully for " + ttl + " seconds.";
        } else {
            // Otherwise, set it without an expiration.
            redisTemplate.opsForValue().set(key, value);
            return "Value cached successfully.";
        }
    }

    @GetMapping("/cache/{key}")
    public String getCachedValue(@PathVariable String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    // --- 3. Implementation using LIST (as a FIFO Queue) ---

    @PostMapping("/queue")
    public String pushToQueue(@RequestParam String message) {
        redisTemplate.opsForList().leftPush(MESSAGE_QUEUE_KEY, message);
        return "Message added to queue";
    }

    @DeleteMapping("/queue")
    public Object popFromQueue() {
        return redisTemplate.opsForList().rightPop(MESSAGE_QUEUE_KEY);
    }

    // --- 4. Implementation using SET (for unique values) ---

    @PostMapping("/tags")
    public String addTag(@RequestParam String tag) {
        redisTemplate.opsForSet().add(TAGS_SET_KEY, tag);
        return "Tag added";
    }

    @GetMapping("/tags")
    public Set<Object> getAllTags() {
        return redisTemplate.opsForSet().members(TAGS_SET_KEY);
    }

    // --- 5. Implementation using SORTED SET (for a Leaderboard) ---

    @PostMapping("/leaderboard")
    public ResponseEntity<String> updateLeaderboard(@RequestParam String username, @RequestParam double score) {
        // opsForZSet() provides methods for Sorted Sets.
        // We add the user with their score. If the user already exists, their score is updated.
        redisTemplate.opsForZSet().add(LEADERBOARD_KEY, username, score);
        return ResponseEntity.ok("Score updated for " + username);
    }

    @GetMapping("/leaderboard")
    public Set<ZSetOperations.TypedTuple<Object>> getLeaderboard(@RequestParam(defaultValue = "10") int top) {
        // reverseRangeWithScores gets a range of members, sorted from highest score to lowest.
        // We get the top N players (from index 0 to top-1).
        return redisTemplate.opsForZSet().reverseRangeWithScores(LEADERBOARD_KEY, 0, top - 1);
    }

    // --- 6. Implementation using Pub/Sub (Publish/Subscribe) ---

    @PostMapping("/publish")
    public ResponseEntity<String> publishMessage(@RequestParam String message) {
        messagePublisher.publish(message);
        return ResponseEntity.ok("Message '" + message + "' published to channel '" + RedisIntroApplication.CHAT_CHANNEL + "'.");
    }
}