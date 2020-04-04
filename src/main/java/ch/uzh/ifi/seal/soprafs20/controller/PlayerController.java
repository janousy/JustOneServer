package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.player.Player;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.player.PlayerDTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
            if (player.getGameId() == gameId) {
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
    public PlayerGetDTO createPlayer(@RequestBody PlayerPostDTO playerPostDTO, @PathVariable String gameId, @PathVariable String userId) {
        Player playerInput = PlayerDTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);
        Player createdPlayer = playerService.createPlayer(playerInput);

        return PlayerDTOMapper.INSTANCE.convertEntityToPlayerGetDTO(createdPlayer);
    }
}
