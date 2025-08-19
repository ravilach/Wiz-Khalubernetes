package com.wizkhalubernetes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.beans.factory.annotation.Value;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

@SpringBootApplication
public class WizKhalubernetesApplication {
    public static void main(String[] args) {
        SpringApplication.run(WizKhalubernetesApplication.class, args);
    }

    // Fallback dummy MongoTemplate bean to prevent app exit if MongoDB is unavailable
    @Bean
    @ConditionalOnMissingBean(MongoTemplate.class)
    public MongoTemplate dummyMongoTemplate() {
        // Return a MongoTemplate that points to a non-existent DB, but doesn't throw on creation
        try {
            return new MongoTemplate(new SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/dummy"));
        } catch (Exception e) {
            // Return a no-op MongoTemplate (will throw on actual DB operations, but allows app to start)
            return null;
        }
    }
}
