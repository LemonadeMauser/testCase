package com.example.testproject.repository;

import com.example.testproject.entity.Quote;
import com.example.testproject.entity.Rating;
import com.example.testproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RatingRepo extends JpaRepository<Rating, Long> {
    Optional<Rating> findByQuote(Quote quote);

    Rating findFirstByQuoteOrderByUpdateTimeDesc(Quote quote);

    @Query("SELECT r.ratedUsers FROM Rating r WHERE r.id = :ratingId")
    Set<User> findRatedUsersByRatingId(@Param("ratingId") Long ratingId);

    List<Rating> findAllByQuoteOrderByUpdateTimeDesc(Quote quote);
}
