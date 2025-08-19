
package com.wizkhalubernetes.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
// Removed unused import

@Configuration
@ConditionalOnProperty(name = "spring.data.mongodb.uri")
@EnableMongoRepositories(basePackages = "com.wizkhalubernetes.repository")
public class MongoConfig {
    @Bean
    public MongoTemplate mongoTemplate(org.springframework.core.env.Environment env) {
        String uri = env.getProperty("spring.data.mongodb.uri");
        if (uri == null || uri.isEmpty()) {
            System.err.println("MongoDB URI not set. Using dummy MongoTemplate. DB operations will fail gracefully.");
            // Use a dummy factory with a local URI that won't connect, but allows bean creation
            return new MongoTemplate(new org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/dummy"));
        }
        try {
            return new MongoTemplate(new org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory(uri));
        } catch (Exception e) {
            // Log and return a dummy MongoTemplate to allow app startup
            System.err.println("MongoDB connection failed: " + e.getMessage());
            return new MongoTemplate(new org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/dummy"));
        }
    }
}
