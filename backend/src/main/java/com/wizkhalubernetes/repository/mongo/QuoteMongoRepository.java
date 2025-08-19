package com.wizkhalubernetes.repository.mongo;

import com.wizkhalubernetes.model.mongo.QuoteMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
@org.springframework.stereotype.Repository("mongoQuoteRepository")
public interface QuoteMongoRepository extends MongoRepository<QuoteMongo, Long> {
	// ...existing code...
}
