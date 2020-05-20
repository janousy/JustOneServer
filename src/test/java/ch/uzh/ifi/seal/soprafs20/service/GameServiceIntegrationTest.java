package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.*;
import ch.uzh.ifi.seal.soprafs20.entity.*;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

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
        List<Game> gameList = gameRepository.findAll();
        for (Game game : gameList) {
            game.setStatus(GameStatus.DELETE);
            gameRepository.save(game);
            gameService.deleteGameById(game.getGameId());
        }

    }


    @Test
    public void createGame_validInputs_success() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setStatus(GameStatus.LOBBY);
        testGame.setCorrectCards(0);

        // when
        Game createdGame = gameService.createGame(testGame);

        // then
        assertEquals(testGame.getGameId(), createdGame.getGameId());
        assertEquals(testGame.getName(), createdGame.getName());
        assertEquals(testGame.getCorrectCards(), createdGame.getCorrectCards());
        assertEquals(GameStatus.LOBBY, createdGame.getStatus());
        assertEquals(testGame.getPlayerList(), createdGame.getPlayerList());
        assertEquals(testGame.getCardList(), createdGame.getCardList());
    }

    @Test
    public void createGame_duplicateName_throwsException() {
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setStatus(GameStatus.LOBBY);
        testGame.setCorrectCards(0);

        Game createdGame = gameService.createGame(testGame);

        // attempt to create second user with same username
        Game testGame2 = new Game();

        // forget to change the game name
        testGame2.setName("testGame 1");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(testGame2));
    }

    @Test
    public void getGameById_validInputs_success() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setStatus(GameStatus.LOBBY);
        testGame.setCorrectCards(0);

        // when
        Game createdGame = gameService.createGame(testGame);
        long idOfGame = createdGame.getGameId();

        // when -> any object is being save in the gameRepository
        Game gameById = gameService.getGameById(idOfGame);

        // then
        assertEquals(testGame.getGameId(), gameById.getGameId());
        assertEquals(testGame.getName(), gameById.getName());
        assertEquals(GameStatus.LOBBY, gameById.getStatus());
        assertEquals(testGame.getCorrectCards(), gameById.getCorrectCards());
    }

    @Test
    public void getGameById_wrongInputs_throwsException() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        // when
        assertThrows(ResponseStatusException.class, () -> gameService.getGameById(0L));
    }

    @Test
    public void deleteGameById_validInput_success() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setCorrectCards(0);

        testGame = gameService.createGame(testGame);
        testGame.setStatus(GameStatus.DELETE);
        gameRepository.save(testGame);
        long idOfGame = testGame.getGameId();

        Game deletedGame = gameService.deleteGameById(idOfGame);

        // then
        assertEquals(testGame.getGameId(), deletedGame.getGameId());
        assertEquals(testGame.getName(), deletedGame.getName());
        assertEquals(GameStatus.DELETE, deletedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), deletedGame.getCorrectCards());
    }

    @Test
    public void deleteGameById_wrongInput_returnsTheSame() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setCorrectCards(0);

        testGame = gameService.createGame(testGame);
        long idOfGame = testGame.getGameId();

        Game deletedGame = gameService.deleteGameById(idOfGame);


        // then
        assertEquals(testGame.getGameId(), deletedGame.getGameId());
        assertEquals(testGame.getName(), deletedGame.getName());
        assertEquals(GameStatus.LOBBY, deletedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), deletedGame.getCorrectCards());
    }

    @Test
    public void checkGameReady_validInput_success() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setCorrectCards(0);

        testGame = gameService.createGame(testGame);

        //adjusting the testgame in order to let it start
        Player player1 = new Player();
        player1.setName("player1");
        player1.setStatus(PlayerStatus.READY);
        player1.setScore(0);
        player1.setRole(PlayerRole.GUEST);
        player1.setUserToken("player1");
        player1.setElapsedTime(0L);

        Player player2 = new Player();
        player2.setName("player2");
        player2.setStatus(PlayerStatus.READY);
        player2.setScore(0);
        player2.setRole(PlayerRole.GUEST);
        player2.setUserToken("player2");
        player2.setElapsedTime(0L);

        Player player3 = new Player();
        player3.setName("player3");
        player3.setStatus(PlayerStatus.READY);
        player3.setScore(0);
        player3.setRole(PlayerRole.GUEST);
        player3.setUserToken("player3");
        player3.setElapsedTime(0L);

        testGame.addPlayer(player1);
        testGame.addPlayer(player2);
        testGame.addPlayer(player3);

        //call method we want to test
        Game checkedGame = gameService.checkGameReady(testGame);


        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.RECEIVING_TERM, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());

    }

    @Test
    public void checkGameReady_playerListSmaller3_returnsTheSame() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setCorrectCards(0);

        testGame = gameService.createGame(testGame);

        //call method we want to test
        Game checkedGame = gameService.checkGameReady(testGame);

        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.LOBBY, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());

    }

    @Test
    public void checkGameReady_playerListNotReady_returnsTheSame() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setCorrectCards(0);

        testGame = gameService.createGame(testGame);

        //adjusting the testgame in order to let it start
        Player player1 = new Player();
        Player player2 = new Player();
        Player player3 = new Player();
        testGame.addPlayer(player1);
        testGame.addPlayer(player2);
        testGame.addPlayer(player3);

        //call method we want to test
        Game checkedGame = gameService.checkGameReady(testGame);

        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.LOBBY, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());

    }

    @Test
    public void checkIfPlayersKnowTerm_success() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setCorrectCards(0);

        testGame = gameService.createGame(testGame);

        //prepare the game
        Player player1 = new Player();
        player1.setName("player1");
        player1.setStatus(PlayerStatus.CLUE_GIVER);
        player1.setScore(0);
        player1.setRole(PlayerRole.GUEST);
        player1.setPlayerTermStatus(PlayerTermStatus.KNOWN);
        player1.setUserToken("player1");
        player1.setElapsedTime(0L);
        testGame.addPlayer(player1);

        Player player2 = new Player();
        player2.setName("player2");
        player2.setStatus(PlayerStatus.CLUE_GIVER);
        player2.setScore(0);
        player2.setRole(PlayerRole.GUEST);
        player2.setPlayerTermStatus(PlayerTermStatus.KNOWN);
        player2.setUserToken("player2");
        player2.setElapsedTime(0L);
        testGame.addPlayer(player2);


        //call the to test method
        Game checkedGame = gameService.checkIfPlayersKnowTerm(testGame);


        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.RECEIVING_HINTS, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());

    }

    @Test
    public void checkIfPlayersKnowTerm_nrOfUnknownsToLarge() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setCorrectCards(0);

        testGame = gameService.createGame(testGame);

        //prepare the game
        Player player1 = new Player();
        player1.setName("player1");
        player1.setStatus(PlayerStatus.CLUE_GIVER);
        player1.setScore(0);
        player1.setRole(PlayerRole.GUEST);
        player1.setPlayerTermStatus(PlayerTermStatus.KNOWN);
        player1.setUserToken("player1");
        player1.setElapsedTime(0L);
        testGame.addPlayer(player1);

        Player player2 = new Player();
        player2.setName("player2");
        player2.setStatus(PlayerStatus.CLUE_GIVER);
        player2.setScore(0);
        player2.setRole(PlayerRole.GUEST);
        player2.setPlayerTermStatus(PlayerTermStatus.UNKNOWN);
        player2.setUserToken("player2");
        player2.setElapsedTime(0L);
        testGame.addPlayer(player2);

        Round round = new Round();
        round.setId(1L);
        testGame.addRound(round);

        //call the to be tested method
        Game checkedGame = gameService.checkIfPlayersKnowTerm(testGame);


        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.RECEIVING_TERM, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());
    }

    @Test
    public void checkIfPlayersKnowTerm_NotAllClueGiversHaveReported() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setCorrectCards(0);

        testGame = gameService.createGame(testGame);

        //prepare the game
        Player player1 = new Player();
        player1.setName("player1");
        player1.setStatus(PlayerStatus.CLUE_GIVER);
        player1.setScore(0);
        player1.setRole(PlayerRole.GUEST);
        player1.setPlayerTermStatus(PlayerTermStatus.NOT_SET);
        player1.setUserToken("player1");
        player1.setElapsedTime(0L);
        testGame.addPlayer(player1);

        Player player2 = new Player();
        player2.setName("player2");
        player2.setStatus(PlayerStatus.CLUE_GIVER);
        player2.setScore(0);
        player2.setRole(PlayerRole.GUEST);
        player2.setPlayerTermStatus(PlayerTermStatus.NOT_SET);
        player2.setUserToken("player2");
        player2.setElapsedTime(0L);
        testGame.addPlayer(player2);

        //call the to be tested method
        Game checkedGame = gameService.checkIfPlayersKnowTerm(testGame);


        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.LOBBY, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());
    }

    @Test
    public void updateGameStatus_validInput_success() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        Game testGame = new Game();
        testGame.setName("testGame 1");
        testGame.setStatus(GameStatus.LOBBY);
        testGame.setCorrectCards(0);

        gameRepository.save(testGame);

        //defining the update for the Game
        Game updateForGame = new Game();
        updateForGame.setStatus(GameStatus.FINISHED);

        Game updatedGame = gameService.updateGameStatus(testGame.getGameId(), updateForGame);

        assertEquals(testGame.getGameId(), updatedGame.getGameId());
        assertEquals(testGame.getName(), updatedGame.getName());
        assertEquals(GameStatus.FINISHED, updatedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), updatedGame.getCorrectCards());
    }

    @Test
    public void updateGameStatus_invalidInput_throwsException() {
        // given
        assertNull(gameRepository.findByName("testGame 1"));

        //defining the update for the Game
        Game updateForGame = new Game();
        updateForGame.setStatus(null);

        assertThrows(ResponseStatusException.class, () -> gameService.updateGameStatus(1L, updateForGame));

    }

}
