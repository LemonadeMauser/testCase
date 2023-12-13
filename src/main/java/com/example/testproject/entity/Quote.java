package com.example.testproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quote")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String text;
    @ManyToOne
    @JoinColumn(name = "author_id")
    User author;
    LocalDateTime creationDateTime;
    LocalDateTime lastUpdateTime;
    Long rating;
}
