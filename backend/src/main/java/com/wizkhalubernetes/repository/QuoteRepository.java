// MongoDB repository interface for Quote documents
package com.wizkhalubernetes.repository;

import com.wizkhalubernetes.model.Quote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuoteRepository extends MongoRepository<Quote, String> {
}
