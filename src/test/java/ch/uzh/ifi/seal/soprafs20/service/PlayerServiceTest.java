package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.*;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private PlayerService playerService;


    private Player testPlayer;
    private Game testGame;
    private User testUser;
    private List<Player> playerList;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setName("testPlayer");
        testPlayer.setUserToken("12345");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUsername");
        testUser.setToken("12345");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setPassword("password");

        testGame = new Game();
        testGame.setStatus(GameStatus.LOBBY);
        playerList = new ArrayList<>();

        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        Mockito.when(gameRepository.findGameByGameId(Mockito.anyLong())).thenReturn(testGame);
        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(testUser);
        Mockito.when(playerRepository.findPlayerById(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testPlayer));
        Mockito.when(playerRepository.findByGameGameId(Mockito.any())).thenReturn(playerList);
    }

    @Test
    public void createPlayer_validInputs_success() {

        Player createdPlayer = playerService.createPlayer(testPlayer, 1L);

        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getName(), createdPlayer.getName());
        assertEquals(testPlayer.getStatus(), createdPlayer.getStatus());
        assertEquals(testPlayer.getScore(), createdPlayer.getScore());
        assertEquals(testPlayer.getRole(), createdPlayer.getRole());
        assertEquals(testPlayer.getUserToken(), createdPlayer.getUserToken());
        assertEquals(testPlayer.getElapsedTime(), createdPlayer.getElapsedTime());
    }

    @Test
    public void whenCreatePlayer_CheckIfGameHasHost_OnlyOneHost_success() {
        Player testPlayer2 = new Player();
        testPlayer2.setId(2L);
        testPlayer2.setName("testPlayer");
        testPlayer2.setUserToken("12345");

        Mockito.when(playerRepository.findByGameGameId(Mockito.anyLong())).thenReturn(playerList);

        Player createdPlayer1 = playerService.createPlayer(testPlayer, 1L);

        playerList.add(testPlayer);
        testGame.setPlayerList(playerList);
        Player createdPlayer2 = playerService.createPlayer(testPlayer2, 1L);

        Mockito.verify(playerRepository, Mockito.times(2)).save(Mockito.any());
        Mockito.verify(playerRepository, Mockito.times(2)).findByGameGameId(Mockito.any());

        assertEquals(PlayerRole.HOST, createdPlayer1.getRole());
        assertEquals(PlayerRole.GUEST, createdPlayer2.getRole());
    }

    @Test
    public void whenCreatePlayer_FirstIsHostAndSecondGuest_success() {
        testPlayer.setRole(PlayerRole.HOST);
        playerList.add(testPlayer);
        testGame.setStatus(GameStatus.RECEIVING_GUESS);

        assertThrows(ResponseStatusException.class, () -> playerService.createPlayer(testPlayer, 1L));

        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void deletePlayer_whenPlayerInGame_success() {
        Long gameId = 1L;
        Long playerId = 1L;
        List<Player> testPlayerList = new ArrayList<>();
        testPlayerList.add(testPlayer);
        testGame.setPlayerList(testPlayerList);

        Player deletedPlayer = playerService.deletePlayer(gameId, playerId);

        Mockito.verify(playerRepository, Mockito.times(1)).findPlayerById(Mockito.any());

        assertEquals(testPlayer.getId(), deletedPlayer.getId());
        assertEquals(testPlayer.getName(), deletedPlayer.getName());
        assertEquals(testPlayer.getStatus(), deletedPlayer.getStatus());
        assertEquals(testPlayer.getScore(), deletedPlayer.getScore());
        assertEquals(testPlayer.getRole(), deletedPlayer.getRole());
        assertEquals(testPlayer.getUserToken(), deletedPlayer.getUserToken());
        assertEquals(testPlayer.getElapsedTime(), deletedPlayer.getElapsedTime());
    }

    @Test
    public void deletePlayer_whenPlayerNotInGame_throwsExeption() {
        Long gameId = 1L;
        Long playerId = 1L;

        assertThrows(ResponseStatusException.class, () -> playerService.deletePlayer(gameId, playerId));
        Mockito.verify(playerRepository, Mockito.times(1)).findPlayerById(Mockito.any());
    }

    @Test
    public void deletePlayer_whenGameHasStarted_throwsExeption() {
        Long gameId = 1L;
        Long playerId = 1L;
        testGame.setStatus(GameStatus.RECEIVING_GUESS); //must not be LOBBY or FINISHED

        assertThrows(ResponseStatusException.class, () -> playerService.deletePlayer(gameId, playerId));
        Mockito.verify(playerRepository, Mockito.times(1)).findPlayerById(Mockito.any());
    }

    @Test
    public void updatePlayerTermStatus_whenPlayerIsClueGiver_success() {

        Long playerId = 1L;

        Player playerInput = new Player();
        playerInput.setPlayerTermStatus(PlayerTermStatus.KNOWN);
        playerInput.setStatus(null); //has to be null as PlayerTermStatus cannot be updated during active play

        testPlayer.setStatus(PlayerStatus.CLUE_GIVER);

        Player updatedPlayer = playerService.updatePlayer(playerInput, playerId);

        Mockito.verify(playerRepository, Mockito.times(1)).findById(Mockito.any());

        assertEquals(testPlayer.getStatus(), updatedPlayer.getStatus());
        assertEquals(PlayerTermStatus.KNOWN, updatedPlayer.getPlayerTermStatus());
    }

    @Test
    public void updatePlayerTermStatus_whenPlayerIsNotClueGiver_throwsExeption() {

        Long playerId = 1L;

        Player playerInput = new Player();
        playerInput.setPlayerTermStatus(PlayerTermStatus.KNOWN);
        playerInput.setStatus(null); //has to be null as PlayerTermStatus cannot be updated during active play

        testPlayer.setStatus(PlayerStatus.READY);

        assertThrows(ResponseStatusException.class, () -> playerService.updatePlayer(playerInput, playerId));
        Mockito.verify(playerRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    public void updatePlayerStatus_whenPlayerNotInAction_success() {

        Long playerId = 1L;

        Player playerInput = new Player();
        playerInput.setPlayerTermStatus(null);
        playerInput.setStatus(PlayerStatus.READY);

        testPlayer.setStatus(PlayerStatus.NOT_READY);

        Player updatedPlayer = playerService.updatePlayer(playerInput, playerId);

        Mockito.verify(playerRepository, Mockito.times(1)).findById(Mockito.any());

        assertEquals(playerInput.getStatus(), updatedPlayer.getStatus());
        assertEquals(testPlayer.getPlayerTermStatus(), updatedPlayer.getPlayerTermStatus());
    }


    @Test
    public void updatePlayerStatus_whenPlayerInAction_throwsExeption() {

        Long playerId = 1L;

        Player playerInput = new Player();
        playerInput.setPlayerTermStatus(null);
        playerInput.setStatus(PlayerStatus.READY);

        testPlayer.setStatus(PlayerStatus.CLUE_GIVER);

        assertThrows(ResponseStatusException.class, () -> playerService.updatePlayer(playerInput, playerId));
        Mockito.verify(playerRepository, Mockito.times(1)).findById(Mockito.any());
    }
}
