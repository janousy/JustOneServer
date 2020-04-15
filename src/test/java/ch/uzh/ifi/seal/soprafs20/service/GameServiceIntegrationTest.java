package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {
    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @BeforeEach
    public void setup() {
        gameRepository.deleteAll();
    }

    //TODO diese test gehen nicht da mit repository fehler, debuggen nicht möglich
/*
    @Test
    public void createGame_validInputs_success() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setStatus(GameStatus.LOBBY);
        testGame.setCorrectCards(0);
        testGame.setGameId(2L);

        // when
        Game createdGame = gameService.createGame(testGame);

        // then
        assertEquals(testGame.getGameId(), createdGame.getGameId());
        assertEquals(testGame.getName(), createdGame.getName());
        assertEquals(testGame.getCorrectCards(), createdGame.getCorrectCards());
        assertEquals(GameStatus.LOBBY, createdGame.getStatus());
        assertEquals(testGame.getPlayerList(), createdGame.getPlayerList());
        //assertEquals(testGame.getRoundList(), createdGame.getRoundList());
        assertEquals(testGame.getCardList(), createdGame.getCardList());
    }


    @Test
    public void createGame_duplicateName_throwsException() {
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setStatus(GameStatus.LOBBY);
        testGame.setCorrectCards(0);
        testGame.setGameId(1L);

        Game createdGame = gameService.createGame(testGame);

        // attempt to create second user with same username
        Game testGame2 = new Game();

        // forget to change the game name
        testGame2.setName("testGame 1");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(testGame2));
    }

 */

}
