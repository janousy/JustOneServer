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


class RoundServiceTest {

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
    void whenGetAllRoundsOfGame_returnRounds_success() {
        round1.setGame(game1);
        round2.setGame(game2);
        roundList.add(round1);
        roundList.add(round2);

        List<Round> roundsByGameId = roundService.getAllRoundsOfGame(1L);

        assertEquals(1, roundsByGameId.size());
        assertEquals(round1.getId(), roundsByGameId.get(0).getId());
    }

    @Test
    void whenGetLastRound_NoRounds_throwsExeption() {
        //no rounds added to roundList, thus no round in game startet
        game1.setRoundList(roundList);

        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(game1);
        assertThrows(ResponseStatusException.class, () -> roundService.getLastRoundOfGame(1L));
    }

    @Test
    void whenGetLastRound_GameFinished_success() {
        roundList.add(round1);
        game1.setRoundList(roundList);
        game1.setStatus(GameStatus.FINISHED);

        Mockito.when(gameRepository.findGameByGameId(Mockito.anyLong())).thenReturn(game1);
        Round lastRound = roundService.getLastRoundOfGame(game1.getGameId());
        assertEquals(round1, lastRound);
    }

    @Test
    void addHintToRound_TokenValid_success() {

        player1.setUserToken("testToken");
        player1.setStatus(PlayerStatus.CLUE_GIVER);

        roundList.add(round1);
        game1.setRoundList(roundList);
        game1.setStatus(GameStatus.RECEIVING_HINTS);
        Hint inputHint = new Hint();

        Hint validatedHint = inputHint;
        validatedHint.setStatus(ActionTypeStatus.VALID);

        Mockito.doAnswer((invocation -> null)).when(scoringSystem).stopTimeForPlayer(Mockito.any());
        Mockito.when(hintValidator.validateWithExernalResources(inputHint, round1.getTerm(), "wordnet")).thenReturn(validatedHint);
        Mockito.when(playerRepository.findByUserToken(Mockito.any())).thenReturn(player1);
        Mockito.when(gameRepository.findGameByGameId(Mockito.anyLong())).thenReturn(game1);


        Hint createdHint = roundService.addHintToRound(inputHint, game1.getGameId());

        assertEquals(createdHint, round1.getHintList().get(0));
    }

    @Test
    void addGuessToRound_TokenValid_success() {

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
    void addTermToRound_TokenValid_success() {
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

        Mockito.doAnswer((invocation -> null)).when(scoringSystem).startTimeForClueGivers(Mockito.any());
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
    void givenRoundWithHints_getAllHintsFromRound() {
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
    void givenRoundWithGuess_getCurrentGuessFromRound() {
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
    void givenRoundWithTerm_getCurrentTermFromRound() {
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
    void givenRoundWithHint_updateHint_success() {
        ArrayList<Integer> similarities = new ArrayList<>();
        ArrayList<String> reporter = new ArrayList<>();
        reporter.add("testToken");
        similarities.add(1);

        Hint hint1 = new Hint();
        hint1.setToken("testToken");
        Hint hint2 = new Hint();
        Hint inputHint = new Hint();
        inputHint.setSimilarity(similarities);
        inputHint.setToken("testToken");
        inputHint.setReporters(reporter);

        List<Hint> currentHints = new ArrayList<>();
        currentHints.add(hint1);
        currentHints.add(hint2);

        List<Player> playerList = new ArrayList<>();
        player1.setUserToken("testToken");
        player1.setStatus(PlayerStatus.CLUE_GIVER);
        playerList.add(player1);
        inputHint.setToken("testToken");

        round1.setHintList(currentHints);
        List<Round> roundList = new ArrayList<>();
        roundList.add(round1);
        game1.setRoundList(roundList);
        game1.setStatus(GameStatus.VALIDATING_HINTS);

        Mockito.when(playerRepository.findByGameGameId(game1.getGameId())).thenReturn(playerList);
        Mockito.when(gameRepository.findGameByGameId(1L)).thenReturn(game1);
        Mockito.when(playerRepository.findByUserToken(player1.getUserToken())).thenReturn(player1);

        Hint updatedHint = roundService.updateHint(inputHint, game1.getGameId());

        assertEquals(currentHints.size(), round1.getHintList().size());
        assertEquals(GameStatus.VALIDATING_HINTS, game1.getStatus());
        assertEquals(similarities, updatedHint.getSimilarity());
        assertEquals(reporter, updatedHint.getReporters());
    }

    @Test
    void givenTerm_skipTermByGuesser_success() {
        player1.setUserToken("testToken");
        player1.setStatus(PlayerStatus.GUESSER);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);

        Card card = new Card();
        List<Card> cardList = new ArrayList<>();
        cardList.add(card);

        Guess inputGuess = new Guess();
        Guess currentGuess = new Guess();
        inputGuess.setToken("testToken");
        round1.setGuess(currentGuess);

        List<Round> roundList = new ArrayList<>();
        roundList.add(round1);
        game1.setRoundList(roundList);
        game1.setCardList(cardList);
        game1.setStatus(GameStatus.RECEIVING_GUESS);
        game1.setPlayerList(playerList);

        Mockito.when(gameRepository.findGameByGameId(game1.getGameId())).thenReturn(game1);
        Mockito.when(playerRepository.findByUserToken("testToken")).thenReturn(player1);
        Mockito.doAnswer((invocation -> null)).when(guessValidator).guessValidationGuessSkipped(Mockito.any());

        Game updatedGame = roundService.skipTermToBeGuessed(inputGuess, game1.getGameId());

        assertEquals(2, updatedGame.getRoundList().size());
        assertEquals(2, updatedGame.getRoundList().size());
    }
}

