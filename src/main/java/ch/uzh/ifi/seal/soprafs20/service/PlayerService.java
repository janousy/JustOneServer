package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    //TODO get players from specific game with repository method

    public Player getPlayerById(Long playerId) {
        Optional<Player> optionalPlayer = playerRepository.findById(playerId);

        if (optionalPlayer.isPresent()) {
            Player playerById = optionalPlayer.get();
            log.debug("Found Player by Id: {}", playerById);
            return playerById;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID provided does not exists");
        }
    }

    public Player createPlayer(Player newPlayer, Long gameId, Long userId) {
        checkIfPlayerExistsByName(newPlayer);

        newPlayer.setStatus(PlayerStatus.WAITING);
        newPlayer.setScore(0); //score initially zero

        User userById = userService.getUserById(userId);

        newPlayer.setUser(userById);

        //TODO: how to link the player belonging to a game
        gameService.addPlayerToGame(newPlayer, gameId);

        /* set role to HOST if player list is empty, otherwise set role to GUEST*/
        newPlayer.setRole(PlayerRole.GUEST);

        //newPlayer.setUser();
        newPlayer.setToken(UUID.randomUUID().toString());
        newPlayer.setElapsedTime(0.00);


        Player addedPlayer = playerRepository.save(newPlayer);
        log.debug("Created Information for Player: {}", newPlayer);
        return addedPlayer;
    }

    public Player deletePlayer(Player player) {
        return null;
    }


    //helper methods
    private void checkIfPlayerExistsByName(Player playerToBeCreated) {
        Player playerByName = playerRepository.findByName(playerToBeCreated.getName());
        String baseErrorMessage = "The %s provided %s not unique. Therefore, the player could not be created!";

        if (playerByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "playername", "is"));
        }
    }
}
