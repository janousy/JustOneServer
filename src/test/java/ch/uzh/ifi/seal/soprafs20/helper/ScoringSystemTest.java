package ch.uzh.ifi.seal.soprafs20.helper;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.constant.CONSTANTS;
import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ScoringSystemTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private ScoringSystem scoringSystem;

    private Game testGame;
    private Player testPlayer;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setName("testGame1");
        testGame.setCorrectCards(0);
        testGame.setStatus(GameStatus.LOBBY);

        testPlayer = new Player();
        testPlayer.setStatus(PlayerStatus.GUESSER);
        testPlayer.setElapsedTime(10);
        testPlayer.setScore(50);
        testPlayer.setUserToken("abcdf1");

        testGame.addPlayer(testPlayer);

    }

    @Test
    public void updateScoresOfGuesser_GuessValid() {

        //preparation for the test
        Guess givenGuess = new Guess();
        givenGuess.setStatus(ActionTypeStatus.VALID);

        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Mockito.when(playerRepository.findByUserToken(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        int oldScore = testPlayer.getScore();
        int scoreAdjustment = (int) (CONSTANTS.MAX_POINTS_PER_ROUND_GUESS - (10 * CONSTANTS.POINT_DEDUCTION_PER_SECOND));
        int expectedScore = oldScore + scoreAdjustment;

        //invoke the testMethod
        scoringSystem.updateScoreOfGuesser(givenGuess);

        //then
        Mockito.verify(playerRepository, Mockito.times(1)).findByUserToken(Mockito.any());
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(expectedScore, testPlayer.getScore());

    }

    @Test
    public void updateScoresOfGuesser_GuessInvalid() {

        //preparation for the test
        Guess givenGuess = new Guess();
        givenGuess.setStatus(ActionTypeStatus.INVALID);


        Mockito.when(playerRepository.findByUserToken(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        int oldScore = testPlayer.getScore();
        int scoreAdjustment = (int) -(10 * CONSTANTS.POINT_DEDUCTION_PER_SECOND);
        int expectedScore = oldScore + scoreAdjustment;

        //invoke the testMethod
        scoringSystem.updateScoreOfGuesser(givenGuess);

        //then
        Mockito.verify(playerRepository, Mockito.times(1)).findByUserToken(Mockito.any());
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(expectedScore, testPlayer.getScore());
    }

    @Test
    public void updateScoresOfClueGiver_HintValid() {

        //preparation for the test
        Hint givenHint = new Hint();
        givenHint.setStatus(ActionTypeStatus.INVALID);

        Mockito.when(playerRepository.findByUserToken(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        int oldScore = testPlayer.getScore();
        int scoreAdjustment = (int) (-10 * CONSTANTS.POINT_DEDUCTION_PER_SECOND);
        int expectedScore = oldScore + scoreAdjustment;


        //invoke the testMethod
        scoringSystem.updateScoreOfClue_Giver(givenHint);

        //then
        Mockito.verify(playerRepository, Mockito.times(1)).findByUserToken(Mockito.any());
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(expectedScore, testPlayer.getScore());
    }

    @Test
    public void updateScoresOfClueGiver_HintInvalid() {

        //preparation for the test
        Hint givenHint = new Hint();
        givenHint.setStatus(ActionTypeStatus.VALID);

        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Mockito.when(playerRepository.findByUserToken(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        int oldScore = testPlayer.getScore();
        int scoreAdjustment = (int) (CONSTANTS.MAX_POINTS_PER_ROUND_HINT - 10 * CONSTANTS.POINT_DEDUCTION_PER_SECOND);
        int expectedScore = oldScore + scoreAdjustment;

        //invoke the testMethod
        scoringSystem.updateScoreOfClue_Giver(givenHint);

        //then
        Mockito.verify(playerRepository, Mockito.times(1)).findByUserToken(Mockito.any());
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(expectedScore, testPlayer.getScore());
    }

    @Test
    public void updateScoresOfUsers_ScoreLargerThanZero() {

        //preparation for the test
        User testUser = new User();
        testUser.setToken("abcdf1");

        testPlayer.setUser(testUser);

        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);

        //invoke the testMethod
        scoringSystem.updateScoresOfUsers(testGame);


        //then
        Mockito.verify(userRepository, Mockito.times(1)).findByToken(Mockito.any());

        assertEquals(testPlayer.getScore(), testUser.getOverallScore());
    }

    @Test
    public void updateScoresOfUsers_ScoreSmallerThanZero() {

        //preparation for the test
        testPlayer.setScore(-10);

        User testUser = new User();
        testUser.setToken("abcdf1");

        testPlayer.setUser(testUser);
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);

        //invoke the testMethod
        scoringSystem.updateScoresOfUsers(testGame);


        //then
        Mockito.verify(userRepository, Mockito.times(1)).findByToken(Mockito.any());

        assertEquals(-10, testUser.getOverallScore());
    }

    @Test
    public void stopTimeForPlayers_success() {

        Guess guess = new Guess();
        guess.setStatus(ActionTypeStatus.VALID);
        guess.setToken(testPlayer.getUserToken());

        long currentTime = System.currentTimeMillis();
        testPlayer.setElapsedTime(currentTime);

        Mockito.when(playerRepository.findByUserToken(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        long oldTimeOfPlayer = testPlayer.getElapsedTime();

        //invoke the testMethod
        scoringSystem.stopTimeForPlayer(guess);

        //then
        Mockito.verify(playerRepository, Mockito.times(1)).findByUserToken(Mockito.any());
        Mockito.verify(playerRepository, Mockito.times(1)).findByUserToken(Mockito.any());

        assertNotEquals(oldTimeOfPlayer, testPlayer.getElapsedTime());
    }

    @Test
    public void startTimeForGuesser_success() {

        testPlayer.setStatus(PlayerStatus.GUESSER);

        List<Player> playerList = testGame.getPlayerList();

        Mockito.when(playerRepository.findByGameGameId(Mockito.any())).thenReturn(playerList);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        long oldTimeOfPlayer = testPlayer.getElapsedTime();

        //invoke the testMethod
        scoringSystem.startTimeForGuesser(testGame.getGameId());

        //then
        Mockito.verify(playerRepository, Mockito.times(1)).findByGameGameId(Mockito.any());
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertNotEquals(oldTimeOfPlayer, testPlayer.getElapsedTime());
    }

    @Test
    public void startTimeForGuesser_NoGuesser() {

        testPlayer.setStatus(PlayerStatus.CLUE_GIVER);

        List<Player> playerList = testGame.getPlayerList();

        Mockito.when(playerRepository.findByGameGameId(Mockito.any())).thenReturn(playerList);

        long oldTimeOfPlayer = testPlayer.getElapsedTime();

        //invoke the testMethod
        scoringSystem.startTimeForGuesser(testGame.getGameId());

        //then
        Mockito.verify(playerRepository, Mockito.times(1)).findByGameGameId(Mockito.any());

        assertEquals(oldTimeOfPlayer, testPlayer.getElapsedTime());
    }

    @Test
    public void startTimeForClueGivers_success() {

        testPlayer.setStatus(PlayerStatus.CLUE_GIVER);

        List<Player> playerList = testGame.getPlayerList();

        Mockito.when(playerRepository.findByGameGameId(Mockito.any())).thenReturn(playerList);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        long oldTimeOfPlayer = testPlayer.getElapsedTime();

        //invoke the testMethod
        scoringSystem.startTimeForClue_Givers(testGame.getGameId());

        //then
        Mockito.verify(playerRepository, Mockito.times(1)).findByGameGameId(Mockito.any());
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertNotEquals(oldTimeOfPlayer, testPlayer.getElapsedTime());

    }

    @Test
    public void startTimeForClue_Givers_NoClueGiver() {

        testPlayer.setStatus(PlayerStatus.GUESSER);

        List<Player> playerList = testGame.getPlayerList();

        Mockito.when(playerRepository.findByGameGameId(Mockito.any())).thenReturn(playerList);

        long oldTimeOfPlayer = testPlayer.getElapsedTime();

        //invoke the testMethod
        scoringSystem.startTimeForClue_Givers(testGame.getGameId());

        //then
        Mockito.verify(playerRepository, Mockito.times(1)).findByGameGameId(Mockito.any());

        assertEquals(oldTimeOfPlayer, testPlayer.getElapsedTime());
    }

}
