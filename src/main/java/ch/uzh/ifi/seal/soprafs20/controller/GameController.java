package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.*;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.GameDTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    //returns a list with all games http method: get, mapping: /games
    @GetMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getAllGames() {
        // fetch all users in the internal representation
        List<Game> games = gameService.getAllGames();
        List<GameGetDTO> gameGetDTOS = new ArrayList<>();

        // convert each user to the API representation
        for (Game g : games) {
            gameGetDTOS.add(GameDTOMapper.INSTANCE.convertEntityToGameGetDTO(g));
        }
        return gameGetDTOS;

    }

    //returns a specific game corresponding to the id http method: get, mapping: /games/{id}
    @GetMapping("/games/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameByID(@PathVariable Long id) {

        //get the proper game
        Game game = gameService.getGameById(id);

        // convert internal representation of user back to API
        return GameDTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    //creation of a game
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createGame(@RequestBody GamePostDTO gamePostDTO) {

        Game newGameInput = GameDTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);

        Game createdGame = gameService.createGame(newGameInput);

        //TODO der status welcher zurückgegeben wird passt nicht mit dem was gesendet werden sollte überein
        return GameDTOMapper.INSTANCE.convertEntityToGameGetDTO(createdGame);
    }


    //returns a specific game corresponding to the id http method: get, mapping: /games/{id}
    @DeleteMapping("/games/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO deleteGameByID(@PathVariable Long id) {

        //get the proper game
        Game game = gameService.deleteGameById(id);

        // convert internal representation of user back to API
        return GameDTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }
}
