package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.*;
import ch.uzh.ifi.seal.soprafs20.entity.*;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import ch.uzh.ifi.seal.soprafs20.helper.GuessValidator;
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

import java.lang.reflect.Field;
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
    private GuessValidator guessValidator;
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

    @Test
    public void addGuessToRound_TokenValid_success() {

        player1.setUserToken("testToken");
        player1.setStatus(PlayerStatus.GUESSER);

        roundList.add(round1);
        game1.setRoundList(roundList);
        game1.setStatus(GameStatus.RECEIVING_GUESS);
        Guess inputGuess = new Guess();

        Guess validatedGuess = inputGuess;
        validatedGuess.setStatus(ActionTypeStatus.VALID);

        Mockito.doAnswer((invocation -> null)).when(scoringSystem).stopTimeForPlayer(Mockito.any());
        Mockito.when(guessValidator.guessValidationGuessGiven(inputGuess,
                game1.getGameId(), round1)).thenReturn(validatedGuess);
        Mockito.when(playerRepository.findByUserToken(Mockito.any())).thenReturn(player1);
        Mockito.when(gameRepository.findGameByGameId(Mockito.anyLong())).thenReturn(game1);

        Guess createdGuess = roundService.addGuessToRound(inputGuess, game1.getGameId());

        assertEquals(GameStatus.FINISHED, game1.getStatus());
        assertEquals(createdGuess.getRoundId(), round1.getId());
        assertEquals(createdGuess, game1.getRoundList().get(0).getGuess());
        assertEquals(createdGuess, round1.getGuess());
    }

    @Test
    public void addTermToRound_TokenValid_success() {
        player1.setUserToken("testToken");
        player1.setStatus(PlayerStatus.GUESSER);

        player1.setPlayerTermStatus(PlayerTermStatus.KNOWN);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);

        Card card = new Card();
        card.setWord1("testTerm1");
        card.setWord2("testTerm2");
        card.setWord3("testTerm3");
        card.setWord4("testTerm4");
        card.setWord5("testTerm5");
        round1.setCard(card);
        roundList.add(round1);
        game1.setRoundList(roundList);
        game1.setStatus(GameStatus.RECEIVING_TERM);

        Term inputTerm = new Term();
        inputTerm.setWordId(0L); //setting testTerm1

        Mockito.doAnswer((invocation -> null)).when(scoringSystem).startTimeForClue_Givers(Mockito.any());
        Mockito.when(playerRepository.findByUserToken(Mockito.any())).thenReturn(player1);
        Mockito.when(gameRepository.findGameByGameId(Mockito.anyLong())).thenReturn(game1);
        Mockito.when(playerRepository.findByGameGameId(Mockito.anyLong())).thenReturn(playerList);

        Term createdTerm = roundService.addTermToRound(inputTerm, game1.getGameId());

        assertEquals(createdTerm, game1.getRoundList().get(0).getTerm());
        assertEquals(createdTerm, round1.getTerm());
        assertEquals(createdTerm.getRoundId(), round1.getId());
        assertEquals(createdTerm.getContent(), game1.getRoundList().get(0).getTerm().getContent());
        assertEquals("testTerm1", game1.getRoundList().get(0).getTerm().getContent());
        assertEquals(PlayerTermStatus.NOT_SET, player1.getPlayerTermStatus());
        assertEquals(GameStatus.VALIDATING_TERM, game1.getStatus());
    }

    @Test
    public void givenRoundWithHints_getAllHintsFromRound() {
        Hint testHint = new Hint();
        List<Hint> hintList = new ArrayList<>();
        hintList.add(testHint);
        round1.setHintList(hintList);
        roundList.add(round1);
        game1.setRoundList(roundList);

        Mockito.when(gameRepository.findGameByGameId(Mockito.anyLong())).thenReturn(game1);
        List<Hint> returnedHints = roundService.getAllHintsFromRound(game1.getGameId());
        assertEquals(hintList, returnedHints);
        assertEquals(hintList, round1.getHintList());
    }

    @Test
    public void givenRoundWithGuess_getCurrentGuessFromRound() {
        Guess testGuess = new Guess();
        round1.setGuess(testGuess);
        roundList.add(round1);
        game1.setRoundList(roundList);

        Mockito.when(gameRepository.findGameByGameId(Mockito.anyLong())).thenReturn(game1);
        Guess currentGuess = roundService.getGuessOfCurrentRound(game1.getGameId());
        assertEquals(testGuess, currentGuess);
        assertEquals(testGuess, round1.getGuess());
    }

    @Test
    public void givenRoundWithTerm_getCurrentTermFromRound() {
        Term testTerm = new Term();
        round1.setTerm(testTerm);
        roundList.add(round1);
        game1.setRoundList(roundList);

        Mockito.when(gameRepository.findGameByGameId(Mockito.anyLong())).thenReturn(game1);
        Term currentTerm = roundService.getCurrentTermFromRound(game1.getGameId());
        assertEquals(testTerm, currentTerm);
        assertEquals(testTerm, round1.getTerm());
    }

    @Test
    public void givenRoundWithHint_updateHint_success() {
        Hint hint1 = new Hint();
        Hint hint2 = new Hint();

    }
}
