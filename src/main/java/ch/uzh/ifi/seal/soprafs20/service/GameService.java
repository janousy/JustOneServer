package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final GameRepository gameRepository;
    private final CardRepository cardRepository;

    private final RoundService roundService;
    private final PlayerService playerService;


    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("cardRepository") CardRepository cardRepository, RoundService roundService, PlayerService playerService) {
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
        newGame.setRoundNr(0);
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

        if (gameToBeDeleted.getStatus() != GameStatus.LOBBY && gameToBeDeleted.getStatus() != GameStatus.IDLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The game is not in the correct status to delete");
        }

        List<Player> playerList = gameToBeDeleted.getPlayerList();
        List<Round> roundList = gameToBeDeleted.getRoundList();
        List<Card> cardList = gameToBeDeleted.getCardList();

        //remove all players from the list
        int sizePlayerList = playerList.size();
        for (int i = 0; i < sizePlayerList; i++) {
            gameToBeDeleted.removePlayer(playerList.get(i));
            playerService.removePlayerFromUser(playerList.get(i));
        }

        for (Round r : roundList) {
            gameToBeDeleted.removeRound(r);
        }

        for (Card c : cardList) {
            gameToBeDeleted.removeCard(c);
        }

        gameRepository.delete(gameToBeDeleted);

        return gameToBeDeleted;
    }

    //TODO diese können rausgeworfen werden
    public Card getCurrentCard() {
        return null;
    }

    public Card getNextCard() {
        return null;
    }

    public Card removeCard() {
        return null;
    }

    public void updateCorrectCards() {

    }

    //checks whether a game is ready and returns the game
    //param: Long gameId
    //returns the game which has been checked on its Status
    public Game checkGameReady(Game game) {

        if (game.getPlayerList().isEmpty()) {
            return game;
        }

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

    //TODO hier schauen was besser zurückgegeben werden soll, besser ein game oder besser eine neue Round?
    //TODO diese methode aufrufen wenn ein clue gegeben wurde
    /*
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
        game = roundService.addRoundToGame(game, card);
        game.setStatus(GameStatus.RECEIVINGTERM);

        game.setRoundNr(roundNr + 1);
        gameRepository.save(game);

        return game;
    }


     */
    public void updateScores() {

    }

    //this method finish the preparation of a game to start playing
    //param: Game game
    //return: void
    private void prepareGameToPlay(Game game) {
        //add cards to game and sets it status
        addCardsToGame(game);

        //adding a new round to the game
        game = roundService.addRound(game);

        //setting the new player status
        settingPlayerStatus(game);

        gameRepository.save(game);
    }

    //This methods adds 13 unique cards to a game
    //param: Game game
    //return: void
    private void addCardsToGame(Game game) {

        List<Card> cardList = cardRepository.findAll();

        int totalNrOfCards = cardList.size();
        Long[] pickedNrs = new Long[13];
        Arrays.fill(pickedNrs, -1L);

        //adding 13 unique cards to the game
        for (int i = 0; i < 13; i++) {
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
                playerService.setPlayerStatus(playerList.get(i), PlayerStatus.GUESSER);
            }
            else {
                playerService.setPlayerStatus(playerList.get(i), PlayerStatus.CLUE_GIVER);
            }
        }
    }

    //This is a helper method to check whether the provided name is unique, throws an exception if not
    //param: Game newGame
    private void checkIfGameExists(Game newGame) {
        Game gameByName = gameRepository.findByName(newGame.getName());

        String baseErrorMessage = "The name provided is not %s. Therefore, the game could not be %s!";
        if (gameByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "unique", "created"));
        }

    }
}