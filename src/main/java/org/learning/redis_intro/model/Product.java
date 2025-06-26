package org.learning.redis_intro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
// The Serializable interface is crucial for the JdkSerializationRedisSerializer to work.
public class Product implements Serializable {
    private int id;
    private String name;
    private double price;
}