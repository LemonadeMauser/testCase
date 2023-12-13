package com.example.testproject.service;

import com.example.testproject.dto.RatingDto;
import com.example.testproject.entity.Quote;
import com.example.testproject.entity.Rating;
import com.example.testproject.entity.User;
import com.example.testproject.exception.AccessEx;
import com.example.testproject.repository.RatingRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepo repo;
    private final ModelMapper mapper;

    protected void createRating(User author, Quote quote, Long score) {
        Rating rating = buildRating(author, quote, score);
        repo.save(rating);
    }

    protected Optional<Rating> getRatingByQuote(Quote quote) {
        return repo.findByQuote(quote);
    }

    public void changeRating(User author, Quote quote, Long score) {
        Rating rating = repo.findFirstByQuoteOrderByUpdateTimeDesc(quote);
        Set<User> ratedUsers = repo.findRatedUsersByRatingId(rating.getId());
        if (ratedUsers.contains(author)) {
            throw new AccessEx("Each user can rate only once.");
        }
        ratedUsers.add(author);
        rating.setRatedUsers(ratedUsers);
        rating.setRating(rating.getRating() + score);
        rating.setUpdateTime(LocalDateTime.now());
        repo.save(rating);
    }


    private Rating buildRating(User author, Quote quote, Long score) {
        return Rating.builder()
                .rating(score)
                .quote(quote)
                .ratedUsers(Collections.singleton(author))
                .updateTime(LocalDateTime.now())
                .build();
    }

    public List<RatingDto> findAllRatingsChangesByQuoteId(Quote quote) {
        List<Rating> ratings = repo.findAllByQuoteOrderByUpdateTimeDesc(quote);
        return ratings.stream()
                .map(rating -> mapper.map(rating, RatingDto.class))
                .collect(Collectors.toList());
    }
}
