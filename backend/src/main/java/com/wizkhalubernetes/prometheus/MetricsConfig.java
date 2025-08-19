package com.wizkhalubernetes.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;

@Configuration
public class MetricsConfig {

    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        try {
            return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        } catch (Exception e) {
            System.err.println("PrometheusMeterRegistry creation failed: " + e.getMessage());
            return null;
        }
    }

    @Bean
    public MeterRegistry meterRegistry(PrometheusMeterRegistry prometheusMeterRegistry) {
        if (prometheusMeterRegistry == null) {
            System.err.println("MeterRegistry creation failed: PrometheusMeterRegistry is null.");
            return null;
        }
        try {
            return prometheusMeterRegistry;
        } catch (Exception e) {
            System.err.println("MeterRegistry creation failed: " + e.getMessage());
            return null;
        }
    }
}
