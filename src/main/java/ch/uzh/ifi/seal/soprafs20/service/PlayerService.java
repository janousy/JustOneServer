package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerTermStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                         @Qualifier("gameRepository") GameRepository gameRepository,
                         @Qualifier("userRepository") UserRepository userRepository) {

        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    public List<Player> getPlayersFromGame(Long gameId) {
        return playerRepository.findByGameGameId(gameId);
    }

    public Player getPlayerById(Long playerId) {
        Player optionalPlayer = playerRepository.findPlayerById(playerId);

        if (optionalPlayer != null) {
            log.debug("Found Player by Id: {}", optionalPlayer);
            return optionalPlayer;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player ID provided does not exists");
        }
    }

    public Player createPlayer(Player newPlayer, Long gameId) {

        //TODO how to check if gameByID exists? cannot ask gameService

        checkIfPlayerExistsByName(newPlayer);
        User userByToken = userRepository.findByToken(newPlayer.getUserToken());

        newPlayer.setStatus(PlayerStatus.NOT_READY);
        newPlayer.setScore(0); //score initially zero
        newPlayer.setUser(userByToken);
        newPlayer.setUserToken(userByToken.getToken());
        newPlayer.setElapsedTime(0L);
        newPlayer.setPlayerTermStatus(PlayerTermStatus.NOT_SET);
        if (!checkIfGameHasHost(gameId)) {
            newPlayer.setRole(PlayerRole.HOST);
        }
        else {
            newPlayer.setRole(PlayerRole.GUEST);
        }

        Player addedPlayer = playerRepository.save(newPlayer);
        log.debug("Created Information for Player: {}", newPlayer);

        addedPlayer = addPlayerToGame(newPlayer, gameId);

        return addedPlayer;
    }

    public Player deletePlayer(Long gameId, Long playerId) {
        Game game1 = gameRepository.findGameByGameId(gameId);

        Player playerById = playerRepository.findPlayerById(playerId);

        if (playerById != null) {

            //delete the player from the game
            removePlayerFromGame(playerById, gameId);

            //delete the player from the user
            removePlayerFromUser(playerById);

            playerRepository.flush();

            //a new host must be set if the deleted player was a host
            if (playerById.getRole().equals(PlayerRole.HOST)) {
                Game game = gameRepository.findGameByGameId(gameId);
                List<Player> playerList = game.getPlayerList();
                //List<Player> playerList = playerRepository.findByGameGameId(gameId);
                Random rand = new Random();
                Player randomPlayer = playerList.get(rand.nextInt(playerList.size()));
                randomPlayer.setRole(PlayerRole.HOST);
                playerRepository.save(randomPlayer);
                log.info("new host was set");
            }
            playerRepository.flush();
            return playerById;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "player by id not found");
        }
    }

    /* Update player status */
    public Player updatePlayer(Player playerInput, Long playerId, Long gameId) {
        Optional<Player> playerById = playerRepository.findById(playerId);

        if (playerById.isPresent()) {
            Player updatedPlayer = playerById.get();
            if (playerInput.getStatus() != null) {
                if (!checkIfPlayerStatusExists(playerInput.getStatus())) {
                    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "invalid player status");
                }
                if (updatedPlayer.getStatus() != PlayerStatus.READY &&
                        updatedPlayer.getStatus() != PlayerStatus.NOT_READY &&
                        updatedPlayer.getPlayerTermStatus() != PlayerTermStatus.NOT_SET) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status cannot be changed anymore, player is in action");
                }
                updatedPlayer.setStatus(playerInput.getStatus());
            }

            if ((playerInput.getPlayerTermStatus() != null)) {
                if (updatedPlayer.getStatus() == PlayerStatus.CLUE_GIVER) {
                    updatedPlayer.setPlayerTermStatus(playerInput.getPlayerTermStatus());
                }
                else {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("invalid player status, current: %s, must be %s", updatedPlayer.getStatus(), PlayerStatus.CLUE_GIVER));
                }
            }
            playerRepository.save(updatedPlayer);
            return updatedPlayer;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "player by id not found");
        }
    }

    //method sets the playerStatus and saves the player
    //param: Player player, PlayerStatus, playerStatus
    //return: void
    public void setPlayerStatus(Player player, PlayerStatus playerStatus) {
        player.setStatus(playerStatus);
        playerRepository.save(player);
    }


    //helper methods
    private void checkIfPlayerExistsByName(Player playerToBeCreated) {
        Player playerByName = playerRepository.findByName(playerToBeCreated.getName());
        String baseErrorMessage = "The %s provided %s not unique. Therefore, the player could not be created!";

        if (playerByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "playername", "is"));
        }
    }

    private boolean checkIfGameHasHost(Long gameId) {
        boolean hasHost = false;
        List<Player> playersInGameById = playerRepository.findByGameGameId(gameId);
        for (Player player : playersInGameById) {
            if (player.getRole().equals(PlayerRole.HOST)) {
                hasHost = true;
                break;
            }
        }
        return hasHost;
    }

    private boolean checkIfPlayerStatusExists(PlayerStatus status) {
        for (PlayerStatus playerStatus : PlayerStatus.values()) {
            if (playerStatus.equals(status)) {
                return true;
            }
        }
        return false;
    }

    //takes a player and sets the player field of the corresponding user to null
    //param: Player playerToBeRemoved
    //return: void
    public void removePlayerFromUser(Player playerToBeRemoved) {
        User userToDeletePlayerFrom = userRepository.findByToken(playerToBeRemoved.getUserToken());
        userToDeletePlayerFrom.setPlayer(null);
    }


    //adds a Player to a game by using the gameId
    //param: Player playerToBeAdded, Long GameId
    //returns the Player which has been added to the game
    private Player addPlayerToGame(Player playerToBeAdded, Long gameId) {
        Game game = gameRepository.findGameByGameId(gameId);

        //throw an error if too many players want to join the game
        if (game.getPlayerList().size() == 7) {
            String baseErrorMessage = "The lobby has already the maximum amount of players(7)";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, baseErrorMessage);
        }

        if (game.getStatus() != GameStatus.LOBBY) {
            String baseErrorMessage = "The Game has already started, thus you cannot join anymore";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, baseErrorMessage);
        }

        game.addPlayer(playerToBeAdded);

        gameRepository.save(game);
        log.debug("Added a Player to a game");

        return playerToBeAdded;
    }


    //removes a Player from a game by using the gameId
    //param: Player playerToBeRemoved, Long GameId
    //returns the Player which has been removed from the game
    private void removePlayerFromGame(Player playerToBeRemoved, Long GameId) {

        //find the game from which a player should be removed and remove it
        Game game = gameRepository.findGameByGameId(GameId);

        //throw an error if too many players want to join the game
        if (game.getPlayerList().size() == 0) {
            String baseErrorMessage = "The lobby is already empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, baseErrorMessage);
        }

        if (game.getStatus() != GameStatus.LOBBY) {
            String baseErrorMessage = "The Game has already started, thus you cannot remove a player anymore";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, baseErrorMessage);
        }

        game.removePlayer(playerToBeRemoved);

        //save the game in the repository
        gameRepository.save(game);
    }


}
