package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.constant.CONSTANTS;
import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.RoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.codec.Hints;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RoundService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final RoundRepository roundRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final HintValidationService hintValidationService;
    private final GuessValidationService guessValidationService;


    @Autowired
    public RoundService(@Qualifier("roundRepository") RoundRepository roundRepository,
                        @Qualifier("gameRepository") GameRepository gameRepository,
                        @Qualifier("playerRepository") PlayerRepository playerRepository,
                        HintValidationService hintValidationService,
                        GuessValidationService guessValidationService) {
        this.roundRepository = roundRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.hintValidationService = hintValidationService;
        this.guessValidationService = guessValidationService;
    }

    //get all rounds as a list
    //param:
    //return: returns a List<Round> with all rounds in it
    public List<Round> getAllRounds() {
        return this.roundRepository.findAll();
    }

    public Hint addHintToRound(Hint hint, Long gameId) {
        checkIfTokenValid(hint.getToken(), PlayerStatus.CLUE_GIVER);
        validateGameState(GameStatus.RECEIVINGHINTS, gameId);

        Round currentRound = roundRepository.findRoundByGameGameId(gameId);
        hint.setRoundId(currentRound.getId()); //TODO neccessary to set ID?
        hint.setStatus(ActionTypeStatus.UNKNOWN);
        hint.setMarked(ActionTypeStatus.UNKNOWN);
        currentRound.addHint(hint);
        roundRepository.save(currentRound); //TODO check if save is necessary on entitiy update

        Game game = gameRepository.findGameByGameId(gameId);
        int nrOfPlayers = game.getPlayerList().size();
        int nrOfHints = currentRound.getHintList().size();

        //TODO hier nur eine primitive version von check hints um spielfluss zu gewährleisten, anpassen auf etwas anderes evtl
        if (nrOfHints == (nrOfPlayers - 1)) {
            game.setStatus(GameStatus.VALIDATION);
            gameRepository.save(game);
        }
        return hint;
    }

    public Guess addGuessToRound(Guess guess, Long gameId) {
        checkIfTokenValid(guess.getToken(), PlayerStatus.GUESSER);
        validateGameState(GameStatus.RECEIVINGGUESS, gameId);

        Round currentRound = findRoundByGameId(gameId);
        guess.setRoundId(currentRound.getId());
        guess.setStatus(ActionTypeStatus.UNKNOWN);
        currentRound.setGuess(guess);

        Guess validatedGuess = guessValidationService.guessValidation(guess, currentRound);

        //TODO dieses behaviour evtl in andere methode ausbauen damit diese nicht so gross ist
        Game currentGame = gameRepository.findGameByGameId(gameId);
        //this means that the guess is correct and we add some points
        if (validatedGuess.getStatus().equals(ActionTypeStatus.VALID)) {
            int correctCards = currentGame.getCorrectCards();
            currentGame.setCorrectCards(correctCards + 1);
            addRound(currentGame);
        }
        else {
            int currentRoundNr = currentGame.getRoundNr();
            currentGame.setRoundNr(currentRoundNr + 1);
            addRound(currentGame);
        }
        return validatedGuess;
    }

    public Term addTermToRound(Term newTerm, Long gameId) {
        checkIfTokenValid(newTerm.getToken(), PlayerStatus.GUESSER);
        validateGameState(GameStatus.RECEIVINGTERM, gameId);

        Round currentRound = roundRepository.findRoundByGameGameId(gameId);
        String[] wordsOfCards = currentRound.getCard().getTerms();
        int relWordId = Math.toIntExact(newTerm.getWordId());

        if (relWordId >= 0 && relWordId < CONSTANTS.MAX_WORDS_PER_CARD) {
            newTerm.setContent(wordsOfCards[Math.toIntExact(relWordId)]);
            newTerm.setWordId((long) relWordId);
            newTerm.setRoundId(currentRound.getId());

            //setting the gamestatus to receiving hints
            Game game = gameRepository.findGameByGameId(gameId);
            game.setStatus(GameStatus.RECEIVINGHINTS);
            gameRepository.save(game);

            currentRound.setTerm(newTerm);
            return newTerm;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid wordId");
        }
    }

    public List<Hint> getAllHintsFromRound(Long gameId) {
        Round currentRound = findRoundByGameId(gameId);
        return currentRound.getHintList();
    }

    public Guess getGuessOfCurrentRound(Long gameId) {
        Round currentRound = findRoundByGameId(gameId);
        return currentRound.getGuess();
    }

    public Term getCurrentTermFromRound(Long gameId) {
        Round currentRound = findRoundByGameId(gameId);
        return currentRound.getTerm();
    }

    public Hint updateHint(Hint inputHint, Long gameId) {
        validateGameState(GameStatus.VALIDATION, gameId);
        String reporterToken = inputHint.getReporters().get(0);
        //validate reporterToken, player available

        List<Hint> currentHints = findRoundByGameId(gameId).getHintList();
        Hint hintByToken = findHintByToken(currentHints, inputHint.getToken());

        hintByToken.getReporters().forEach(token -> {
            if (token.equals(reporterToken)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "this player has already reporter this hint");
            }
        });

        //merge the two arrays
        var currentSimilarity = hintByToken.getSimilarity();
        var currentReporters = hintByToken.getReporters();

        currentSimilarity.addAll(inputHint.getSimilarity());
        currentReporters.addAll(inputHint.getReporters());

        hintByToken.setMarked(inputHint.getMarked());
        hintByToken.setSimilarity(currentSimilarity);
        hintByToken.setReporters(currentReporters);

        return hintByToken;
        //TODO wenn alle verifiziert, ganze liste an validator schicken
        //TODO reports & gameState
    }

    public Term deleteCurrentTermOfRound(Long gameId) {
        validateGameState(GameStatus.RECEIVINGHINTS, gameId);

        Round currentRound = findRoundByGameId(gameId);
        Term deletedTerm = currentRound.getTerm();
        if (deletedTerm == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no term set yet");
        }

        //setting the gamestatus back to receiving term
        Game game = gameRepository.findGameByGameId(gameId);
        game.setStatus(GameStatus.RECEIVINGTERM);

        currentRound.setTerm(null);
        return deletedTerm;
    }

    public Guess skipTermToBeGuessed(Long gameId) {
        Round currentRound = findRoundByGameId(gameId);
        //TODO: skip round, call game
        return null;
    }

    //method returns the rounds which belong to a game by the gameId
    //param: Long gameId
    //return: returns all rounds which belong to a game
    public List<Round> getAllRoundsOfGame(Long gameId) {

        List<Round> allRounds = roundRepository.findAll();
        List<Round> rounds = new ArrayList<Round>();

        for (Round r : allRounds) {
            if (r.getGame().getGameId().equals(gameId)) {
                rounds.add(r);
            }
        }

        return rounds;
    }

    //method adds a round to a game
    //param: Game game
    //return: returns the game to which the round has been added
    public Game addRound(Game game) {

        int roundNr = game.getRoundNr();
        if (roundNr > 13) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The game has already finished 13 rounds");
        }

        //adding a new round to the game
        Card card = game.getCardList().get(roundNr);
        //game = addRoundToGame(game, card);

        //adding the new round to the game
        Round newRound = new Round();
        newRound.setCard(card);
        game.addRound(newRound);
        newRound = roundRepository.save(newRound);

        game.setStatus(GameStatus.RECEIVINGTERM);

        //increasing the Round number of the game
        game.setRoundNr(roundNr + 1);
        gameRepository.save(game);

        return game;
    }

    //add a new Round to a Game
    //param: game Game
    //return: returns the newly created round
    private Game addRoundToGame(Game game, Card card) {

        Round newRound = new Round();
        newRound.setCard(card);

        game.addRound(newRound);

        newRound = roundRepository.save(newRound);

        return game;
    }

    private Hint findHintByToken(List<Hint> currentHints, String token) {
        Hint hintByToken = currentHints.stream()
                .filter(hint -> token.equals(hint.getToken()))
                .findAny()
                .orElse(null);
        if (hintByToken == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "hint by token not found");
        }
        else {
            return hintByToken;
        }
    }

    private Round findRoundByGameId(Long gameId) {
        Game game = gameRepository.findGameByGameId(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("game by ID %d not found", gameId));
        }

        int indexOfCurrentRound = game.getRoundNr() - 1;
        return game.getRoundList().get(indexOfCurrentRound);
    }

    private void checkIfTokenValid(String token, PlayerStatus playerStatus) {
        Player player = playerRepository.findByUserToken(token);

        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid token");
        }

        if (player.getStatus() != playerStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This player is not allowed to send this request");
        }
    }

    private void validateGameState(GameStatus checkState, Long gameId) {
        String baseErrorMessage = "invalid game status, current: %s, must be: %s";
        GameStatus currentState = gameRepository.findGameByGameId(gameId).getStatus();
        if (!currentState.equals(checkState)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, currentState, checkState));
        }
    }


}