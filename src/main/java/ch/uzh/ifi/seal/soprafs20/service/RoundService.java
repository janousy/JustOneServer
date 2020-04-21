package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.*;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.*;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.RoundRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintPostDTO;
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
    private final ScoringService scoringService;


    @Autowired
    public RoundService(@Qualifier("roundRepository") RoundRepository roundRepository,
                        @Qualifier("gameRepository") GameRepository gameRepository,
                        @Qualifier("playerRepository") PlayerRepository playerRepository,
                        HintValidationService hintValidationService,
                        GuessValidationService guessValidationService,
                        ScoringService scoringService) {
        this.roundRepository = roundRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.hintValidationService = hintValidationService;
        this.guessValidationService = guessValidationService;
        this.scoringService = scoringService;
    }

    //get all rounds as a list
    //param:
    //return: returns a List<Round> with all rounds in it
    public List<Round> getAllRounds() {
        return this.roundRepository.findAll();
    }

    //method returns all rounds which belong to a game by the gameId
    //param: Long gameId
    //return: List<Round> rounds
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

    //method returns the last round which was played, if no last round exists it throws an error
    //param: Long gameId
    //return: Round round
    public Round getLastRoundOfGame(Long gameId) {
        Game game = gameRepository.findGameByGameId(gameId);
        List<Round> roundList = game.getRoundList();
        //currentRound nr is in external representation(+1)
        int currentRoundExternal = roundList.size();

        //internalRepresentation of last round is -1 for last round and -1 because of the offset of array mapping
        int lastRoundInternal = currentRoundExternal - 2;

        if (lastRoundInternal < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no last round");
        }
        //if the game has finished we always want to access the last element of the list
        if (game.getStatus() == GameStatus.FINISHED) {
            lastRoundInternal = lastRoundInternal + 1;
        }

        return roundList.get(lastRoundInternal);
    }


    public Hint addHintToRound(Hint inputHint, Long gameId) {
        checkIfTokenValid(inputHint.getToken(), PlayerStatus.CLUE_GIVER);
        validateGameState(GameStatus.RECEIVINGHINTS, gameId);

        Round currentRound = findRoundByGameId(gameId);
        var currentHints = currentRound.getHintList();
        currentHints.forEach(hint -> {
            if (hint.getToken().equals(inputHint.getToken())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "player has already set a hint");
            }
        });

        inputHint.setRoundId(currentRound.getId());
        inputHint.setStatus(ActionTypeStatus.UNKNOWN);
        inputHint.setMarked(ActionTypeStatus.UNKNOWN);

        Hint validatedHint = hintValidationService.validateWithExernalResources(inputHint, currentRound);
        currentRound.addHint(validatedHint);
        roundRepository.save(currentRound);


        //stopping the time of the player using the actionType
        scoringService.stopTimeForPlayer(inputHint);

        Game game = gameRepository.findGameByGameId(gameId);
        int nrOfPlayers = playerRepository.findByGameGameId(gameId).size();
        int nrOfHints = currentRound.getHintList().size();

        //go into if when all hints have arrived
        if (nrOfHints == (nrOfPlayers - 1)) {
            game.setStatus(GameStatus.VALIDATING_HINTS);
            gameRepository.save(game);
        }
        return inputHint;
    }

    //adds a guess to the round, starts new round,
    public Guess addGuessToRound(Guess guess, Long gameId) {
        checkIfTokenValid(guess.getToken(), PlayerStatus.GUESSER);
        validateGameState(GameStatus.RECEIVINGGUESS, gameId);

        Round currentRound = findRoundByGameId(gameId);
        guess.setRoundId(currentRound.getId());
        guess.setStatus(ActionTypeStatus.UNKNOWN);
        currentRound.setGuess(guess);

        //stopping the time of the player using the actionType
        scoringService.stopTimeForPlayer(guess);
        //validating the guess
        Guess validatedGuess = guessValidationService.guessValidation(guess, gameId, currentRound);
        //updating the score of the guesser
        scoringService.updateScoreOfGuesser(validatedGuess, gameId);

        //starting a new round
        Game currentGame = gameRepository.findGameByGameId(gameId);
        addRound(currentGame);

        return validatedGuess;
    }

    public Term addTermToRound(Term newTerm, Long gameId) {
        checkIfTokenValid(newTerm.getToken(), PlayerStatus.GUESSER);
        validateGameState(GameStatus.RECEIVINGTERM, gameId);

        Round currentRound = findRoundByGameId(gameId);
        String[] wordsOfCards = currentRound.getCard().getTerms();
        int relWordId = Math.toIntExact(newTerm.getWordId());

        if (relWordId >= 0 && relWordId < CONSTANTS.MAX_WORDS_PER_CARD) {
            newTerm.setContent(wordsOfCards[Math.toIntExact(relWordId)]);
            newTerm.setWordId((long) relWordId);
            newTerm.setRoundId(currentRound.getId());

            //setting the gamestatus to receiving hints
            Game game = gameRepository.findGameByGameId(gameId);
            game.setStatus(GameStatus.VALIDATING_TERM);
            gameRepository.save(game);

            //starting the time for all clue_givers
            scoringService.startTimeForClue_Givers(gameId);

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

        if (inputHint.getReporters().size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "invalid reporters, should only be one");
        }
        String reporterToken = inputHint.getReporters().get(0);
        int nrOfClueGivers = playerRepository.findByGameGameId(gameId).size() - 1;
        Game gameById = gameRepository.findGameByGameId(gameId);
        List<Hint> currentHints = findRoundByGameId(gameId).getHintList();

        Hint hintByToken = findHintByToken(currentHints, inputHint.getToken());

        validateGameState(GameStatus.VALIDATING_HINTS, gameId);
        checkIfTokenValid(reporterToken, PlayerStatus.CLUE_GIVER);


        //catch if report contains its own hint
        int hintPosition = currentHints.indexOf(hintByToken);
        if (inputHint.getSimilarity().contains(hintPosition)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "similarities cannot contain hint itself");
        }

        //catch if report of player to this hint already exists
        hintByToken.getReporters().forEach(token -> {
            if (token.equals(reporterToken)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "this player has already reporter this hint");
            }
        });

        //TODO: move updateHint to hinvalidationservice
        //merge the two arrays
        var currentSimilarity = hintByToken.getSimilarity();
        var currentReporters = hintByToken.getReporters();

        currentSimilarity.addAll(inputHint.getSimilarity());
        currentReporters.addAll(inputHint.getReporters());

        hintByToken.setMarked(inputHint.getMarked());
        hintByToken.setSimilarity(currentSimilarity);
        hintByToken.setReporters(currentReporters);



        //check if all hints validated
        boolean allHintsReported = true;
        for (Hint hint : currentHints) {
            if (hint.getReporters().size() != nrOfClueGivers) {
                allHintsReported = false;
                break;
            }
        }

        if (allHintsReported) {
            List<Hint> validatedHints = hintValidationService.validateSimilarityAndMarking(currentHints);
            findRoundByGameId(gameId).setHintList(validatedHints);
            gameById.setStatus(GameStatus.RECEIVINGGUESS);
            scoringService.startTimeForGuesser(gameId);
        }
        else {
            gameById.setStatus(GameStatus.VALIDATING_HINTS);
        }
        return hintByToken;

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
        currentRound.setTerm(null);
        game.setStatus(GameStatus.RECEIVINGTERM);

        return deletedTerm;
    }

    public Guess skipTermToBeGuessed(Long gameId) {
        validateGameState(GameStatus.RECEIVINGGUESS, gameId);

        Round currentRound = findRoundByGameId(gameId);
        Guess guess = new Guess();

        //add a new round to the game
        Game game = gameRepository.findGameByGameId(gameId);
        addRound(game);
        return guess;
    }

    //method adds a round to a game
    //param: Game game
    //return: Game game
    public Game addRound(Game game) {

        int roundNr = game.getRoundNr();
        //if it was the last round we set the gameStatus to finished
        if (roundNr == 13) {
            game.setStatus(GameStatus.FINISHED);
            gameRepository.save(game);
            scoringService.updateScoresOfUsers(game);

            return null;
        }

        //adding a new round to the game
        Card card = game.getCardList().get(roundNr);

        //adding the new round to the game
        Round newRound = new Round();
        newRound.setCard(card);
        game.addRound(newRound);
        newRound = roundRepository.save(newRound);

        game.setStatus(GameStatus.RECEIVINGTERM);

        //increasing the Round number of the game
        game.setRoundNr(roundNr + 1);
        gameRepository.save(game);

        //setting the playerStatus correctly
        settingPlayerStatus(game);

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

    //method finds the current round of the game
    //param: Long gameId
    //return: Round round
    private Round findRoundByGameId(Long gameId) {
        Game game = gameRepository.findGameByGameId(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("game by ID %d not found", gameId));
        }

        //adapt the round nr to the representation in the list
        int indexOfCurrentRound = game.getRoundList().size() - 1;
        if (indexOfCurrentRound < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no round in the game");
        }

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

    //this method sets the player roles
    //param: Game game
    //return void
    private void settingPlayerStatus(Game game) {

        List<Player> playerList = game.getPlayerList();
        int numberOfPlayers = playerList.size();

        int indexOfLastGuesser = -1;
        int indexOfHost = -1;

        //for loop  to get the indexOfLastGuesser and the indexOfHost
        for (int i = 0; i < numberOfPlayers; i++) {
            Player player = playerList.get(i);
            if (player.getStatus() == PlayerStatus.GUESSER) {
                indexOfLastGuesser = i;
            }
            if (player.getRole() == PlayerRole.HOST) {
                indexOfHost = i;
            }
        }

        //in case that the game starts(no last guesser, the one after the host gets guesser)
        //host is always 0
        if (indexOfLastGuesser < 0) {
            indexOfLastGuesser = indexOfHost;
        }

        int indexOfNextGuesser = (indexOfLastGuesser + 1) % numberOfPlayers;

        //for loop to set the new statuses
        for (int i = 0; i < numberOfPlayers; i++) {
            if (i == indexOfNextGuesser) {
                Player player = playerList.get(i);
                player.setStatus(PlayerStatus.GUESSER);
                playerRepository.save(player);
            }
            else {
                Player player = playerList.get(i);
                player.setStatus(PlayerStatus.CLUE_GIVER);
                playerRepository.save(player);
            }
        }
    }

}