package com.bryanbatanes.scrabble.service;

import com.bryanbatanes.scrabble.dto.LetterPointsMap;
import com.bryanbatanes.scrabble.dto.LetterPointsResponse;
import com.bryanbatanes.scrabble.dto.SaveScoreRequest;
import com.bryanbatanes.scrabble.model.LetterPoints;
import com.bryanbatanes.scrabble.model.LetterPointsRepository;
import com.bryanbatanes.scrabble.model.Scores;
import com.bryanbatanes.scrabble.model.ScoresRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ScoringService {

    private LetterPointsRepository pointsRepo;

    private ScoresRepository scoresRepo;

    private Integer calculateScore(String word) {
        if(StringUtils.isBlank(word)) {
            log.error("Invalid user input [{}]", word);
            throw new RuntimeException("User Input is Invalid");
        }

        List<LetterPoints> pointList = pointsRepo.findAll();

        Map<Character, Integer> pointMap = CollectionUtils.emptyIfNull(pointList).stream()
                .collect(Collectors.toMap(LetterPoints::getLetter, LetterPoints::getPoints));

        return word.chars()
                .map(letter -> {
                    Character key = (char) letter;
                    Integer point = pointMap.get(key);
                    return point == null ? 0 : point;
                }).sum();
    }

    public LetterPointsResponse fetchScoringSystem() {
        log.info("IN >> fetching scoring system");
        Set<Integer> pointSet = pointsRepo.findDistinctPoints();

        List<LetterPointsMap> responseEntries = new ArrayList<>();

        for(Integer point : pointSet) {
            List<LetterPoints> letterPointsList = pointsRepo.findByPoints(point);
            List<Character> letters = letterPointsList.stream()
                    .map(LetterPoints::getLetter).toList();
            responseEntries.add(LetterPointsMap.builder()
                    .points(point)
                    .letters(letters)
                    .build());
        }
        LetterPointsResponse response = LetterPointsResponse.builder()
                .letterPoints(responseEntries)
                .build();
        log.info("OUT >> fetching scoring system [{}]", response);
        return response;
    }

    @Transactional
    public Long saveScore(SaveScoreRequest request) {
        String word = request.getWord();
        log.info("IN >> Start saving score for word [{}]", word);
        Integer score = calculateScore(word);
        log.info("Score calculated [{}]", score);
        Scores toSave = Scores.builder()
                .score(score)
                .word(word)
                .build();

        log.info("Saving score entry [{}]", toSave);
        Long savedId = scoresRepo.save(toSave).getId();
        log.info("OUT >> Saving success id [{}]", savedId);
        return savedId;
    }

    public List<Scores> fetchAllScores() {
        log.info("IN >> fetching current scores");
        Sort sorted = Sort.by(
                Sort.Order.desc("score"),
                Sort.Order.desc("createdDate")
        );

        List<Scores> allScores = scoresRepo.findAll(sorted);
        log.info("OUT >> fetching all scores [{}]", allScores);
        return allScores;
    }

    /**
     * call once for setting up point system
     * @param pointSetup
     */
    @Transactional
    public void setupPoints(Map<Integer, List<Character>> pointSetup) {
        log.info("IN >> Setting up point system [{}]", pointSetup);
        pointsRepo.deleteAll();
        pointSetup.keySet().forEach(point -> {
            pointSetup.get(point).forEach(letter -> {
                LetterPoints entry = LetterPoints.builder()
                        .points(point)
                        .letter(letter)
                        .build();
                pointsRepo.save(entry);
            });
        });
        log.info("OUT >> Successfully setup point system");
    }
}
