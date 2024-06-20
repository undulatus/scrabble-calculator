package com.bryanbatanes.scrabble.controller;

import com.bryanbatanes.scrabble.dto.LetterPointsResponse;
import com.bryanbatanes.scrabble.dto.SaveScoreRequest;
import com.bryanbatanes.scrabble.model.Scores;
import com.bryanbatanes.scrabble.service.ScoringService;
import com.bryanbatanes.scrabble.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ScoringController.class)
@SuppressWarnings("unchecked")
public class ScoringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScoringService scoringService;

    //can change to prop url with spring profile
    private static final String BASE_URL = "http://localhost:8080/scrabble" ;

    // ####### constants ########

    private static String letterPointsRequestJson;
    private static String saveScoreRequestJson;
    private static SaveScoreRequest saveScoreRequest;
    private static LetterPointsResponse letterPointsResponse;
    private static List<Scores> scoresResponse;
    private static List<Scores> scoresTop10Response;

    private static final String SUCCESS = "success";
    private static final String WORD = "word";
    private static final int EIGHT = 8;

    private static final String LETTERPOINTS_URL = BASE_URL + "/letterpoints";
    private static final String LETTERPOINTS_SETUP_URL = LETTERPOINTS_URL + "/setup";
    private static final String LETTERPOINTS_REMOVE_URL = LETTERPOINTS_URL + "/remove";

    private static final String SCORES_URL = BASE_URL + "/scores";
    private static final String SCORES_CALCULATE_URL = SCORES_URL + "/calculate";
    private static final String SCORES_TOP10_URL = SCORES_URL + "/top10";

    @BeforeAll
    static void setupConstants() throws IOException {
        letterPointsRequestJson = TestUtil.getJsonStringFromFile("request/letterpoint.json");
        saveScoreRequest = SaveScoreRequest.builder().word(WORD).build();
        saveScoreRequestJson = TestUtil.getJsonStringFromObject(saveScoreRequest);

        letterPointsResponse = TestUtil.getObjectFromJson("response/letterpoint.json",
                LetterPointsResponse.class);
        scoresResponse = TestUtil.getObjectFromJson("response/scores.json", List.class);
        scoresTop10Response = TestUtil.getObjectFromJson("response/scorestop10.json", List.class);
    }

    @Test
    @DisplayName("GET - letter points - success")
    void fetchLetterPointsTest() throws Exception {
        when(scoringService.fetchScoringSystem()).thenReturn(letterPointsResponse);

        String jsonResp = mockMvc.perform(get(LETTERPOINTS_URL))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        LetterPointsResponse response = TestUtil.jsonToPojo(jsonResp, LetterPointsResponse.class);

        assertEquals(letterPointsResponse, response);
    }

    @Test
    @DisplayName("POST - letter points setup - success")
    void setupScoringSystem() throws Exception {
        doNothing().when(scoringService).setupPoints(anyMap());

        String jsonResp = mockMvc.perform(post(LETTERPOINTS_SETUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(letterPointsRequestJson))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andReturn().getResponse().getContentAsString();

        assertEquals(SUCCESS, jsonResp);
    }

    @Test
    @DisplayName("DELETE - letter points setup - success")
    void removeScoringSystem() throws Exception {
        doNothing().when(scoringService).removePointsData();

        String jsonResp = mockMvc.perform(delete(LETTERPOINTS_REMOVE_URL))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        assertEquals(SUCCESS, jsonResp);
    }

    @Test
    @DisplayName("GET - fetchAllScores - success")
    void fetchAllScores() throws Exception {
        when(scoringService.fetchAllScores()).thenReturn(scoresResponse);

        String jsonResp = mockMvc.perform(get(SCORES_URL))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        List<Scores> actualResp = TestUtil.jsonToPojo(jsonResp, List.class);

        assertEquals(scoresResponse, actualResp);
    }

    @Test
    @DisplayName("GET - fetchTop10Scores - success")
    void fetchTop10Scores() throws Exception {
        when(scoringService.fetchTop10Scores()).thenReturn(scoresTop10Response);

        String jsonResp = mockMvc.perform(get(SCORES_TOP10_URL))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        List<Scores> actualResp = TestUtil.jsonToPojo(jsonResp, List.class);

        assertEquals(scoresTop10Response, actualResp);
    }

    @Test
    @DisplayName("GET - calculateScore - success")
    void calculateScore() throws Exception {
        when(scoringService.calculateScore(WORD)).thenReturn(EIGHT);

        String jsonResp = mockMvc.perform(get(SCORES_CALCULATE_URL).param(WORD, WORD))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        Integer actualResp = Integer.parseInt(jsonResp);

        assertEquals(EIGHT, actualResp);
    }

    @Test
    @DisplayName("POST - saveScore - success")
    void saveScore() throws Exception {
        Long expectedResp = 1L;
        when(scoringService.saveScore(saveScoreRequest)).thenReturn(expectedResp);

        String jsonResp = mockMvc.perform(post(SCORES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(saveScoreRequestJson))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andReturn().getResponse().getContentAsString();
        Long actualResp = Long.parseLong(jsonResp);

        assertEquals(expectedResp, actualResp);
    }

    @Test
    @DisplayName("POST - saveScore - invalid request body word is blank")
    void saveScoreInvalid() throws Exception {
        //given
        SaveScoreRequest saveScoreRequestBlank = saveScoreRequest.toBuilder()
                .word("") //blank will give missing error
                .build();

        String saveScoreRequestBlankJson = TestUtil.getJsonStringFromObject(saveScoreRequestBlank);

        //when
        String jsonResp = mockMvc.perform(post(SCORES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveScoreRequestBlankJson))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn().getResponse().getContentAsString();

        //then
        assertTrue(jsonResp.contains("input word is missing"));
    }

    @Test
    @DisplayName("POST - saveScore - invalid request body word is > 10 chars")
    void saveScoreInvalidWordTooLong() throws Exception {
        //given
        SaveScoreRequest saveScoreRequestWordTooLong = saveScoreRequest.toBuilder()
                .word("I AM GREATER THAN 10 CHARACTERS") //too long
                .build();

        String saveScoreRequestWordTooLongJson = TestUtil.getJsonStringFromObject(saveScoreRequestWordTooLong);

        //when
        String jsonResp = mockMvc.perform(post(SCORES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveScoreRequestWordTooLongJson))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn().getResponse().getContentAsString();

        //then
        assertTrue(jsonResp.contains("please enter up to 10 characters only"));
    }

}
