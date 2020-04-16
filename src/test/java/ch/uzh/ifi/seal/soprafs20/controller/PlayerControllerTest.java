package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;


@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @Test
    public void givenPlayers_whenGetAllPlayers_thenReturnJsonArray() throws Exception {

        Player testPlayer = new Player();
        testPlayer = new Player();
        testPlayer.setName("testPlayer");
        testPlayer.setStatus(PlayerStatus.NOT_READY);
        testPlayer.setScore(0);
        testPlayer.setRole(PlayerRole.HOST);
        testPlayer.setUserToken("12345");
        testPlayer.setElapsedTime(0L);

        List<Player> allPlayers = Collections.singletonList(testPlayer);

        given(playerService.getPlayers()).willReturn(allPlayers);

        MockHttpServletRequestBuilder getRequest = get("/games/players").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(testPlayer.getName())))
                .andExpect(jsonPath("$[0].status", is(testPlayer.getStatus().toString())))
                .andExpect(jsonPath("$[0].score", is(testPlayer.getScore())))
                .andExpect(jsonPath("$[0].role", is(testPlayer.getRole().toString())))
                .andExpect(jsonPath("$[0].userToken", is(testPlayer.getUserToken())))
                .andExpect(jsonPath("$[0].elapsedTime", is((int) testPlayer.getElapsedTime())));
    }

    @Test
    public void givenPlayers_whenGetAllPlayerFromGame() throws Exception {

        Player testPlayer = new Player();
        testPlayer = new Player();
        testPlayer.setName("testPlayer");
        testPlayer.setStatus(PlayerStatus.NOT_READY);
        testPlayer.setScore(0);
        testPlayer.setRole(PlayerRole.HOST);
        testPlayer.setUserToken("12345");
        testPlayer.setElapsedTime(0L);

        List<Player> allPlayers = Collections.singletonList(testPlayer);

        given(playerService.getPlayersFromGame(Mockito.anyLong())).willReturn(allPlayers);

        MockHttpServletRequestBuilder getRequest = get("/games/1/players").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(testPlayer.getName())))
                .andExpect(jsonPath("$[0].status", is(testPlayer.getStatus().toString())))
                .andExpect(jsonPath("$[0].score", is(testPlayer.getScore())))
                .andExpect(jsonPath("$[0].role", is(testPlayer.getRole().toString())))
                .andExpect(jsonPath("$[0].userToken", is(testPlayer.getUserToken())))
                .andExpect(jsonPath("$[0].elapsedTime", is((int) testPlayer.getElapsedTime())));

    }

    @Test
    public void givenPlayers_whenGetPlayerFromGame_thenReturnJsonArray() throws Exception {

        Player testPlayer = new Player();
        testPlayer = new Player();
        testPlayer.setName("testPlayer");
        testPlayer.setStatus(PlayerStatus.NOT_READY);
        testPlayer.setScore(0);
        testPlayer.setRole(PlayerRole.HOST);
        testPlayer.setUserToken("12345");
        testPlayer.setElapsedTime(0L);

        given(playerService.getPlayerById(Mockito.anyLong())).willReturn(testPlayer);

        MockHttpServletRequestBuilder getRequest = get("/games/players/1").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(testPlayer.getName())))
                .andExpect(jsonPath("$.status", is(testPlayer.getStatus().toString())))
                .andExpect(jsonPath("$.role", is(testPlayer.getRole().toString())))
                .andExpect(jsonPath("$.score", is(testPlayer.getScore())))
                .andExpect(jsonPath("$.userToken", is(testPlayer.getUserToken())))
                .andExpect(jsonPath("$.elapsedTime", is((int) testPlayer.getElapsedTime())));
    }

    @Test
    public void createPlayer_validInput_playerCreated() throws Exception {
        Player testPlayer = new Player();
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setName("testPlayer");
        testPlayer.setStatus(PlayerStatus.NOT_READY);
        testPlayer.setScore(0);
        testPlayer.setRole(PlayerRole.HOST);
        testPlayer.setUserToken("12345");
        testPlayer.setElapsedTime(0L);

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setName("testPlayer");
        playerPostDTO.setUserToken("12345");

        given(playerService.createPlayer(Mockito.any(), Mockito.anyLong())).willReturn(testPlayer);

        MockHttpServletRequestBuilder postRequest = post("/games/1/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(testPlayer.getName())))
                .andExpect(jsonPath("$.status", is(testPlayer.getStatus().toString())))
                .andExpect(jsonPath("$.score", is(testPlayer.getScore())))
                .andExpect(jsonPath("$.role", is(testPlayer.getRole().toString())))
                .andExpect(jsonPath("$.userToken", is(testPlayer.getUserToken())))
                .andExpect(jsonPath("$.elapsedTime", is((int) testPlayer.getElapsedTime())));
    }

    @Test
    public void deletePlayer_validInput_playerDeleted() throws Exception {
        Player testPlayer = new Player();
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setName("testPlayer");
        testPlayer.setStatus(PlayerStatus.NOT_READY);
        testPlayer.setScore(0);
        testPlayer.setRole(PlayerRole.HOST);
        testPlayer.setUserToken("12345");
        testPlayer.setElapsedTime(0L);

        given(playerService.deletePlayer(Mockito.anyLong(), Mockito.anyLong())).willReturn(testPlayer);

        MockHttpServletRequestBuilder deleteRequest = delete("/games/1/players/1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(testPlayer.getName())))
                .andExpect(jsonPath("$.status", is(testPlayer.getStatus().toString())))
                .andExpect(jsonPath("$.score", is(testPlayer.getScore())))
                .andExpect(jsonPath("$.role", is(testPlayer.getRole().toString())))
                .andExpect(jsonPath("$.userToken", is(testPlayer.getUserToken())))
                .andExpect(jsonPath("$.elapsedTime", is((int) testPlayer.getElapsedTime())));
    }

    /* HELPER METHOD*/
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }

}
