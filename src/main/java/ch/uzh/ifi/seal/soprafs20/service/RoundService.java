package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.*;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.*;
import ch.uzh.ifi.seal.soprafs20.helper.GuessValidator;
import ch.uzh.ifi.seal.soprafs20.helper.HintValidator;
import ch.uzh.ifi.seal.soprafs20.helper.ScoringSystem;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.RoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RoundService {
    private final Logger log = LoggerFactory.getLogger(RoundService.class);

    private final RoundRepository roundRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final HintValidator hintValidator;
    private final GuessValidator guessValidator;
    private final ScoringSystem scoringSystem;


    @Autowired
    public RoundService(@Qualifier("roundRepository") RoundRepository roundRepository,
                        @Qualifier("gameRepository") GameRepository gameRepository,
                        @Qualifier("playerRepository") PlayerRepository playerRepository,
                        HintValidator hintValidator,
                        GuessValidator guessValidator,
                        ScoringSystem scoringSystem) {
        this.roundRepository = roundRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.hintValidator = hintValidator;
        this.guessValidator = guessValidator;
        this.scoringSystem = scoringSystem;
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
        List<Round> rounds = new ArrayList<>();

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

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The game id you specified does not exist");
        }
        List<Round> roundList = game.getRoundList();

        //currentRound nr is in external representation(+1)
        int currentRoundExternal = roundList.size();

        //for the special case that the game is finished
        if (game.getStatus() == GameStatus.FINISHED) {
            return roundList.get(currentRoundExternal - 1);
        }


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

    public synchronized Hint addHintToRound(Hint inputHint, Long gameId) {
        log.debug(String.format("adding hint started for: %s", inputHint.getContent()));
        checkIfTokenValid(inputHint.getToken(), PlayerStatus.CLUE_GIVER);
        validateGameState(GameStatus.RECEIVING_HINTS, gameId);

        Round currentRound = findRoundByGameId(gameId);
        var currentHints = currentRound.getHintList();
        currentHints.forEach(hint -> {
            if (hint.getToken().equals(inputHint.getToken())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "player has already set a hint");
            }
        });

        Hint newHint = new Hint();
        newHint.setToken(inputHint.getToken());
        newHint.setContent(inputHint.getContent());
        newHint.setRoundId(currentRound.getId());
        newHint.setStatus(ActionTypeStatus.UNKNOWN);
        newHint.setMarked(ActionTypeStatus.UNKNOWN);

        //validate input content hint with external API
        //validating twice, once empty stemmer for stemming, once with wordnet for lemmatization
        hintValidator.validateWithExernalResources(newHint, currentRound.getTerm(), "");
        hintValidator.validateWithExernalResources(newHint, currentRound.getTerm(), "wordnet");

        //check if hint is similar to any hint given, if yes set to invalid
        hintValidator.checkDuplicates(newHint, currentHints);
        hintValidator.checkSingleWord(newHint);
        hintValidator.checkTermIncluded(newHint, currentRound.getTerm());

        log.debug(String.format("adding hint to round: %s", newHint.getContent()));

        currentHints.add(newHint);
        currentRound.setHintList(currentHints);
        roundRepository.saveAndFlush(currentRound);

        //stopping the time of the player using the actionType
        scoringSystem.stopTimeForPlayer(newHint);

        Game game = gameRepository.findGameByGameId(gameId);
        int nrOfPlayers2 = game.getPlayerList().size();
        int nrOfPlayers = playerRepository.findByGameGameId(gameId).size();
        int nrOfHints = currentRound.getHintList().size();
        log.debug(String.format("nr of hints: %d", currentRound.getHintList().size()));
        log.debug(String.format("nr of players: %d", nrOfPlayers));

        //go into when all hints have arrived
        if (nrOfHints == (nrOfPlayers2 - 1)) {
            log.debug(String.format("setting game status to: %s", GameStatus.VALIDATING_HINTS));
            game.setStatus(GameStatus.VALIDATING_HINTS);
        }
        else {
            log.debug(String.format("setting game status to: %s", GameStatus.RECEIVING_HINTS));
            game.setStatus(GameStatus.RECEIVING_HINTS);
        }
        return newHint;
    }

    //adds a guess to the round, starts new round,
    public Guess addGuessToRound(Guess guess, Long gameId) {
        checkIfTokenValid(guess.getToken(), PlayerStatus.GUESSER);
        validateGameState(GameStatus.RECEIVING_GUESS, gameId);

        Round currentRound = findRoundByGameId(gameId);
        guess.setRoundId(currentRound.getId());
        guess.setStatus(ActionTypeStatus.UNKNOWN);
        currentRound.setGuess(guess);

        //stopping the time of the player using the actionType
        scoringSystem.stopTimeForPlayer(guess);
        //validating the guess
        Guess validatedGuess = guessValidator.guessValidationGuessGiven(guess, gameId, currentRound);

        //updated validity score of guesser
        updateValidityCountOfPlayer(validatedGuess);

        //updating the score of the guesser and the clueGivers
        scoringSystem.updateScoresOfPlayers(gameId);

        //starting a new round
        Game currentGame = gameRepository.findGameByGameId(gameId);
        addRound(currentGame);

        return validatedGuess;
    }

    public Term addTermToRound(Term newTerm, Long gameId) {
        checkIfTokenValid(newTerm.getToken(), PlayerStatus.GUESSER);
        validateGameState(GameStatus.RECEIVING_TERM, gameId);

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

            //as soon as a new term is set, the playertermstatus must be reset
            playerRepository.findByGameGameId(gameId).forEach(player -> player.setPlayerTermStatus(PlayerTermStatus.NOT_SET));

            //starting the time for all clue_givers
            scoringSystem.startTimeForClueGivers(gameId);

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

    public synchronized Hint updateHint(Hint inputHint, Long gameId) {

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

        //merge the two arrays
        var currentSimilarity = hintByToken.getSimilarity();
        var currentReporters = hintByToken.getReporters();

        currentSimilarity.addAll(inputHint.getSimilarity());
        currentReporters.addAll(inputHint.getReporters());

        if (inputHint.getMarked() == ActionTypeStatus.INVALID) {
            int invalidCount = hintByToken.getInvalidCounter();
            hintByToken.setInvalidCounter(invalidCount + 1);
        }

        hintByToken.setMarked(ActionTypeStatus.UNKNOWN); //just to make sure never changed
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
            List<Hint> validatedHints = hintValidator.validateSimilarityAndMarking(currentHints);

            //after final validation of hints, the counters of valid hints and guesses for the respecitve player must be updated
            validatedHints.forEach(this::updateValidityCountOfPlayer);

            Round currentRound = findRoundByGameId(gameId);
            currentRound.setHintList(validatedHints);
            gameById.setStatus(GameStatus.RECEIVING_GUESS);
            roundRepository.saveAndFlush(currentRound);

            //start the time for the guessing player as he now can see the hints
            scoringSystem.startTimeForGuesser(gameId);
        }
        else {
            Round currentRound = findRoundByGameId(gameId);
            currentRound.setHintList(currentHints);
            gameById.setStatus(GameStatus.VALIDATING_HINTS);
            roundRepository.saveAndFlush(currentRound);
        }
        return hintByToken;
    }

    public Game skipTermToBeGuessed(Guess inputGuess, Long gameId) {
        validateGameState(GameStatus.RECEIVING_GUESS, gameId);

        String inputToken = inputGuess.getToken();
        PlayerStatus senderStatus = playerRepository.findByUserToken(inputToken).getStatus();
        if (senderStatus != PlayerStatus.GUESSER) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(
                    "invalid player role, current: %s, must be %s", senderStatus, PlayerStatus.GUESSER));
        }

        //add a new round to the game
        Game game = gameRepository.findGameByGameId(gameId);
        guessValidator.guessValidationGuessSkipped(gameId);

        addRound(game);
        return game;
    }

    //method adds a round to a game
    //param: Game game
    //return: Game game
    public Game addRound(Game game) {

        List<Card> cardList = game.getCardList();
        //if it was the last round we set the gameStatus to finished
        if (cardList.isEmpty()) {
            game.setStatus(GameStatus.FINISHED);
            gameRepository.save(game);
            scoringSystem.updateScoresOfUsers(game);

            return game;
        }

        //adding a new round to the game
        Card card = game.getCardList().get(0);

        //adding the new round to the game
        Round newRound = new Round();
        newRound.setCard(card);
        game.addRound(newRound);
        roundRepository.save(newRound);

        game.setStatus(GameStatus.RECEIVING_TERM);

        //increasing the Round number of the game
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
        if (currentState != checkState) {
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


    private void updateValidityCountOfPlayer(ActionType inputAction) {
        if (inputAction.getStatus().equals(ActionTypeStatus.VALID)) {

            //find the respective player
            Player playerByHintToken = playerRepository.findByUserToken(inputAction.getToken());

            //check whether toke belongs to cluegiver or guesser
            if (playerByHintToken.getStatus().equals(PlayerStatus.CLUE_GIVER)) {
                int currentCount = playerByHintToken.getNrOfValidHints();
                playerByHintToken.setNrOfValidHints(currentCount + 1);
                log.info(String.format("Hint Validity count of player %s increased to: %d", playerByHintToken.getUser().getUsername(), playerByHintToken.getNrOfValidHints()));
            }
            else if (playerByHintToken.getStatus().equals(PlayerStatus.GUESSER)) {
                int currentCount = playerByHintToken.getNrOfValidGuesses();
                playerByHintToken.setNrOfValidGuesses(currentCount + 1);
                log.info(String.format("Guess Validity count of player %s increased to: %d", playerByHintToken.getUser().getUsername(), playerByHintToken.getNrOfValidGuesses()));
            }
            else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("player by token %s does not have an active status", inputAction.getToken()));
            }
        }
    }
}