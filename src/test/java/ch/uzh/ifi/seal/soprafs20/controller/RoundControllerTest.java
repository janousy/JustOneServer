package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.GuessPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.TermPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.RoundService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoundController.class)
public class RoundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoundService roundService;

    //get all rounds
    @Test
    public void givenRounds_whenGetAllRounds_thenReturnJsonArray() throws Exception {

        Round testRound = new Round();

        List<Round> allRounds = Collections.singletonList(testRound);

        given(roundService.getAllRounds()).willReturn(allRounds);

        MockHttpServletRequestBuilder getRequest = get("/games/rounds").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testRound.getId())));
    }

    //get rounds from game ID
    @Test
    public void givenRound_whenGetRoundsFromGame_thenReturnJsonArray() throws Exception {

        Round testRound = new Round();

        List<Round> allRounds = Collections.singletonList(testRound);

        given(roundService.getAllRoundsOfGame(1L)).willReturn(allRounds);

        MockHttpServletRequestBuilder getRequest = get("/games/1/rounds").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testRound.getId())));
    }

    //get last round from game by id
    public void givenRound_whenGetRoundsFromGameWithParams_thenReturnJsonArray() throws Exception {

        Round testRound = new Round();

        List<Round> allRounds = Collections.singletonList(testRound);

        given(roundService.getAllRoundsOfGame(1L)).willReturn(allRounds);

        MockHttpServletRequestBuilder getRequest = get("/games/1/rounds?lastRound=true").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testRound.getId())));
    }

    @Test
    public void givenTerm_whenGetTermFromRound_thenReturnJsonArray() throws Exception {

        Term term = new Term();

        given(roundService.getCurrentTermFromRound(1L)).willReturn(term);

        MockHttpServletRequestBuilder getRequest = get("/games/1/terms").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.roundId", is(term.getRoundId())))
                .andExpect(jsonPath("$.content", is(term.getContent())))
                .andExpect(jsonPath("$.token", is(term.getToken())))
                .andExpect(jsonPath("$.wordId", is(term.getWordId())));
    }

    @Test
    public void givenGuess_whenGetGuessFromRound_thenReturnJsonArray() throws Exception {

        Guess guess = new Guess();

        given(roundService.getGuessOfCurrentRound(1L)).willReturn(guess);

        MockHttpServletRequestBuilder getRequest = get("/games/1/guesses").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.roundId", is(guess.getRoundId())))
                .andExpect(jsonPath("$.content", is(guess.getContent())))
                .andExpect(jsonPath("$.token", is(guess.getToken())));
    }

    @Test
    public void givenHintList_whenGetHintsFromRound_thenReturnJsonArray() throws Exception {

        Hint testHint = new Hint();

        List<Hint> hints = Collections.singletonList(testHint);

        given(roundService.getAllHintsFromRound(1L)).willReturn(hints);

        MockHttpServletRequestBuilder getRequest = get("/games/1/hints").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].roundId", is(hints.get(0).getRoundId())))
                .andExpect(jsonPath("$[0].content", is(hints.get(0).getContent())))
                .andExpect(jsonPath("$[0].token", is(hints.get(0).getToken())));
    }

    @Test
    public void givenHint_whenCreateHintToRound_thenReturnJsonArray() throws Exception {

        Hint hint = new Hint();
        hint.setRoundId(1L);
        hint.setContent("test");
        hint.setToken("123");

        HintPostDTO hintPostDTO = new HintPostDTO();
        hintPostDTO.setRoundId(1L);
        hintPostDTO.setContent("test");
        hintPostDTO.setToken("123");

        given(roundService.addHintToRound(Mockito.any(), Mockito.anyLong())).willReturn(hint);

        MockHttpServletRequestBuilder postRequest = post("/games/1/hints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(hintPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roundId", is(hint.getRoundId().intValue())))
                .andExpect(jsonPath("$.content", is(hint.getContent())))
                .andExpect(jsonPath("$.token", is(hint.getToken())))
                .andExpect(jsonPath("$.similarity", is(hint.getSimilarity())))
                .andExpect(jsonPath("$.reporters", is(hint.getReporters())))
                .andExpect(jsonPath("$.marked", is(hint.getMarked())))
                .andExpect(jsonPath("$.status", is(hint.getStatus())));
    }

    @Test
    public void givenHint_whenReportHintOfRound_thenReturnJsonArray() throws Exception {

        Hint hint = new Hint();
        hint.setRoundId(1L);
        hint.setContent("test");
        hint.setToken("123");
        hint.setSimilarity(new ArrayList<>(Arrays.asList(1, 2)));
        hint.setReporters(new ArrayList<>(Arrays.asList("someToken", "anotherToken")));
        hint.setMarked(ActionTypeStatus.INVALID);

        HintPutDTO hintPutDTO = new HintPutDTO();
        hintPutDTO.setToken("123");
        hintPutDTO.setSimilarity(new ArrayList<>(Arrays.asList(1, 2)));
        hintPutDTO.setReporters(new ArrayList<>(Arrays.asList("someToken", "anotherToken")));
        hintPutDTO.setMarked(ActionTypeStatus.INVALID);


        given(roundService.updateHint(Mockito.any(), Mockito.anyLong())).willReturn(hint);

        MockHttpServletRequestBuilder putRequest = put("/games/1/hints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(hintPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roundId", is(hint.getRoundId().intValue())))
                .andExpect(jsonPath("$.content", is(hint.getContent())))
                .andExpect(jsonPath("$.token", is(hint.getToken())))
                .andExpect(jsonPath("$.similarity", is(hint.getSimilarity())))
                .andExpect(jsonPath("$.reporters", is(hint.getReporters())))
                .andExpect(jsonPath("$.marked", is(hint.getMarked().toString())));
    }

    @Test
    public void givenGuess_whenCreateGuessToRound_thenReturnJsonArray() throws Exception {

        Guess guess = new Guess();
        guess.setRoundId(1L);
        guess.setContent("test");
        guess.setToken("123");

        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setRoundId(1L);
        guessPostDTO.setContent("test");
        guessPostDTO.setToken("123");

        given(roundService.addGuessToRound(Mockito.any(), Mockito.anyLong())).willReturn(guess);

        MockHttpServletRequestBuilder postRequest = post("/games/1/guesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(guessPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roundId", is(guess.getRoundId().intValue())))
                .andExpect(jsonPath("$.content", is(guess.getContent())))
                .andExpect(jsonPath("$.token", is(guess.getToken())))
                .andExpect(jsonPath("$.status", is(guess.getStatus())));
    }

    @Test
    public void givenTerm_whenCreateTermToRound_thenReturnJsonArray() throws Exception {

        Term term = new Term();
        term.setRoundId(1L);
        term.setWordId(1L);
        term.setContent("test");
        term.setToken("123");


        TermPostDTO termPostDTO = new TermPostDTO();
        termPostDTO.setToken("123");
        termPostDTO.setWordId(1L);

        given(roundService.addTermToRound(Mockito.any(), Mockito.anyLong())).willReturn(term);

        MockHttpServletRequestBuilder postRequest = post("/games/1/terms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(termPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roundId", is(term.getRoundId().intValue())))
                .andExpect(jsonPath("$.wordId", is(term.getWordId().intValue())))
                .andExpect(jsonPath("$.content", is(term.getContent())))
                .andExpect(jsonPath("$.token", is(term.getToken())));
    }

    @Test
    public void givenTermAndGame_whenDeleteTermFromRound_thenReturnJsonArray() throws Exception {

        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setToken("123");

        Game game = new Game();
        game.setGameId(1L);

        given(roundService.skipTermToBeGuessed(Mockito.any(), Mockito.anyLong())).willReturn(game);

        MockHttpServletRequestBuilder postRequest = delete("/games/1/guesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(guessPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is(game.getGameId().intValue())));
    }


    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
