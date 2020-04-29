package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.*;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.helper.HintValidator;
import ch.uzh.ifi.seal.soprafs20.helper.ScoringSystem;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.RoundRepository;
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


public class RoundServiceTest {

    @Mock
    private RoundRepository roundRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private HintValidator hintValidator;
    @Mock
    private ScoringSystem scoringSystem;

    @InjectMocks
    private RoundService roundService;

    private Round round1;
    private Round round2;
    private Game game1;
    private Game game2;
    private Player player1;

    private List<Round> roundList;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        round1 = new Round();
        round1.setId(1L);
        round2 = new Round();
        round2.setId(2L);

        game1 = new Game();
        game1.setGameId(1L);
        game2 = new Game();
        game2.setGameId(2L);

        player1 = new Player();

        roundList = new ArrayList<>();

        Mockito.when(roundRepository.findAll()).thenReturn(roundList);
        Mockito.when(roundRepository.save(Mockito.any())).thenReturn(round1);
    }

    @Test
    public void whenGetAllRoundsOfGame_returnRounds_success() {
        round1.setGame(game1);
        round2.setGame(game2);
        roundList.add(round1);
        roundList.add(round2);

        List<Round> roundsByGameId = roundService.getAllRoundsOfGame(1L);

        assertEquals(1, roundsByGameId.size());
        assertEquals(round1.getId(), roundsByGameId.get(0).getId());
    }

    @Test
    public void whenGetLastRound_NoRounds_throwsExeption() {
        //no rounds added to roundList, thus no round in game startet
        game1.setRoundList(roundList);

        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(game1);
        assertThrows(ResponseStatusException.class, () -> roundService.getLastRoundOfGame(1L));
    }

    @Test
    public void whenGetLastRound_GameFinished_success() {
        //TODO what if 1 round and game not finished
        roundList.add(round1);
        game1.setRoundList(roundList);
        game1.setStatus(GameStatus.FINISHED);

        Mockito.when(gameRepository.findGameByGameId(Mockito.anyLong())).thenReturn(game1);
        Round lastRound = roundService.getLastRoundOfGame(game1.getGameId());
        assertEquals(round1, lastRound);
    }

    @Test
    public void addHintToRound_TokenValid_success() {

        player1.setUserToken("testToken");
        player1.setStatus(PlayerStatus.CLUE_GIVER);

        roundList.add(round1);
        game1.setRoundList(roundList);
        game1.setStatus(GameStatus.RECEIVING_HINTS);
        Hint inputHint = new Hint();

        Hint validatedHint = inputHint;
        validatedHint.setStatus(ActionTypeStatus.VALID);

        Mockito.doAnswer((invocation -> {
            return null;
        })).when(scoringSystem).stopTimeForPlayer(Mockito.any());
        Mockito.when(hintValidator.validateWithExernalResources(inputHint, round1)).thenReturn(validatedHint);
        Mockito.when(playerRepository.findByUserToken(Mockito.any())).thenReturn(player1);
        Mockito.when(gameRepository.findGameByGameId(Mockito.anyLong())).thenReturn(game1);

        Hint createdHint = roundService.addHintToRound(inputHint, game1.getGameId());

        assertEquals(createdHint, round1.getHintList().get(0));
    }
}
