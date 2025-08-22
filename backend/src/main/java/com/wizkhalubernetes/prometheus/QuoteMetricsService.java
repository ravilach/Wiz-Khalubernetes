package com.wizkhalubernetes.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

/**
 * Service for Prometheus counters tracking database CRUD operations in H2 and MongoDB.
 * Exposes counters for create, read, update, and delete actions for both databases.
 */
@Service
public class QuoteMetricsService {
    private final Counter h2CreateCounter;
    private final Counter h2ReadCounter;
    private final Counter h2UpdateCounter;
    private final Counter h2DeleteCounter;
    private final Counter mongoCreateCounter;
    private final Counter mongoReadCounter;
    private final Counter mongoUpdateCounter;
    private final Counter mongoDeleteCounter;

    /**
     * Initializes Prometheus counters for H2 and MongoDB CRUD operations.
     * @param registry MeterRegistry for Prometheus integration
     */
    public QuoteMetricsService(MeterRegistry registry) {
        h2CreateCounter = registry.counter("db_h2_create_total");
        h2ReadCounter = registry.counter("db_h2_read_total");
        h2UpdateCounter = registry.counter("db_h2_update_total");
        h2DeleteCounter = registry.counter("db_h2_delete_total");
        mongoCreateCounter = registry.counter("db_mongo_create_total");
        mongoReadCounter = registry.counter("db_mongo_read_total");
        mongoUpdateCounter = registry.counter("db_mongo_update_total");
        mongoDeleteCounter = registry.counter("db_mongo_delete_total");
    }

    /** Increment H2 create counter */
    public void incrementH2Create() { h2CreateCounter.increment(); }
    /** Increment H2 read counter */
    public void incrementH2Read() { h2ReadCounter.increment(); }
    /** Increment H2 update counter */
    public void incrementH2Update() { h2UpdateCounter.increment(); }
    /** Increment H2 delete counter */
    public void incrementH2Delete() { h2DeleteCounter.increment(); }

    /** Increment MongoDB create counter */
    public void incrementMongoCreate() { mongoCreateCounter.increment(); }
    /** Increment MongoDB read counter */
    public void incrementMongoRead() { mongoReadCounter.increment(); }
    /** Increment MongoDB update counter */
    public void incrementMongoUpdate() { mongoUpdateCounter.increment(); }
    /** Increment MongoDB delete counter */
    public void incrementMongoDelete() { mongoDeleteCounter.increment(); }
}
