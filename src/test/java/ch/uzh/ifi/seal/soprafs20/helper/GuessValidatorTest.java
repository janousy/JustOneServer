package ch.uzh.ifi.seal.soprafs20.helper;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuessValidatorTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GuessValidator guessValidator;

    private Game testGame;
    private Round testRound;
    private Card card1;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setName("testGame1");
        testGame.setCorrectCards(0);
        testGame.setStatus(GameStatus.LOBBY);

        card1 = new Card();
        card1.setId(1L);
        card1.setWord1("word1");
        card1.setWord2("word2");
        card1.setWord3("word3");
        card1.setWord4("word4");
        card1.setWord5("word5");

        Card card2 = new Card();
        card2.setId(1L);
        card2.setWord1("word1_2");
        card2.setWord2("word2_2");
        card2.setWord3("word3_2");
        card2.setWord4("word4_2");
        card2.setWord5("word5_2");

        testGame.addCard(card1);
        testGame.addCard(card2);

        Term term = new Term();
        term.setContent("word1");
        term.setWordId(1L);

        testRound = new Round();
        testRound.setId(1L);
        testRound.setCard(card1);
        testRound.setTerm(term);

        testGame.addRound(testRound);


        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(testGame);
    }

    @Test
    public void guessValidationGuessGiven_guessTrue() {

        Guess givenGuess = new Guess();
        givenGuess.setContent("word1");

        int oldNumberOfCorrectCards = testGame.getCorrectCards();
        int oldSizeOfCardList = testGame.getCardList().size();

        Guess validatedGuess = guessValidator.guessValidationGuessGiven(givenGuess, testGame.getGameId(), testRound);

        Mockito.verify(gameRepository, Mockito.times(1)).findGameByGameId(Mockito.any());

        assertEquals(oldNumberOfCorrectCards + 1, testGame.getCorrectCards());
        assertEquals(oldSizeOfCardList - 1, testGame.getCardList().size());
        assertEquals(ActionTypeStatus.VALID, validatedGuess.getStatus());
    }

    @Test
    public void guessValidationGuessGiven_guessTrue_UpperCaseAndBlankSpace() {

        Guess givenGuess = new Guess();
        givenGuess.setContent("WORD 1    ");

        int oldNumberOfCorrectCards = testGame.getCorrectCards();
        int oldSizeOfCardList = testGame.getCardList().size();

        Guess validatedGuess = guessValidator.guessValidationGuessGiven(givenGuess, testGame.getGameId(), testRound);

        Mockito.verify(gameRepository, Mockito.times(1)).findGameByGameId(Mockito.any());

        assertEquals(oldNumberOfCorrectCards + 1, testGame.getCorrectCards());
        assertEquals(oldSizeOfCardList - 1, testGame.getCardList().size());
        assertEquals(ActionTypeStatus.VALID, validatedGuess.getStatus());

    }

    @Test
    public void guessValidationGuessGiven_guessTrue_cardListEmpty() {

        Guess givenGuess = new Guess();
        givenGuess.setContent("word1");

        testGame.setCardList(new ArrayList<Card>());

        int oldNumberOfCorrectCards = testGame.getCorrectCards();
        int oldSizeOfCardList = testGame.getCardList().size();

        Guess validatedGuess = guessValidator.guessValidationGuessGiven(givenGuess, testGame.getGameId(), testRound);

        Mockito.verify(gameRepository, Mockito.times(1)).findGameByGameId(Mockito.any());

        assertEquals(oldNumberOfCorrectCards + 1, testGame.getCorrectCards());
        assertEquals(oldSizeOfCardList, testGame.getCardList().size());
        assertEquals(ActionTypeStatus.VALID, validatedGuess.getStatus());
    }

    @Test
    public void guessValidationGuessGiven_guessFalse_NotLastCard() {

        Guess givenGuess = new Guess();
        givenGuess.setContent("falseGuess");

        int oldNumberOfCorrectCards = testGame.getCorrectCards();
        int oldSizeOfCardList = testGame.getCardList().size();

        Guess validatedGuess = guessValidator.guessValidationGuessGiven(givenGuess, testGame.getGameId(), testRound);

        Mockito.verify(gameRepository, Mockito.times(1)).findGameByGameId(Mockito.any());

        assertEquals(oldNumberOfCorrectCards, testGame.getCorrectCards());
        assertEquals(oldSizeOfCardList - 2, testGame.getCardList().size());
        assertEquals(ActionTypeStatus.INVALID, validatedGuess.getStatus());
    }

    @Test
    public void guessValidationGuessGiven_guessFalse_LastCard() {
        Guess givenGuess = new Guess();
        givenGuess.setContent("falseGuess");
        testGame.removeCard(card1);
        testGame.setCorrectCards(5);

        int oldNumberOfCorrectCards = testGame.getCorrectCards();
        int oldSizeOfCardList = testGame.getCardList().size();

        Guess validatedGuess = guessValidator.guessValidationGuessGiven(givenGuess, testGame.getGameId(), testRound);

        Mockito.verify(gameRepository, Mockito.times(1)).findGameByGameId(Mockito.any());

        assertEquals(oldNumberOfCorrectCards - 1, testGame.getCorrectCards());
        assertEquals(oldSizeOfCardList - 1, testGame.getCardList().size());
        assertEquals(ActionTypeStatus.INVALID, validatedGuess.getStatus());
    }

    @Test
    public void guessValidationGuessSkipped_guessSkipped() {
        int oldNumberOfCorrectCards = testGame.getCorrectCards();
        int oldSizeOfCardList = testGame.getCardList().size();

        guessValidator.guessValidationGuessSkipped(testGame.getGameId());

        Mockito.verify(gameRepository, Mockito.times(1)).findGameByGameId(Mockito.any());

        assertEquals(oldNumberOfCorrectCards, testGame.getCorrectCards());
        assertEquals(oldSizeOfCardList - 1, testGame.getCardList().size());
    }

}
