package com.bryanbatanes.scrabble.service;

import com.bryanbatanes.scrabble.dto.LetterPointsResponse;
import com.bryanbatanes.scrabble.dto.SaveScoreRequest;
import com.bryanbatanes.scrabble.model.LetterPoints;
import com.bryanbatanes.scrabble.model.LetterPointsRepository;
import com.bryanbatanes.scrabble.model.Scores;
import com.bryanbatanes.scrabble.model.ScoresRepository;
import com.bryanbatanes.scrabble.util.TestUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
@SuppressWarnings("unchecked")
public class ScoringServiceTest {

    @Autowired
    private ScoringService scoringService;

    @MockBean
    private LetterPointsRepository pointsRepo;

    @MockBean
    private ScoresRepository scoresRepo;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    private static Map<Integer, List<Character>> letterPointsRequest;
    private static SaveScoreRequest saveScoreRequest;
    private static LetterPointsResponse letterPointsResponse;
    private static List<Scores> scoresResponse;
    private static List<Scores> scoresTop10Response;

    // ####### CONSTANTS ############

    private static final String WORD = "word";
    private static final Integer EIGHT = 8;
    private static List<LetterPoints> LIST_LETTER_POINTS;
    private static Set<Integer> SET_POINTS;
    private static Sort SCORES_SORTED;
    private static final String RESET_SEQ =
            "ALTER SEQUENCE letter_points_id_seq RESTART WITH 1";

    @BeforeAll
    static void setup() throws IOException {
        TypeReference<List<LetterPoints>> listLetterPointsType = new TypeReference<>() {};
        TypeReference<List<Scores>> listScoresType = new TypeReference<>() {};
        TypeReference<Map<Integer, List<Character>>> pointsSetupType = new TypeReference<>() {};

        LIST_LETTER_POINTS = TestUtil
                .getObjectFromJson("dbmodel/letterpoint_entity.json", listLetterPointsType);

        SET_POINTS = Set.of(1,2,3,4,6,8,10);
        SCORES_SORTED = Sort.by(
                Sort.Order.desc("score"),
                Sort.Order.desc("createdDate")
        );

        letterPointsRequest = TestUtil.getObjectFromJson("request/letterpoint.json", pointsSetupType);
        saveScoreRequest = SaveScoreRequest.builder().word(WORD).build();

        letterPointsResponse = TestUtil.getObjectFromJson("response/letterpoint.json",
                LetterPointsResponse.class);
        scoresResponse = TestUtil.getObjectFromJson("response/scores.json", listScoresType);
        scoresTop10Response = TestUtil.getObjectFromJson("response/scorestop10.json", listScoresType);
    }

    @Test
    @DisplayName("Test - calculateScore - success")
    void calculateScoreTest() {
        //given word
        when(pointsRepo.findAll()).thenReturn(LIST_LETTER_POINTS);
        //when
        Integer actualResult = scoringService.calculateScore(WORD);
        //then word is 4 + 1 + 1 + 2 = 8
        assertEquals(EIGHT, actualResult);
    }

    private List<LetterPoints> subSetLetterPointsByPoint(Integer point) {
        return LIST_LETTER_POINTS.stream()
                .filter(lp -> lp.getPoints().compareTo(point) == 0).toList();
    }

    @Test
    @DisplayName("Test - fetchScoringSystem - success")
    void fetchScoringSystemTest() {
        //given
        when(pointsRepo.findDistinctPoints()).thenReturn(SET_POINTS);
        when(pointsRepo.findByPoints(anyInt())).thenAnswer(invocation -> {
            Integer capturedPoint = invocation.getArgument(0);
            return subSetLetterPointsByPoint(capturedPoint);
        });
        //when
        LetterPointsResponse actualResult = scoringService.fetchScoringSystem();
        //then
        assertEquals(letterPointsResponse, actualResult);
    }

    @Test
    @DisplayName("Test - fetchAllScores - success")
    void fetchAllScoresTest() {
        //given
        when(scoresRepo.findAll(SCORES_SORTED)).thenReturn(scoresResponse);
        //when
        List<Scores> actualResult = scoringService.fetchAllScores();
        //then
        assertEquals(scoresResponse, actualResult);
    }

    @Test
    @DisplayName("Test - fetchTop10Scores - success")
    void fetchTop10ScoresTest() {
        //given
        when(scoresRepo.findAll(SCORES_SORTED)).thenReturn(scoresResponse);
        //when
        List<Scores> actualResult = scoringService.fetchTop10Scores();
        //then
        assertEquals(scoresTop10Response, actualResult);
    }

    @Test
    @DisplayName("Test - saveScore - success")
    void saveScoreTest() {
        //given
        when(scoresRepo.save(any())).thenReturn(Scores.builder().id(1L).build());
        //when
        Long actualResult = scoringService.saveScore(saveScoreRequest);
        //then
        assertEquals(1L, actualResult);
    }

    @Test
    @DisplayName("Test - removePointsData - success")
    void removePointsDataTest() {
        //given
        doNothing().when(pointsRepo).deleteAll();
        //when
        scoringService.removePointsData();
        //then
        verify(jdbcTemplate).execute(RESET_SEQ);
    }

    @Test
    @DisplayName("Test - setupPoints - success")
    void setupPointsTest() {
        //given
        doNothing().when(pointsRepo).deleteAll();
        when(scoresRepo.save(any())).thenReturn(Scores.builder().id(1L).build());
        //when
        scoringService.setupPoints(letterPointsRequest);
        //then
        verify(jdbcTemplate).execute(RESET_SEQ);
    }

}
