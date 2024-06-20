package com.bryanbatanes.scrabble.controller;

import com.bryanbatanes.scrabble.dto.LetterPointsResponse;
import com.bryanbatanes.scrabble.dto.SaveScoreRequest;
import com.bryanbatanes.scrabble.model.Scores;
import com.bryanbatanes.scrabble.service.ScoringService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/scrabble")
public class ScoringController {

    private ScoringService scoringService;

    @Operation(summary = "Fetch letters and points scoring system")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/letterpoints")
    public LetterPointsResponse fetchLetterPoints() {
        log.info("CALL >> get /letterpoints");
        return scoringService.fetchScoringSystem();
    }

    @Operation(summary = "Create letters and points scoring system")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/letterpoints/setup")
    public String setupScoringSystem(@RequestBody Map<Integer,
                List<Character>> request) {
        log.info("CALL >> post /letterpoints/setup");
        scoringService.setupPoints(request);
        return "success";
    }

    @Operation(summary = "Remove existing letters and points scoring system")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/letterpoints/remove")
    public String removeScoringSystem() {
        log.info("CALL >> delete /letterpoints/remove");
        scoringService.removePointsData();
        return "success";
    }

    @Operation(summary = "Fetch all scores saved")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/scores")
    public List<Scores> fetchAllScores() {
        log.info("CALL >> get /scores");
        return scoringService.fetchAllScores();
    }

    @Operation(summary = "Calculate score of the word")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/scores/calculate")
    public Integer calculateScore(@RequestParam(required = true) String word) {
        log.info("CALL >> get /scores/calculate");
        return scoringService.calculateScore(word);
    }

    @Operation(summary = "Fetch all top 10 scores in descending points order")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/scores/top10")
    public List<Scores> fetchAllTop10Scores() {
        log.info("CALL >> get /scores/top10");
        return scoringService.fetchTop10Scores();
    }

    @Operation(summary = "Save the current word's score")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/scores")
    public Long saveScore(@Valid @RequestBody SaveScoreRequest request) {
        log.info("CALL >> post /scores");
        return scoringService.saveScore(request);
    }
}
