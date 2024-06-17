package com.bryanbatanes.scrabble.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class LetterPointsMap {

    private Integer points;
    private List<Character> letters;

}
