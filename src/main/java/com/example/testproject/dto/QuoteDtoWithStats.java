package com.example.testproject.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuoteDtoWithStats {
    Long id;
    String text;
    String author;
    LocalDateTime updateTime;
    Long rating;
    List<RatingDto> ratingStats;
}
