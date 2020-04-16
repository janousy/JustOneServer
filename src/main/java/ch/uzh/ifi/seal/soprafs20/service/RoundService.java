package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.constant.CONSTANTS;
import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.*;
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

        Round currentRound = findRoundByGameId(gameId);
        hint.setRoundId(currentRound.getId()); //TODO neccessary to set ID?
        hint.setStatus(ActionTypeStatus.UNKNOWN);
        hint.setMarked(ActionTypeStatus.UNKNOWN);
        currentRound.addHint(hint);
        roundRepository.save(currentRound); //TODO check if save is necessary on entitiy update


        //TODO hier nur eine primitive version von check hints um spielfluss zu gewährleisten, anpassen auf etwas anderes evtl
        int nrOfPlayers = playerRepository.findByGameGameId(gameId).size();
        int nrOfHints = currentRound.getHintList().size();

        //TODO calculation of time noch an einen anderen ort platzieren
        //calculating time of the player who sent the hint
        calculateElapsedTime(hint);


        Game game = gameRepository.findGameByGameId(gameId);
        //go into if when all hints have arrived
        if (nrOfHints == (nrOfPlayers - 1)) {
            game.setStatus(GameStatus.VALIDATION);
            //TODO zeit starten evtl noch an einen anderen ort bringen, je nachdem wo und wie die clues validiert werden
            //starting the time of the guesser after all clue_givers entered their clue

            //TODO dieses behaviour noch an einen anderen ort packen
            //setting the gameStatus to receiving guesses if enough hints have arrived
            gameRepository.save(game);
        }
        return hint;
    }

    //adds a guess to the round, starts new round,
    public Guess addGuessToRound(Guess guess, Long gameId) {
        checkIfTokenValid(guess.getToken(), PlayerStatus.GUESSER);
        validateGameState(GameStatus.RECEIVINGGUESS, gameId);

        Round currentRound = findRoundByGameId(gameId);
        guess.setRoundId(currentRound.getId());
        guess.setStatus(ActionTypeStatus.UNKNOWN);
        currentRound.setGuess(guess);

        Guess validatedGuess = guessValidationService.guessValidation(guess, gameId, currentRound);
        //TODO start der zeit noch richtig setzen
        //calculating the time for the guesser
        calculateElapsedTime(guess);

        //guess = validationService.guessValidation(guess, gameId, currentRound);
        //boolean guessTrue = validationService.guessValidation2(guess, currentRound);


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
            game.setStatus(GameStatus.RECEIVINGHINTS);
            gameRepository.save(game);

            //TODO den start der zeit noch richtig setzen
            //starting the time for all clue_givers
            startingTimeforClue_Giver(gameId);

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


        //TODO check that all hints are validated and reported
        startingTimeforGuesser(gameId);


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
        validateGameState(GameStatus.RECEIVINGGUESS, gameId);

        Round currentRound = findRoundByGameId(gameId);
        Guess guess = new Guess();

        //add a new round to the game
        Game game = gameRepository.findGameByGameId(gameId);
        addRound(game);
        return guess;
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
    //return: Game game
    public Game addRound(Game game) {

        int roundNr = game.getRoundNr();
        //if it was the last round we set the gameStatus to finished
        if (roundNr > 13) {
            game.setStatus(GameStatus.FINISHED);
            gameRepository.save(game);
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

    //method finds the newest round of the game
    //param: Long gameId
    //return: Round round
    private Round findRoundByGameId(Long gameId) {
        Game game = gameRepository.findGameByGameId(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("game by ID %d not found", gameId));
        }

        //adapt the round nr to the representation in the list
        int indexOfCurrentRound = game.getRoundNr() - 1;
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


    //method calculates the elapsed time it took a player to send the guess
    //param: ActionType action
    //return: void
    private void calculateElapsedTime(ActionType action) {

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
    private void startingTimeforGuesser(Long gameId) {
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
    private void startingTimeforClue_Giver(Long gameId) {
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

    private void updateUserScores(Game game) {

    }

    //this method sets the player roles
    //param: Game game
    //return void
    private void settingPlayerStatus(Game game) {

        List<Player> playerList = game.getPlayerList();

        int numberOfPlayers = playerList.size();
        int roundNr = game.getRoundNr();

        int nextGuesser = roundNr % numberOfPlayers;

        for (int i = 0; i < numberOfPlayers; i++) {
            if (i == nextGuesser) {
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