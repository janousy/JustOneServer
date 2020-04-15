package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.CONSTANTS;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.repository.RoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class RoundService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final RoundRepository roundRepository;
    private final GameRepository gameRepository;


    @Autowired
    public RoundService(@Qualifier("roundRepository") RoundRepository roundRepository, @Qualifier("gameRepository") GameRepository gameRepository) {
        this.roundRepository = roundRepository;
        this.gameRepository = gameRepository;
    }

    //get all rounds as a list
    //param:
    //return: returns a List<Round> with all rounds in it
    public List<Round> getAllRounds() {
        return this.roundRepository.findAll();
    }


    //TODO: restrict number of hints accorinding to number of players
    //TODO: check status of game such that it is actually acepting hints
    public Hint addHintToRound(Hint hint, Long gameId) {
        Round currentRound = findRoundByGameId(gameId);
        hint.setRoundId(currentRound.getId()); //TODO neccessary to set ID?
        currentRound.addHint(hint);
        roundRepository.save(currentRound); //TODO check if save is necessary on entitiy update
        return hint;
    }

    public List<Hint> getAllHintsFromRound(Long gameId) {
        Round currentRound = findRoundByGameId(gameId);
        return currentRound.getHintList();
    }

    public Guess addGuessToRound(Guess guess, Long gameId) {
        Round currentRound = findRoundByGameId(gameId);
        guess.setRoundId(currentRound.getId());
        currentRound.setGuess(guess);
        return guess;
    }

    public Guess getGuessOfCurrentRound(Long gameId) {
        Round currentRound = findRoundByGameId(gameId);
        return currentRound.getGuess();
    }

    public Term getCurrentTermFromRound(Long gameId) {
        Round currentRound = findRoundByGameId(gameId);
        return currentRound.getTerm();
    }

    public Term addTermToRound(Long gameId, Long wordId) {

        Round currentRound = findRoundByGameId(gameId);
        String[] wordsOfCards = currentRound.getCard().getTerms();
        int relWordId = Math.toIntExact(wordId) - 1;

        if (relWordId >= 0 && relWordId < CONSTANTS.MAX_WORDS_PER_CARD) {
            Term newTerm = new Term();
            newTerm.setContent(wordsOfCards[Math.toIntExact(relWordId)]);
            newTerm.setWordId((long) relWordId);
            newTerm.setRoundId(currentRound.getId());

            currentRound.setTerm(newTerm);
            return newTerm;

        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid wordId");
        }
    }


/*
    //method returns the rounds which belong to a game by the gameId
    //param: Long gameId
    //return: returns all rounds which belong to a game
    public List<Round> getAllRoundsOfGame(Long gameId) {

        List<Round> allRounds = roundRepository.findAll();
        List<Round> rounds = new ArrayList<Round>();

        for(Round r : allRounds){
            if(r.getGame().getGameId().equals(gameId)){
                rounds.add(r);
            }
        }

        return rounds;
    }

 */



    public Round addRound(Long gameId) {


        return null;
    }

    //add a new Round to a Game
    //param: game Game
    //return: returns the newly created round
    public Game addRoundToGame(Game game, Card card) {

        Round newRound = new Round();
        newRound.setCard(card);

        game.addRound(newRound);

        newRound = roundRepository.save(newRound);

        return game;
    }

    private Round findRoundByGameId(Long gameId) {
        Game game = gameRepository.findGameByGameId(gameId);
        int indexOfCurrentRound = game.getRoundNr() - 1;

        return game.getRoundList().get(indexOfCurrentRound);
    }


}