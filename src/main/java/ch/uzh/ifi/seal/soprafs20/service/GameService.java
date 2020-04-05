package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
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
import java.util.UUID;

@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final GameRepository gameRepository;
    private PlayerService playerService;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    //allgmeine methoden nicht auf state aufrufen

    //get all Games as a list
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

        Player player1 = new Player();
        player1.setRole(PlayerRole.HOST);

        addPlayerToGame(player1, newGame.getGameId());

        Player player2 = new Player();
        player2.setRole(PlayerRole.GUEST);

        addPlayerToGame(player2, newGame.getGameId());

        return newGame;
    }

    //delete a specific game with its id
    public Game deleteGame(Game gametobedeleted) {
        return null;
    }

    public List<Player> getAllPlayers(Long gameId) {
        return null;
    }


    //bei all diesen methoden soll auf dem state die methode aufgerufen werden

    //adds a Player to a game by using the gameId
    //param: Player playerToBeAdded, Long GameId
    //returns the Player which has been added to the game
    public Player addPlayerToGame(Player playerToBeAdded, Long GameId) {

        Game game = gameRepository.findGameByGameId(GameId);

        List<Player> oldPlayerList = game.getPlayerList();

        oldPlayerList.add(playerToBeAdded);

        game.setPlayerList(oldPlayerList);

        gameRepository.save(game);

        return playerToBeAdded;
    }

    public Player removePlayerFromGame(Player player) {
        return null;
    }

    public List<Player> updatePlayerList() {
        return null;
    }

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

    public void changePlayerRoles() {

    }

    public void newRound() {

    }

    public void updateScores() {

    }


    //This is a helper method to check whether the provided name is unique, throws an exception if not
    //param: Game newGame
    private void checkIfGameExists(Game newGame) {
        Game gameByName = gameRepository.findByName(newGame.getName());

        String baseErrorMessage = "The name provided is not unique. Therefore, the game could not be created!";
        if (gameByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }

    }


}