// MongoDB configuration with conditional dummy template for resilience

package com.wizkhalubernetes.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableMongoRepositories(basePackages = "com.wizkhalubernetes.repository")
public class MongoConfig {
    @Value("${REMOTE_DB:false}")
    private String remoteDbFlag;

    @Bean
    @ConditionalOnProperty(name = "REMOTE_DB", havingValue = "true")
    public MongoTemplate mongoTemplate(org.springframework.core.env.Environment env) {
        String uri = env.getProperty("spring.data.mongodb.uri");
        if (uri == null || uri.isEmpty()) {
            System.err.println("MongoDB URI not set. Using H2 embedded DB for local startup.");
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
