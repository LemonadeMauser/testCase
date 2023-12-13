package com.example.testproject.service;

import com.example.testproject.dto.QuoteDto;
import com.example.testproject.dto.QuoteDtoWithStats;
import com.example.testproject.dto.RatingDto;
import com.example.testproject.entity.Quote;
import com.example.testproject.entity.User;
import com.example.testproject.exception.AccessEx;
import com.example.testproject.exception.QuotNotFoundEx;
import com.example.testproject.exception.ValidationEx;
import com.example.testproject.repository.QuoteRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuoteService {
    private final QuoteRepo repo;
    private final UserService userService;
    private final ModelMapper mapper;
    private final RatingService ratingService;

    @Transactional
    public ResponseEntity<QuoteDto> createQuote(Long authorId, String text) {
        validator(text);
        Quote quote = Quote.builder()
                .text(text.trim())
                .creationDateTime(LocalDateTime.now())
                .author(getUserIfExistOrThrow(authorId))
                .lastUpdateTime(LocalDateTime.now())
                .rating(0L)
                .build();
        QuoteDto dto = entityToDto(repo.save(quote));

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    public ResponseEntity<QuoteDto> getRandomQuote() {
        QuoteDto dto = entityToDto(repo.findRandomQuote());
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    public ResponseEntity<QuoteDto> getLastUpdatedQuote() {
        Optional<Quote> quote = repo.findFirstByOrderByLastUpdateTimeDesc();
        if (quote.isEmpty()){
            throw new QuotNotFoundEx("There are no quotes at the moment.");
        }
        QuoteDto dto = entityToDto(quote.get());
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Transactional
    public ResponseEntity<QuoteDto> updateQuoteById(Long authorId, Long quoteId, String updatedText) {
        Quote quote = getQuoteByIdOrThrow(quoteId);
        getUserIfExistOrThrow(authorId);
        if (!quote.getAuthor().getId().equals(authorId)) {
            throw new AccessEx("Only the author can change the quote.");
        }
        validator(updatedText);
        quote.setText(updatedText);
        quote.setLastUpdateTime(LocalDateTime.now());
        QuoteDto dto = entityToDto(repo.save(quote));
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Transactional
    public void addRating(Long authorId, Long quoteId, String score) {
        User author = getUserIfExistOrThrow(authorId);
        Quote quote = getQuoteByIdOrThrow(quoteId);
        Long rating = Long.parseLong(score);
        if (ratingService.getRatingByQuote(quote).isEmpty()) {
            ratingService.createRating(author, quote, rating);
        } else {
            ratingService.changeRating(author, quote, rating);
        }
    }

    @Transactional
    public void deleteById(Long quoteId, Long authorId) {
        getUserIfExistOrThrow(authorId);
        Quote quote = getQuoteByIdOrThrow(quoteId);
        if (!quote.getAuthor().getId().equals(authorId)) {
            throw new AccessEx("Only the author can delete the quote.");
        }
        repo.deleteById(quoteId);
    }

    @Transactional
    public ResponseEntity<List<QuoteDtoWithStats>> getAllQuotesSortedByRating(Boolean isTop) {
        List<Quote> quotes;
        if (isTop) {
            quotes = repo.findFirst10ByOrderByRatingDesc();
        } else {
            quotes = repo.findFirst10ByOrderByRatingAsc();
        }
        List<QuoteDtoWithStats> quotesDto = addStatsForDto(quotes);
        return ResponseEntity.status(HttpStatus.OK).body(quotesDto);
    }

    private List<QuoteDtoWithStats> addStatsForDto(List<Quote> quotes) {
        QuoteDtoWithStats stats;
        List<QuoteDtoWithStats> statsList = new ArrayList<>();
        for (Quote quote : quotes) {
            stats = mapper.map(quote, QuoteDtoWithStats.class);
            stats.setAuthor(quote.getAuthor().getUsername());
            stats.setRatingStats(findAllRatingsChanges(quote.getId()));
        }
        return statsList;
    }

    private List<RatingDto> findAllRatingsChanges(Long quoteID) {
        Quote quote = getQuoteByIdOrThrow(quoteID);
        return ratingService.findAllRatingsChangesByQuoteId(quote);
    }

    private Quote getQuoteByIdOrThrow(Long quoteId) {
        return repo.findById(quoteId).orElseThrow(() -> new QuotNotFoundEx("Quote with " +
                "quoteId=" + quoteId + "does not exist"));
    }

    private User getUserIfExistOrThrow(Long userId) {
        return userService.getIfExistOrThrow(userId);
    }

    private QuoteDto entityToDto(Quote quote) {
        QuoteDto dto = mapper.map(quote, QuoteDto.class);
        dto.setAuthor(quote.getAuthor().getUsername());
        dto.setUpdateTime(quote.getLastUpdateTime());
        return dto;
    }

    private void validator(String text) {
        if (text.trim().isEmpty()) {
            throw new ValidationEx("Quote cannot be empty.");
        }
        Optional<Quote> quote = repo.findByText(text);
        if (quote.isPresent()) {
            throw new ValidationEx("This quote already exists.");
        }
    }
}
