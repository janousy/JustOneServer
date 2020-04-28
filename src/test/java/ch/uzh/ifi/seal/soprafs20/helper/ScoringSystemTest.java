package ch.uzh.ifi.seal.soprafs20.helper;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.constant.CONSTANTS;
import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoringSystemTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private ScoringSystem scoringSystem;

    private Game testGame;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setName("testGame1");
        testGame.setCorrectCards(0);
        testGame.setStatus(GameStatus.LOBBY);

    }

    @Test
    public void updateScoresOfPlayers_success() {
        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(testGame);
    }

    @Test
    public void updateScoresOfGuesser_GuessValid() {
        Player testPlayer = new Player();
        testPlayer.setStatus(PlayerStatus.GUESSER);
        testPlayer.setElapsedTime(10);
        testPlayer.setScore(50);

        Guess givenGuess = new Guess();
        givenGuess.setStatus(ActionTypeStatus.VALID);

        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Mockito.when(playerRepository.findByUserToken(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        int oldScore = testPlayer.getScore();
        int scoreAdjustment = CONSTANTS.MAX_POINTS_PER_ROUND_GUESS - (10 * CONSTANTS.POINT_DEDUCTION_PER_SECOND);


        Player updatedPlayer = scoringSystem.updateScoreOfGuesser(givenGuess);

        Mockito.verify(playerRepository, Mockito.times(1)).findByUserToken(Mockito.any());
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getId(), updatedPlayer.getId());
        assertEquals(oldScore + scoreAdjustment, updatedPlayer.getScore());

    }

    @Test
    public void updateScoresOfGuesser_GuessInvalid() {
        Player testPlayer = new Player();
        testPlayer.setStatus(PlayerStatus.GUESSER);
        testPlayer.setElapsedTime(10);
        testPlayer.setScore(50);

        Guess givenGuess = new Guess();
        givenGuess.setStatus(ActionTypeStatus.INVALID);

        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Mockito.when(playerRepository.findByUserToken(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        int oldScore = testPlayer.getScore();
        int scoreAdjustment = -(10 * CONSTANTS.POINT_DEDUCTION_PER_SECOND);


        Player updatedPlayer = scoringSystem.updateScoreOfGuesser(givenGuess);

        Mockito.verify(playerRepository, Mockito.times(1)).findByUserToken(Mockito.any());
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getId(), updatedPlayer.getId());
        assertEquals(oldScore + scoreAdjustment, updatedPlayer.getScore());
    }

    @Test
    public void updateScoresOfClueGiver_HintValid() {

    }

    @Test
    public void updateScoresOfClueGiver_HintInvalid() {

    }

    @Test
    public void updateScoresOfUsers_success() {

    }

    @Test
    public void stopTimeForPlayers_success() {

    }

    @Test
    public void startTimeForGuesser_success() {

    }

    @Test
    public void startTimeForClueGivers_success() {

    }

}
