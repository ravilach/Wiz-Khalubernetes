package com.wizkhalubernetes.repository.h2;

import com.wizkhalubernetes.model.h2.QuoteH2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository("h2QuoteRepository")
public interface QuoteH2Repository extends JpaRepository<QuoteH2, Long> {
}
