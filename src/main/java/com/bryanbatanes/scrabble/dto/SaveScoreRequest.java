package com.bryanbatanes.scrabble.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SaveScoreRequest {

    @NotBlank(message = "input word is missing")
    @Size(max=10, message = "please enter up to 10 characters only")
    private String word;

}
