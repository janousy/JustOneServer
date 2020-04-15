package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game testGame;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setName("testName");
        testGame.setCorrectCards(0);
        testGame.setStatus(GameStatus.LOBBY);
        testGame.setPlayerList(new ArrayList<Player>());


        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
    }

    @Test
    public void createGame_validInputs_success() {
        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Game createdGame = gameService.createGame(testGame);

        // then
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testGame.getGameId(), createdGame.getGameId());
        assertEquals(testGame.getName(), createdGame.getName());
        assertEquals(GameStatus.LOBBY, createdGame.getStatus());
        assertEquals(testGame.getCorrectCards(), createdGame.getCorrectCards());
        assertEquals(testGame.getPlayerList(), createdGame.getPlayerList());
        assertEquals(testGame.getCardList(), createdGame.getCardList());
        assertEquals(testGame.getRoundList(), createdGame.getRoundList());
    }

    @Test
    public void createGame_duplicateInputs_throwsException() {
        // given -> a first game has already been created
        gameService.createGame(testGame);

        // when -> setup additional mocks for GameRepository
        Mockito.when(gameRepository.findByName(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(testGame);

        // then -> attempt to create second game with same name -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(testGame));
    }

    @Test
    public void getGameById_validInputs_success() {
        // when -> setup additional mocks for GameRepository
        Mockito.when(gameRepository.findByName(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(testGame);

        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Game gameById = gameService.getGameById(1L);

        // then
        assertEquals(testGame.getGameId(), gameById.getGameId());
        assertEquals(testGame.getName(), gameById.getName());
        assertEquals(GameStatus.LOBBY, gameById.getStatus());
        assertEquals(testGame.getCorrectCards(), gameById.getCorrectCards());
        assertEquals(testGame.getPlayerList(), gameById.getPlayerList());
        assertEquals(testGame.getCardList(), gameById.getCardList());
        assertEquals(testGame.getRoundList(), gameById.getRoundList());
    }

    @Test
    public void getGameById_wrongInputs_throwsException() {
        // when -> setup additional mocks for GameRepository
        Mockito.when(gameRepository.findByName(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(null);

        //Game gameById = gameService.getGameById(1L);

        assertThrows(ResponseStatusException.class, () -> gameService.getGameById(1L));

    }

    @Test
    public void addPlayerToGame_validInputs_success() {
        testGame.addPlayer(new Player());

        Game createdGame = gameService.createGame(testGame);
        createdGame.addPlayer(new Player());

        // then
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());

        // then
        assertEquals(testGame.getPlayerList(), createdGame.getPlayerList());

    }

    @Test
    public void removePlayerFromGame_validInputs_success() {
        Player player1 = new Player();

        testGame.addPlayer(player1);
        testGame.removePlayer(player1);

        Game createdGame = gameService.createGame(testGame);
        createdGame.addPlayer(player1);
        createdGame.removePlayer(player1);

        // then
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());

        // then
        assertEquals(testGame.getPlayerList(), createdGame.getPlayerList());

    }


}
