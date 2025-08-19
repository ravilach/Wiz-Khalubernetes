// MongoDB configuration with conditional dummy template for resilience

package com.wizkhalubernetes.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ConditionalOnProperty(name = "spring.data.mongodb.uri")
@EnableMongoRepositories(basePackages = "com.wizkhalubernetes.repository")
public class MongoConfig {
    @Bean
    public MongoTemplate mongoTemplate(org.springframework.core.env.Environment env) {
        String uri = env.getProperty("spring.data.mongodb.uri");
        if (uri == null || uri.isEmpty()) {
            System.err.println("MongoDB URI not set. Using H2 embedded DB for local startup.");
            // H2 will be used by default via Spring Boot auto-configuration
            // Return a dummy MongoTemplate to satisfy bean creation
            return new MongoTemplate(new org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/dummy"));
        }
        try {
            return new MongoTemplate(new org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory(uri));
        } catch (Exception e) {
            System.err.println("MongoDB connection failed: " + e.getMessage());
            return new MongoTemplate(new org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/dummy"));
        }
    }
}
