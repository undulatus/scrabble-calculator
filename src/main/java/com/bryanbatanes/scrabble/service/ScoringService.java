package com.bryanbatanes.scrabble.service;

import com.bryanbatanes.scrabble.dto.LetterPointsMap;
import com.bryanbatanes.scrabble.dto.LetterPointsResponse;
import com.bryanbatanes.scrabble.dto.SaveScoreRequest;
import com.bryanbatanes.scrabble.model.LetterPoints;
import com.bryanbatanes.scrabble.model.LetterPointsRepository;
import com.bryanbatanes.scrabble.model.Scores;
import com.bryanbatanes.scrabble.model.ScoresRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ScoringService {

    private LetterPointsRepository pointsRepo;

    private ScoresRepository scoresRepo;

    private JdbcTemplate jdbcTemplate;

    public Integer calculateScore(String word) {
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
        pointSet = pointSet.stream().sorted().collect(Collectors.toSet());

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

        String word = request.getWord().toUpperCase(Locale.ENGLISH);
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
        log.info("IN >> fetching all scores");
        Sort sorted = Sort.by(
                Sort.Order.desc("score"),
                Sort.Order.desc("createdDate")
        );

        List<Scores> allScores = scoresRepo.findAll(sorted);
        log.info("OUT >> fetching all scores [{}]", allScores);
        return allScores;
    }

    public List<Scores> fetchTop10Scores() {
        log.info("IN >> fetching top 10 scores");
        List<Scores> allScores = fetchAllScores();

        List<Scores> top10Scores = new ArrayList<>();
        int limit = 10;
        int iteration = 0, prevPoint = 0, curPoint = 0;
        while(iteration <= limit && iteration <= allScores.size()-1) {
            Scores score = allScores.get(iteration);
            if(iteration == 0) {
                prevPoint = score.getScore();
            }
            curPoint = score.getScore();
            //continue adding until minimum 10 is achieved
            if(iteration <= 9) {
                top10Scores.add(score);
            }
            //check if limit needs to increase for tie breakers and add to list if tied to previous
            if(iteration > 9 && prevPoint == curPoint) {
                limit++;
                top10Scores.add(score);
            }
            //set curPoint as new prevPoint on next iteration
            prevPoint = curPoint;
            iteration++;
        }

        log.info("OUT >> fetching top 10 scores count is [{}], the object [{}] ",top10Scores.size(), top10Scores);
        return top10Scores;
    }

    @Transactional
    public void removePointsData() {
        pointsRepo.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE letter_points_id_seq RESTART WITH 1");
    }

    /**
     * call once for setting up point system
     * @param pointSetup
     */
    @Transactional
    public void setupPoints(Map<Integer, List<Character>> pointSetup) {
        log.info("IN >> Setting up point system [{}]", pointSetup);
        removePointsData();

        pointSetup.keySet().forEach(point -> {
            pointSetup.get(point).forEach(letter -> {
                LetterPoints entry = LetterPoints.builder()
                        .points(point)
                        .letter(Character.toUpperCase(letter))
                        .build();
                pointsRepo.save(entry);
            });
        });
        log.info("OUT >> Successfully setup point system");
    }
}
