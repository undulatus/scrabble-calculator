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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

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

    private static String letterPointsRequestJson;
    private static String saveScoreRequestJson;
    private static SaveScoreRequest saveScoreRequest;
    private static LetterPointsResponse letterPointsResponse;
    private static List<Scores> scoresResponse;
    private static List<Scores> scoresTop10Response;

    // ####### CONSTANTS ############

    private static String WORD = "word";
    private static Integer EIGHT = 8;
    private static List<LetterPoints> LIST_LETTER_POINTS;
    private static Set<Integer> SET_POINTS;

    @BeforeAll
    static void setup() throws IOException {
        TypeReference<List<LetterPoints>> listLetterPointsType = new TypeReference<List<LetterPoints>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        };
        LIST_LETTER_POINTS = TestUtil
                .getObjectFromJson("dbmodel/letterpoint_entity.json", listLetterPointsType);

        SET_POINTS = Set.of(1,2,3,4,6,8,10);

        letterPointsRequestJson = TestUtil.getJsonStringFromFile("request/letterpoint.json");
        saveScoreRequest = SaveScoreRequest.builder().word(WORD).build();
        saveScoreRequestJson = TestUtil.getJsonStringFromObject(saveScoreRequest);

        letterPointsResponse = TestUtil.getObjectFromJson("response/letterpoint.json",
                LetterPointsResponse.class);
        scoresResponse = TestUtil.getObjectFromJson("response/scores.json", List.class);
        scoresTop10Response = TestUtil.getObjectFromJson("response/scorestop10.json", List.class);
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

}
