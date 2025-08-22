package com.wizkhalubernetes.repository.mongo;

import com.wizkhalubernetes.model.mongo.QuoteMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
/**
 * MongoDB repository interface for QuoteMongo entities.
 * Extends Spring Data MongoRepository for CRUD operations.
 */
@org.springframework.stereotype.Repository("mongoQuoteRepository")
public interface QuoteMongoRepository extends MongoRepository<QuoteMongo, String> {
	// ...existing code...
}
