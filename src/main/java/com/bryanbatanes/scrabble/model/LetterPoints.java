package com.bryanbatanes.scrabble.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LetterPoints {

    @Id
    private Long id;

    private Integer points;

    private Character letter;

    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
