// Main Spring Boot application class for Wiz Khalubernetes
// Includes fallback MongoTemplate bean for resilience
package com.wizkhalubernetes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.context.annotation.Import;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

class WizKhalubernetesConfigSelector implements ImportSelector, EnvironmentAware {
    private Environment environment;
    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
    public @NonNull String[] selectImports(@NonNull AnnotationMetadata importingClassMetadata) {
        String remoteDb = environment != null ? environment.getProperty("REMOTE_DB", "false") : "false";
        if ("true".equalsIgnoreCase(remoteDb)) {
            return new String[]{"com.wizkhalubernetes.config.mongo.MongoConfig"};
        } else {
            return new String[]{"com.wizkhalubernetes.config.h2.H2Config"};
        }
    }
}
@SpringBootApplication
@Import(WizKhalubernetesConfigSelector.class)
public class WizKhalubernetesApplication {
    public static void main(String[] args) {
        SpringApplication.run(WizKhalubernetesApplication.class, args);
    }

}
