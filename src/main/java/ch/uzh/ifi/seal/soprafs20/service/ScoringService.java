package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.constant.CONSTANTS;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.actions.ActionType;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ScoringService {

    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;

    @Autowired
    public ScoringService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                          @Qualifier("userRepository") UserRepository userRepository) {
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
    }


    //method updates the score of the guesser, adds points for valid guess, deducts points for invalid guess and does nothing if skipped
    //param: Guess guess, Long gameId
    //return void
    public void updateScoreOfGuesser(Guess guess, Long gameId) {

        Player guessingPlayer = playerRepository.findByUserToken(guess.getToken());
        int elapsedTime = (int) guessingPlayer.getElapsedTime();
        int earnedPoints;

        //case if guess correct
        if (guess.getStatus() == ActionTypeStatus.VALID) {
            earnedPoints = CONSTANTS.MAX_POINTS_PER_ROUND_GUESS - (int) elapsedTime * CONSTANTS.POINT_DEDUCTION_PER_SECOND;

            //to assure that the guesser gets at least 0 points and not negative if too slow
            if (earnedPoints < 0) {
                earnedPoints = 0;
            }

            int oldScore = guessingPlayer.getScore();
            guessingPlayer.setScore(oldScore + earnedPoints);
            playerRepository.save(guessingPlayer);
        }
        //case of guess incorrect points are deducted
        else {
            earnedPoints = -elapsedTime * CONSTANTS.POINT_DEDUCTION_PER_SECOND;

            if (earnedPoints < CONSTANTS.MAX_POINTS_PER_ROUND_GUESS * (-1)) {
                earnedPoints = CONSTANTS.MAX_POINTS_PER_ROUND_GUESS * (-1);
            }

            int oldScore = guessingPlayer.getScore();
            int newScore = oldScore + earnedPoints;

            guessingPlayer.setScore(newScore);
            playerRepository.save(guessingPlayer);
        }
    }


    //TODO diese methode noch an richtiger stelle aufrufen(am besten nachdem die hints validiert wurden) dann für jeden hint in liste aufrufen
    //method updates the score of the a hint giver
    //param: Hint hint, Long gameId
    //return void
    public void updateScoreOfClue_Giver(Hint hint, Long gameId) {

        Player cluegivingPlayer = playerRepository.findByUserToken(hint.getToken());
        int elapsedTime = (int) cluegivingPlayer.getElapsedTime();
        int earnedPoints;

        //case if hint is unique/valid
        if (hint.getStatus() == ActionTypeStatus.VALID) {
            earnedPoints = CONSTANTS.MAX_POINTS_PER_ROUND_HINT - (int) elapsedTime * CONSTANTS.POINT_DEDUCTION_PER_SECOND;

            //to assure that the guesser gets at least 0 points and not negative if too slow
            if (earnedPoints < 0) {
                earnedPoints = 0;
            }

            int oldScore = cluegivingPlayer.getScore();
            cluegivingPlayer.setScore(oldScore + earnedPoints);
            playerRepository.save(cluegivingPlayer);

        }

        //case if hint is invalid
        else {
            earnedPoints = -elapsedTime * CONSTANTS.POINT_DEDUCTION_PER_SECOND;

            if (earnedPoints < CONSTANTS.MAX_POINTS_PER_ROUND_GUESS * (-1)) {
                earnedPoints = CONSTANTS.MAX_POINTS_PER_ROUND_GUESS * (-1);
            }

            int oldScore = cluegivingPlayer.getScore();
            int newScore = oldScore + earnedPoints;

            cluegivingPlayer.setScore(newScore);
            playerRepository.save(cluegivingPlayer);
        }
    }

    //method updates the scores of all users at the end of a game
    //param: Game game
    //return: void
    public void updateScoresOfUsers(Game game) {

        List<Player> playerList = game.getPlayerList();

        for (Player p : playerList) {
            int newScore = p.getScore();
            if (newScore < 0) {
                newScore = 0;
            }

            //find the user and set the score
            User userOfP = userRepository.findByToken(p.getUserToken());
            int oldScore = userOfP.getOverallScore();
            userOfP.setOverallScore(oldScore + newScore);
        }

    }

    //method stops the time and calculates the elapsed time
    //param: ActionType action
    //return: void
    public void stopTimeForPlayer(ActionType action) {

        Player player = playerRepository.findByUserToken(action.getToken());

        long currentTime = System.currentTimeMillis();
        long startingTime = player.getElapsedTime();

        long elapsedTime = (currentTime - startingTime) / 1000;
        player.setElapsedTime(elapsedTime);
        playerRepository.save(player);
    }

    //method sets the current time for the player
    //param: ActionType action
    //return: void
    public void startTimeForGuesser(Long gameId) {
        //find all the players of the game
        List<Player> playerList = playerRepository.findByGameGameId(gameId);

        //set the starting time for the guesser
        for (Player p : playerList) {
            if (p.getStatus() == PlayerStatus.GUESSER) {
                p.setElapsedTime(System.currentTimeMillis());
                playerRepository.save(p);
            }
        }
    }

    //method sets the current time for all clue_givers
    //param: ActionType action
    //return: void
    public void startTimeForClue_Givers(Long gameId) {
        //find all the players of the game
        List<Player> playerList = playerRepository.findByGameGameId(gameId);

        //set starting time for all clue givers
        for (Player p : playerList) {
            if (p.getStatus() == PlayerStatus.CLUE_GIVER) {
                p.setElapsedTime(System.currentTimeMillis());
                playerRepository.save(p);
            }
        }
    }

}
