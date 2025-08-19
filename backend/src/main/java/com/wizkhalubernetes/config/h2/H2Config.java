package com.wizkhalubernetes.config.h2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@ConditionalOnProperty(name = "REMOTE_DB", havingValue = "false", matchIfMissing = true)
@EnableJpaRepositories(basePackages = "com.wizkhalubernetes.repository.h2")
public class H2Config {
    @Value("${REMOTE_DB:false}")
    private String remoteDbFlag;

    @PostConstruct
    public void logRemoteDbFlag() {
        System.out.println("[DEBUG] REMOTE_DB value at startup (H2Config): " + remoteDbFlag);
        String envValue = System.getenv("REMOTE_DB");
        System.out.println("[DEBUG] REMOTE_DB from System.getenv: " + envValue);
    }
    // No beans needed, just enables JPA repositories when REMOTE_DB is false
}
