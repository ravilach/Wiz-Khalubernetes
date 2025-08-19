package com.wizkhalubernetes.repository;

import com.wizkhalubernetes.model.Quote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuoteMongoRepository extends MongoRepository<Quote, Long> {
}
