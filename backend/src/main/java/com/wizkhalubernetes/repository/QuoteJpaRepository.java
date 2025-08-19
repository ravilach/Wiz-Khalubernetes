package com.wizkhalubernetes.repository;

import com.wizkhalubernetes.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteJpaRepository extends JpaRepository<Quote, Long> {
}
