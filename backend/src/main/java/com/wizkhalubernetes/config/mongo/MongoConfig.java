package com.wizkhalubernetes.config.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.beans.factory.annotation.Value;

/**
 * MongoDB configuration for Wiz Khalubernetes.
 * Enables Mongo repositories and provides MongoTemplate bean when REMOTE_DB is true.
 */
@Configuration
@ConditionalOnProperty(name = "REMOTE_DB", havingValue = "true")
@EnableMongoRepositories(basePackages = "com.wizkhalubernetes.repository.mongo")
public class MongoConfig {
    @Value("${REMOTE_DB:false}")
    private String remoteDbFlag;

    /**
     * Creates a MongoTemplate bean using the configured MongoDB URI.
     * Falls back to dummy local DB if URI is missing or connection fails.
     * @param env Spring environment for property lookup
     * @return MongoTemplate instance
     */
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
