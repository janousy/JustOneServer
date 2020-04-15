package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.PlayerDTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PlayerController {


    private final PlayerService playerService;

    PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    //GET all players from every game
    @GetMapping("/games/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> getAllPlayers() {
        List<Player> players = playerService.getPlayers();
        List<PlayerGetDTO> playerGetDTOs = new ArrayList<>();

        for (Player player : players) {
            playerGetDTOs.add(PlayerDTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player));
        }
        if (!playerGetDTOs.isEmpty()) {
            return playerGetDTOs;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no players found");
        }
    }

    //GET players from specific game
    @GetMapping("/games/{gameId}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> getAllPlayersFromGame(@PathVariable long gameId) {

        List<PlayerGetDTO> playerGetDTOs = new ArrayList<>();
        List<Player> playersByGameId = playerService.getPlayersFromGame(gameId);

        for (Player player : playersByGameId) {
            playerGetDTOs.add(PlayerDTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player));
        }
        return playerGetDTOs;
    }

    //GET specific player by Id
    @GetMapping("/games/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO getPlayerFromGame(@PathVariable Long playerId) {
        Player playerById = playerService.getPlayerById(playerId);
        return PlayerDTOMapper.INSTANCE.convertEntityToPlayerGetDTO(playerById);
    }

    //GET players sorted by score descending for scoreboard

    //POST create/join a player to a specific game
    @PostMapping("/games/{gameId}/players")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlayerGetDTO createPlayer(@RequestBody PlayerPostDTO playerPostDTO,
                                     @PathVariable Long gameId) {
        Player playerInput = PlayerDTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);
        Player createdPlayer = playerService.createPlayer(playerInput, gameId);

        return PlayerDTOMapper.INSTANCE.convertEntityToPlayerGetDTO(createdPlayer);
    }

    //TODO: put to update a player
    @PutMapping("/games/{gameId}/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO updatePlayer(@RequestBody PlayerPutDTO playerPutDTO,
                                     @PathVariable Long gameId,
                                     @PathVariable Long playerId) {
        Player playerInput = PlayerDTOMapper.INSTANCE.convertPlayerPutDTOtoEntity(playerPutDTO);
        Player updatedPlayer = playerService.updatePlayer(playerInput, playerId, gameId);
        return PlayerDTOMapper.INSTANCE.convertEntityToPlayerGetDTO(updatedPlayer);
    }

    //DELETE delete player TODO: who becomes host
    @DeleteMapping("/games/{gameId}/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO deletePlayer(@PathVariable Long gameId,
                                     @PathVariable Long playerId) {

        Player deletedPlayer = playerService.deletePlayer(gameId, playerId);

        return PlayerDTOMapper.INSTANCE.convertEntityToPlayerGetDTO(deletedPlayer);
    }
}
