package com.wizkhalubernetes.prometheus;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator for exposing node/system health metrics.
 * Adds free and total memory details to the /actuator/health endpoint and Prometheus metrics.
 */
@Component
public class NodeHealthIndicator implements HealthIndicator {
    /**
     * Returns node health status and memory details.
     * @return Health object with free and total memory
     */
    @Override
    public Health health() {
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        return Health.up()
            .withDetail("freeMemory", freeMemory)
            .withDetail("totalMemory", totalMemory)
            .build();
    }
}
