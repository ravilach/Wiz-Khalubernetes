package com.wizkhalubernetes.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ConditionalOnProperty(name = "spring.data.mongodb.uri")
@EnableMongoRepositories(basePackages = "com.wizkhalubernetes.repository")
public class MongoConfig {
    // This configuration enables MongoDB repositories only if the connection string is present.
}
