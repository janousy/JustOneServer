package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.CONSTANTS;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.RoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class HintValidationService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final RoundRepository roundRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;


    @Autowired
    public HintValidationService(@Qualifier("roundRepository") RoundRepository roundRepository,
                                 @Qualifier("gameRepository") GameRepository gameRepository,
                                 @Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.roundRepository = roundRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }


    //nur testing methode diese von janosch übernehmen grösstenteils
    public Guess guessValidation(Guess guess, Long gameId, Round currentRound) {

        Game currentGame = gameRepository.findGameByGameId(gameId);

        String termContent = currentRound.getTerm().getContent();
        String guessContent = guess.getContent();

        termContent = termContent.replaceAll("\r", "");
        boolean guessTrue = termContent.equalsIgnoreCase(guessContent);

        if (guessTrue) {
            int correctCards = currentGame.getCorrectCards();
            currentGame.setCorrectCards(correctCards + 1);

            String guesscontent = guess.getContent();
            guess.setContent("Guessed correctly " + guesscontent);
        }
        else {
            int currentRoundNr = currentGame.getRoundNr();
            currentGame.setRoundNr(currentRoundNr + 1);

            String guesscontent = guess.getContent();
            guess.setContent("Guessed wrongly " + guesscontent);
        }

        //update the scores of the guessing Player
        updateScoreOfGuesser(guess, gameId);

        return guess;
    }

    //TODO diese methode noch an richtiger stelle aufrufen(nachdem ein guess eingegangen ist)(diese ist nicht schlecht platziert)
    //method updates the score of the guesser
    //param: Guess guess, Long gameId
    //return void
    private void updateScoreOfGuesser(Guess guess, Long gameId) {

        Player guessingPlayer = playerRepository.findByUserToken(guess.getToken());
        int elapsedTime = (int) guessingPlayer.getElapsedTime();
        int earnedPoints;

        //TODO hier noch anpassen mit dem neuen valid/invalid zeugs
        //case if guess correct
        if (guess.getContent() == "correct") {
            earnedPoints = CONSTANTS.MAX_POINTS_PER_ROUND_GUESS - (int) elapsedTime * CONSTANTS.POINT_DEDUCTION_PER_SECOND;

            int oldScore = guessingPlayer.getScore();
            guessingPlayer.setScore(oldScore + earnedPoints);
            playerRepository.save(guessingPlayer);
        }

        //case if skip
        //no points are added or deducted thus the method is not called

        //case of guess incorrect points are deducted
        else {
            earnedPoints = -elapsedTime * CONSTANTS.POINT_DEDUCTION_PER_SECOND;

            int oldScore = guessingPlayer.getScore();
            int newScore = oldScore + earnedPoints;
            if (newScore < 0) {
                newScore = 0;
            }

            guessingPlayer.setScore(newScore);
            playerRepository.save(guessingPlayer);
        }


    }

    //TODO diese methode noch an richtiger stelle aufrufen(am besten nachdem die hints validiert wurden) dann für jeden hint in liste aufrufen
    //method updates the score of the a hint giver
    //param: Hint hint, Long gameId
    //return void
    private void updateScoresOfClue_Giver(Hint hint, Long gameId) {

        Player cluegivingPlayer = playerRepository.findByUserToken(hint.getToken());
        int elapsedTime = (int) cluegivingPlayer.getElapsedTime();
        int earnedPoints;

        //TODO hier noch die neuen felder benutz anstatt diese kacke
        //case if hint is unique/valid
        if (hint.getContent() == "valid") {
            earnedPoints = CONSTANTS.MAX_POINTS_PER_ROUND_HINT - (int) elapsedTime * CONSTANTS.POINT_DEDUCTION_PER_SECOND;

            int oldScore = cluegivingPlayer.getScore();
            cluegivingPlayer.setScore(oldScore + earnedPoints);
            playerRepository.save(cluegivingPlayer);

        }

        //case if hint is invalid
        else {
            earnedPoints = -elapsedTime * CONSTANTS.POINT_DEDUCTION_PER_SECOND;

            int oldScore = cluegivingPlayer.getScore();
            int newScore = oldScore + earnedPoints;
            if (newScore < 0) {
                newScore = 0;
            }

            cluegivingPlayer.setScore(newScore);
            playerRepository.save(cluegivingPlayer);
        }

    }


}
