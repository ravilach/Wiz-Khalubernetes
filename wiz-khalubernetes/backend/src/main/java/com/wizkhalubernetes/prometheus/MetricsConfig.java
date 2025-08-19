package com.wizkhalubernetes.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.prometheus.PrometheusMeterRegistry;

@Configuration
public class MetricsConfig {
    @Bean
    public MeterRegistry meterRegistry(PrometheusMeterRegistry prometheusMeterRegistry) {
        return prometheusMeterRegistry;
    }
}
