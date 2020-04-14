package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
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
public class GameService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final GameRepository gameRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    //allgmeine methoden nicht auf state aufrufen

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
        return this.gameRepository.findGameByGameId(gameId);
    }


    //creates a game and saves it in the repository
    //param: takes a Game newGame
    //returns: returns the newly created game
    public Game createGame(Game newGame) {

        checkIfGameExists(newGame);
        newGame.setStatus(GameStatus.LOBBY);

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

        if (gameToBeDeleted == null) {
            String baseErrorMessage = "The gameId provided does not exist. Therefore, the game could not be deleted";
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }

        gameRepository.delete(gameToBeDeleted);

        return gameToBeDeleted;
    }


    //TODO kann gelöscht werden da diese in PlayerService ist
    public List<Player> getAllPlayers(Long gameId) {
        return null;
    }


    //bei all diesen methoden soll auf dem state die methode aufgerufen werden

    //adds a Player to a game by using the gameId
    //param: Player playerToBeAdded, Long GameId
    //returns the Player which has been added to the game
    public Player addPlayerToGame(Player playerToBeAdded, Long gameId) {
/*
        //find the game to which a player should be added
        Game game = gameRepository.findGameByGameId(gameId);

        //get the playerlist and add a new player
        List<Player> oldPlayerList = game.getPlayerList();
        oldPlayerList.add(playerToBeAdded);
        game.setPlayerList(oldPlayerList);

        //save the game in the repository
        gameRepository.save(game);
        gameRepository.flush();


        return playerToBeAdded;

 */
        Game game = gameRepository.findGameByGameId(gameId);

        //throw an error if too many players want to join the game
        if (game.getPlayerList().size() == 7) {
            String baseErrorMessage = "The lobby has already the maximum amount of players(7)";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, baseErrorMessage);
        }

        game.addPlayer(playerToBeAdded);

        //save the game in the repository
        gameRepository.save(game);

        return playerToBeAdded;
    }


    //removes a Player from a game by using the gameId
    //param: Player playerToBeRemoved, Long GameId
    //returns the Player which has been removed from the game
    public Player removePlayerFromGame(Player playerToBeRemoved, Long GameId) {

        //find the game from which a player should be removed and remove it
        Game game = gameRepository.findGameByGameId(GameId);

        //throw an error if too many players want to join the game
        if (game.getPlayerList().size() == 0) {
            String baseErrorMessage = "The lobby is already empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, baseErrorMessage);
        }

        game.removePlayer(playerToBeRemoved);

        //save the game in the repository
        gameRepository.save(game);

        return playerToBeRemoved;
    }

    //TODO can be removed as this is already in the playerservice
    public List<Player> getPlayerList() {
        return null;
    }

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

    //allgemeine methoden nicht auf state aufrufen

    //TODO can be removed as this should be in the playerservice as well
    public void changePlayerRoles() {

    }

    public void newRound() {

    }

    public void updateScores() {

    }


    //This is a helper method to check whether the provided name is unique, throws an exception if not
    //param: Game newGame
    public void checkIfGameExists(Game newGame) {
        Game gameByName = gameRepository.findByName(newGame.getName());

        String baseErrorMessage = "The name provided is not %s. Therefore, the game could not be %s!";
        if (gameByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "unique", "created"));
        }

    }


}