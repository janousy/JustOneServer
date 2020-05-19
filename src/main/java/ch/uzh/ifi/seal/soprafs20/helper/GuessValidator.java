package ch.uzh.ifi.seal.soprafs20.helper;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class GuessValidator {

    private final GameRepository gameRepository;

    @Autowired
    public GuessValidator(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Guess guessValidationGuessGiven(Guess guess, Long gameId, Round currentRound) {

        Game currentGame = gameRepository.findGameByGameId(gameId);
        List<Card> cardList = currentGame.getCardList();

        String termContent = currentRound.getTerm().getContent();
        String guessContent = guess.getContent();

        //check if the guess and the term match
        termContent = termContent.replaceAll("\r", "");
        termContent = termContent.replaceAll("\\s", "");

        guessContent = guessContent.replaceAll("\r", "");
        guessContent = guessContent.replaceAll("\\s", "");
        boolean guessTrue = termContent.equalsIgnoreCase(guessContent);

        if (guessTrue) {
            int correctCards = currentGame.getCorrectCards();

            //add one to the correct cards number
            currentGame.setCorrectCards(correctCards + 1);
            guess.setStatus(ActionTypeStatus.VALID);
        }
        else {
            //special behaviour if the card was the last we deduct 1 of the correct cards
            if (cardList.size() == 1) {
                int correctCards = currentGame.getCorrectCards();
                if (correctCards > 0) {
                    currentGame.setCorrectCards(correctCards - 1);
                }
            }

            if (!cardList.isEmpty()) {
                cardList.remove(0);
            }

            guess.setStatus(ActionTypeStatus.INVALID);
        }

        if (!cardList.isEmpty()) {
            cardList.remove(0);
        }

        return guess;
    }


    public void guessValidationGuessSkipped(Long gameId) {

        Game game = gameRepository.findGameByGameId(gameId);
        List<Card> cardList = game.getCardList();
        if (!cardList.isEmpty()) {
            cardList.remove(0);
        }
    }

}
