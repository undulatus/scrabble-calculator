package com.bryanbatanes.scrabble.controller;

import com.bryanbatanes.scrabble.dto.LetterPointsResponse;
import com.bryanbatanes.scrabble.dto.SaveScoreRequest;
import com.bryanbatanes.scrabble.model.Scores;
import com.bryanbatanes.scrabble.service.ScoringService;
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/letterpoints")
    public LetterPointsResponse fetchLetterPoints() {
        log.info("CALL >> get /letterpoints");
        return scoringService.fetchScoringSystem();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/letterpoints/setup")
    public String setupScoringSystem(@RequestBody Map<Integer,
                List<Character>> request) {
        log.info("CALL >> post /letterpoints/setup");
        scoringService.setupPoints(request);
        return "success";
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/letterpoints/remove")
    public String removeScoringSystem() {
        log.info("CALL >> delete /letterpoints/remove");
        scoringService.removePointsData();
        return "success";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/scores")
    public List<Scores> fetchAllScores() {
        log.info("CALL >> get /scores");
        return scoringService.fetchAllScores();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/scores/top10")
    public List<Scores> fetchAllTop10Scores() {
        log.info("CALL >> get /scores/top10");
        return scoringService.fetchTop10Scores();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/scores")
    public Long saveScore(@Valid @RequestBody SaveScoreRequest request) {
        log.info("CALL >> post /scores");
        return scoringService.saveScore(request);
    }

}
