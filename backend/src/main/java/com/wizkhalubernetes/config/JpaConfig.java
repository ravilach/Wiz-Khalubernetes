package com.wizkhalubernetes.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(name = "REMOTE_DB", havingValue = "false", matchIfMissing = true)
@EnableJpaRepositories(basePackages = "com.wizkhalubernetes.repository")
public class JpaConfig {
    // No beans needed, just enables JPA repositories when REMOTE_DB is false
}
