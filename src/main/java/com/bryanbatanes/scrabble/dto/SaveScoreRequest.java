package com.bryanbatanes.scrabble.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SaveScoreRequest {

    private String word;

}
