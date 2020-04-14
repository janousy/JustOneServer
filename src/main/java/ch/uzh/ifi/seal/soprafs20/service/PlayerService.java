package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.SopraServiceException;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
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
    private final GameService gameService;
    private final UserService userService;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                         GameService gameService,
                         UserService userService) {

        this.playerRepository = playerRepository;
        this.gameService = gameService;
        this.userService = userService;
    }

    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    public List<Player> getPlayersFromGame(Long gameId) {
        return playerRepository.findByGameGameId(gameId);
    }

    public Player getPlayerById(Long playerId) {
        Optional<Player> optionalPlayer = playerRepository.findById(playerId);

        if (optionalPlayer.isPresent()) {
            Player playerById = optionalPlayer.get();
            log.debug("Found Player by Id: {}", playerById);
            return playerById;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player ID provided does not exists");
        }
    }

    public Player createPlayer(Player newPlayer, Long gameId) {

        //TODO how to check if gameByID exists? cannot ask gameService

        checkIfPlayerExistsByName(newPlayer);
        User userByToken = userService.getUserByToken(newPlayer.getUserToken());

        newPlayer.setStatus(PlayerStatus.WAITING);
        newPlayer.setScore(0); //score initially zero
        newPlayer.setUser(userByToken);
        newPlayer.setUserToken(userByToken.getToken());
        newPlayer.setElapsedTime(0.00);
        if (!checkIfGameHasHost(gameId)) {
            newPlayer.setRole(PlayerRole.HOST);
        }
        else {
            newPlayer.setRole(PlayerRole.GUEST);
        }

        Player addedPlayer = playerRepository.save(newPlayer);
        log.debug("Created Information for Player: {}", newPlayer);

        addedPlayer = gameService.addPlayerToGame(newPlayer, gameId);

        return addedPlayer;
    }

    public Player deletePlayer(Long gameId, Long playerId) {
        //TODO delete somehow not working
        Optional<Player> playerById = playerRepository.findById(playerId);

        if (playerById.isPresent()) {
            Player toDelete = playerById.get();
            playerRepository.delete(toDelete);
            playerRepository.flush();
            List<Player> playerList2 = playerRepository.findByGameGameId(gameId);

            //a new host must be set if the deleted player was a host
            if (playerById.get().getRole().equals(PlayerRole.HOST)) {
                List<Player> playerList = playerRepository.findByGameGameId(gameId);
                Random rand = new Random();
                Player randomPlayer = playerList.get(rand.nextInt(playerList.size()));
                randomPlayer.setRole(PlayerRole.HOST);
                playerRepository.save(randomPlayer);
                log.info("new host was set");
            }
            playerRepository.flush();
            return playerById.get();
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "player by id not found");
        }
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

}
