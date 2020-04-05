package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.PlayerDTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        return playerGetDTOs;
    }

    //GET players from specific game
    @GetMapping("/games/{gameId}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> getAllPlayersFromGame(@PathVariable int gameId) {
        List<Player> players = playerService.getPlayers();
        List<PlayerGetDTO> playerGetDTOs = new ArrayList<>();

        //TODO improve with repository implementation
        for (Player player : players) {
            if (player.getGame().getGameId() == gameId) {
                playerGetDTOs.add(PlayerDTOMapper.INSTANCE.convertEntityToPlayerGetDTO((Player) player)); //TODO
            }
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
    @PostMapping("/games/{gameId}/players/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlayerGetDTO createPlayer(@RequestBody PlayerPostDTO playerPostDTO,
                                     @PathVariable Long gameId,
                                     @PathVariable Long userId) { //controller returns 400 if header not found!
        Player playerInput = PlayerDTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);
        Player createdPlayer = playerService.createPlayer(playerInput, gameId, userId);

        return PlayerDTOMapper.INSTANCE.convertEntityToPlayerGetDTO(createdPlayer);
    }
}
