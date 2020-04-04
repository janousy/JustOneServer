package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.player.Player;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository userRepository) {
        this.playerRepository = userRepository;
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

    public Player createPlayer(Player newPlayer) {
        checkIfPlayerExistsByName(newPlayer);

        newPlayer = playerRepository.save(newPlayer);
        log.debug("Created Information for Player: {}", newPlayer);
        return newPlayer;
    }

    //helper method
    private void checkIfPlayerExistsByName(Player playerToBeCreated) {
        Player playerByName = playerRepository.findByName(playerToBeCreated.getName());
        String baseErrorMessage = "The %s provided %s not unique. Therefore, the player could not be created!";

        if (playerByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "playername", "is"));
        }
    }
}
