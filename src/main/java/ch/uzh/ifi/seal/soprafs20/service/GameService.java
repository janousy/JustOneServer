package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.constant.CONSTANTS;
import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerTermStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final CardRepository cardRepository;

    private final RoundService roundService;
    private final PlayerService playerService;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("cardRepository") CardRepository cardRepository,
                       @Qualifier("playerRepository") PlayerRepository playerRepository,
                       RoundService roundService,
                       PlayerService playerService
    ) {
        this.gameRepository = gameRepository;
        this.cardRepository = cardRepository;
        this.roundService = roundService;
        this.playerService = playerService;
    }

    //get all Games as a list
    //param:
    //return: returns a List<Game> with all games in it
    public List<Game> getAllGames() {
        return this.gameRepository.findAll();
    }

    //method returns the game which is found by its Id
    //param: Long gameId
    //return: returns a Game gameByID
    public Game getGameById(Long gameId) {

        Game gameById = gameRepository.findGameByGameId(gameId);

        if (gameById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The id is not correct or the id does not exist");
        }

        gameById = checkGameReady(gameById);

        if (gameById.getStatus() == GameStatus.VALIDATING_TERM) {
            gameById = checkIfPlayersKnowTerm(gameById);
        }

        //TODO remove and make other method addHint synchronized
        if (gameById.getStatus() == GameStatus.RECEIVING_HINTS) {
            int nrOfPlayers = gameById.getPlayerList().size();
            int lastRoundIndex = gameById.getRoundList().size() - 1;
            int nrOfHints = gameById.getRoundList().get(lastRoundIndex).getHintList().size();
            if (nrOfHints == nrOfPlayers - 1) {
                gameById.setStatus(GameStatus.VALIDATING_HINTS);
            }
        }

        return gameById;
    }

    //creates a game and saves it in the repository
    //param: takes a Game newGame
    //returns: returns the newly created game
    public Game createGame(Game newGame) {

        //first check if the game exists
        checkIfGameExists(newGame);

        //setting status and cards
        newGame.setStatus(GameStatus.LOBBY);
        newGame.setCorrectCards(0);

        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        log.debug("Created Information for Game: {}", newGame);

        return newGame;
    }

    //delete a specific game with its id
    //param: Long gameId
    //return: returns the deleted game Game
    public Game deleteGameById(Long gameId) {
        Game gameToBeDeleted = gameRepository.findGameByGameId(gameId);

        if (gameToBeDeleted.getStatus() != GameStatus.DELETE) {
            return gameToBeDeleted;
        }

        List<Player> playerList = gameToBeDeleted.getPlayerList();
        List<Round> roundList = gameToBeDeleted.getRoundList();
        List<Card> cardList = gameToBeDeleted.getCardList();

        int sizeOfPlayerList = playerList.size();
        if (sizeOfPlayerList != 0) {
            for (int i = 0; i < sizeOfPlayerList; i++) {
                Player player = playerList.get(0);
                playerService.removePlayerFromUser(player);
                gameToBeDeleted.removePlayer(player);

            }
        }

        int sizeOfRoundList = roundList.size();
        for (int i = 0; i < sizeOfRoundList; i++) {
            gameToBeDeleted.removeRound(roundList.get(0));
        }

        int sizeOfCardList = cardList.size();
        for (int i = 0; i < sizeOfCardList; i++) {
            gameToBeDeleted.removeCard(cardList.get(0));
        }

        gameRepository.delete(gameToBeDeleted);

        return gameToBeDeleted;
    }

    //checks whether a game is ready and returns the game
    //param: Long gameId
    //returns the game which has been checked on its Status
    public Game checkGameReady(Game game) {

        //if the playerlist is empty or only one player in it the game cannot start
        if (game.getPlayerList().size() < 3) {
            return game;
        }

        //checking for all players whether they are ready or not
        List<Player> playerList = game.getPlayerList();
        for (Player player : playerList) {
            if (player.getStatus() != PlayerStatus.READY) {
                return game;
            }
        }

        //if all players are ready invoke the final preparation of the game
        prepareGameToPlay(game);

        return game;
    }

    public Game checkIfPlayersKnowTerm(Game game) {
        List<Player> playersInGame = game.getPlayerList();

        //check if all clue givers have reporter whether they know the term
        //if they did not yet, return game
        for (Player player : playersInGame) {
            if (player.getPlayerTermStatus() == PlayerTermStatus.NOT_SET && player.getStatus() == PlayerStatus.CLUE_GIVER) {
                return game;
            }
        }

        //if all players have reporter whether they know the word, check if anybody does not know the term
        //if yes, reset the game state to receiving term to get a new term
        //otherwise set the game state to receiving hints
        int nrOfUnknowns = 0;
        for (Player player : playersInGame) {
            if (player.getPlayerTermStatus() == PlayerTermStatus.UNKNOWN && player.getStatus() == PlayerStatus.CLUE_GIVER) {
                nrOfUnknowns += 1;

            }
        }

        if (nrOfUnknowns > ((playersInGame.size() - 1) / 2)) {
            Round currentRound = findRoundByGameId(game);
            if (currentRound != null) {
                currentRound.setTerm(null);
            }
            game.setStatus(GameStatus.RECEIVING_TERM);
            return game;
        }
        else {
            game.setStatus(GameStatus.RECEIVING_HINTS);
            return game;
        }
    }

    //updates the status of a game
    //param: Long gameId, Game updateForGame
    //return: returns the updated game
    public Game updateGameStatus(Long gameId, Game updateForGame) {

        Game gameToUpdate = getGameById(gameId);

        if (updateForGame.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You did not define a proper GameStatus");
        }

        gameToUpdate.setStatus(updateForGame.getStatus());
        gameRepository.save(gameToUpdate);

        return gameToUpdate;
    }

    //this method finish the preparation of a game to start playing
    //param: Game game
    //return: void
    void prepareGameToPlay(Game game) {
        //add cards to game and sets it status
        addCardsToGame(game);

        //adding a new round to the game
        game = roundService.addRound(game);

        gameRepository.save(game);
    }

    //This methods adds Constants.NumberOfRounds unique cards to a game
    //param: Game game
    //return: void
    void addCardsToGame(Game game) {

        List<Card> cardList = cardRepository.findAll();

        int totalNrOfCards = cardList.size();
        Long[] pickedNrs = new Long[CONSTANTS.NUMBER_OF_ROUNDS];
        Arrays.fill(pickedNrs, -1L);

        //adding 13 unique cards to the game
        for (int i = 0; i < CONSTANTS.NUMBER_OF_ROUNDS; i++) {
            long randomNum = 0;
            boolean unique = false;
            //loop to check uniqueness of card
            while (!unique) {
                randomNum = 1L + (long) (Math.random() * (totalNrOfCards - 1L));
                unique = true;
                for (int j = 0; j < i; j++) {
                    if (pickedNrs[j].equals(randomNum)) {
                        unique = false;
                        break;
                    }
                }
                pickedNrs[i] = randomNum;
            }

            Card cardToBeAdded = cardRepository.findCardById(randomNum);
            game.addCard(cardToBeAdded);
        }

        gameRepository.save(game);
    }


    //This is a helper method to check whether the provided name is unique, throws an exception if not
    //param: Game newGame
    //return: void
    private void checkIfGameExists(Game newGame) {
        Game gameByName = gameRepository.findByName(newGame.getName());

        String baseErrorMessage = "The name provided is not %s. Therefore, the game could not be %s!";
        if (gameByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "unique", "created"));
        }

    }

    Round findRoundByGameId(Game game) {
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game by ID not found");
        }

        //adapt the round nr to the representation in the list
        int indexOfCurrentRound = game.getRoundList().size() - 1;
        if (indexOfCurrentRound < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no round in the game");
        }

        return game.getRoundList().get(indexOfCurrentRound);
    }
}