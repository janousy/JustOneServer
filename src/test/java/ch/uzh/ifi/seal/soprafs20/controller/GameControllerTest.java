package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.entity.*;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GameDeleteDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * GameControllerTest
 * This is a WebMvcTest which allows to test the GameController i.e. GET/POST request without actually sending them over the network.
 * This tests if the GameController works.
 */
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    public void givenGames_whenGetGames_thenReturnJsonArray() throws Exception {
        // given
        Game game = new Game();
        game.setName("Game 1");
        game.setGameId(1L);
        game.setCorrectCards(0);
        game.setPlayerList(new ArrayList<Player>());
        game.setRoundList(new ArrayList<Round>());
        game.setCardList(new ArrayList<Card>());
        game.setStatus(GameStatus.LOBBY);

        List<Game> allGames = Collections.singletonList(game);

        // this mocks the GameService -> we define above what the gameService should return when getUsers() is called
        given(gameService.getAllGames()).willReturn(allGames);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(game.getName())))
                .andExpect(jsonPath("$[0].gameId", is(game.getGameId().intValue())))
                .andExpect(jsonPath("$[0].status", is(game.getStatus().toString())))
                .andExpect(jsonPath("$[0].correctCards", is(game.getCorrectCards())));
    }

    @Test
    public void createGame_validInput_gameCreated() throws Exception {
        // given
        Game game = new Game();
        game.setName("Game 1");
        game.setGameId(1L);
        game.setCorrectCards(0);
        game.setPlayerList(new ArrayList<Player>());
        game.setRoundList(new ArrayList<Round>());
        game.setCardList(new ArrayList<Card>());
        game.setStatus(GameStatus.LOBBY);

        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setName("Test Game");

        given(gameService.createGame(Mockito.any())).willReturn(game);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO));


        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId", is(game.getGameId().intValue())))
                .andExpect(jsonPath("$.name", is(game.getName())))
                .andExpect(jsonPath("$.status", is(game.getStatus().toString())))
                .andExpect(jsonPath("$.correctCards", is(game.getCorrectCards())));

    }

    @Test
    public void getGameById_validInput() throws Exception {
        // given
        Game game = new Game();
        game.setName("Game 1");
        game.setGameId(1L);
        game.setCorrectCards(0);
        game.setPlayerList(new ArrayList<Player>());
        game.setRoundList(new ArrayList<Round>());
        game.setCardList(new ArrayList<Card>());
        game.setStatus(GameStatus.LOBBY);

        GameGetDTO gameGetDTO = new GameGetDTO();
        gameGetDTO.setGameId(1L);
        gameGetDTO.setName("Game 1");
        gameGetDTO.setStatus(GameStatus.LOBBY);
        gameGetDTO.setCorrectCards(0);
        //gameGetDTO.setPlayerList(new ArrayList<Player>());

        given(gameService.getGameById(Mockito.any())).willReturn(game);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/games/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameGetDTO));


        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is(game.getGameId().intValue())))
                .andExpect(jsonPath("$.name", is(game.getName())))
                .andExpect(jsonPath("$.status", is(game.getStatus().toString())))
                .andExpect(jsonPath("$.correctCards", is(game.getCorrectCards())));
    }


    @Test
    public void deleteGame_validInput_gameDeleted() throws Exception {
        // given
        Game game = new Game();
        game.setName("Game 1");
        game.setGameId(1L);
        game.setCorrectCards(0);
        game.setPlayerList(new ArrayList<Player>());
        game.setRoundList(new ArrayList<Round>());
        game.setCardList(new ArrayList<Card>());
        game.setStatus(GameStatus.LOBBY);

        GameDeleteDTO gameDeleteDTO = new GameDeleteDTO();
        gameDeleteDTO.setGameId(1L);

        given(gameService.deleteGameById(Mockito.any())).willReturn(game);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder deleteRequest = delete("/games/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameDeleteDTO));

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is(game.getGameId().intValue())))
                .andExpect(jsonPath("$.name", is(game.getName())))
                .andExpect(jsonPath("$.status", is(game.getStatus().toString())))
                .andExpect(jsonPath("$.correctCards", is(game.getCorrectCards())));
    }


    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }

}