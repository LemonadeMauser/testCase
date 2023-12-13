package com.example.testproject.controller;


import com.example.testproject.dto.QuoteDto;
import com.example.testproject.dto.QuoteDtoWithStats;
import com.example.testproject.service.QuoteService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/quotes")
public class QuoteController {
    private final QuoteService service;

    @PostMapping()
    public ResponseEntity<QuoteDto> createQuote(@RequestHeader("X-SMedia-User-Id") Long senderId,
                                                @RequestBody @NotBlank String quote) {
        log.info("A request for a create new quote has been received." +
                "sender id=" + senderId + ".");
        return service.createQuote(senderId, quote);
    }

    @GetMapping("/random")
    public ResponseEntity<QuoteDto> getRandomQuote() {
        log.info("A request for a random quote has been received.");
        return service.getRandomQuote();
    }

    @GetMapping
    public ResponseEntity<QuoteDto> getLastUpdatedQuote() {
        log.info("A request for a last quote has been received.");
        return service.getLastUpdatedQuote();
    }


    @PatchMapping("/{quoteId}")
    public ResponseEntity<QuoteDto> updateQuote(@RequestHeader("X-SMedia-User-Id") Long senderId,
                                                @PathVariable Long quoteId,
                                                @RequestBody @NotBlank String updatedQuote) {
        log.info("A request for update quote with quoteId =" + quoteId + "has been received." +
                "Sender id=" + senderId);
        return service.updateQuoteById(senderId, quoteId, updatedQuote);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/rating/{quoteId}")
    public void addRatingById(@RequestHeader("X-SMedia-User-Id") Long senderId,
                              @PathVariable Long quoteId,
                              @RequestBody @Pattern(regexp = "1|-1") String rating) {
        log.info("Request to add a rating to quote with quoteId=" + quoteId + "from sender with id="
                + senderId + ".");
        service.addRating(senderId, quoteId, rating);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{quoteId}")
    public void deleteQuoteById(@RequestHeader("X-SMedia-User-Id") Long senderId,
                                @PathVariable Long quoteId) {
        log.info("Request to delete a quote with id=" + quoteId + " has been received."
                + "sender Id =" + senderId);
        service.deleteById(quoteId, senderId);
    }

    @GetMapping("/list")
    public ResponseEntity<List<QuoteDtoWithStats>> getQuotesSortedByRating(@RequestParam Boolean isBest) {
        log.info("A request for 10 quotes has been received."
                + "the 10 best quotes? - " + isBest + ".");
        return service.getAllQuotesSortedByRating(isBest);
    }
}
