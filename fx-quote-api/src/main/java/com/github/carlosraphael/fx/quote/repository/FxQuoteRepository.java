package com.github.carlosraphael.fx.quote.repository;

import com.github.carlosraphael.fx.quote.domain.FxQuote;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

public interface FxQuoteRepository extends ReactiveCassandraRepository<FxQuote, String> {
}
