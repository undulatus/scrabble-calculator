package com.bryanbatanes.scrabble.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LetterPointsResponse {

    List<LetterPointsMap> letterPoints;
}
