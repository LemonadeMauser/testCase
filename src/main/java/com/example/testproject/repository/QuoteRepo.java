package com.example.testproject.repository;

import com.example.testproject.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepo extends JpaRepository<Quote, Long> {
    @Query(value = "SELECT q FROM Quote q ORDER BY RAND() LIMIT 1")
    Quote findRandomQuote();

    List<Quote> findFirst10ByOrderByRatingDesc();

    List<Quote> findFirst10ByOrderByRatingAsc();

    Optional<Quote> findFirstByOrderByLastUpdateTimeDesc();

    Optional<Quote> findByText(String text);
}
