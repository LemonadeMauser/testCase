package com.example.testproject.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuoteDto {
    Long id;
    String text;
    String author;
    LocalDateTime updateTime;
    Long rating;
}

