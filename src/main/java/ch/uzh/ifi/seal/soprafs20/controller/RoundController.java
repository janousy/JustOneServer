package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.ActionType;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.ActionTypeDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.round.RoundGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.RoundDTOMapper;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.action.HintDTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.RoundService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RoundController {

    private final RoundService roundService;
    private final GameService gameService;

    RoundController(RoundService roundService, GameService gameService) {
        this.roundService = roundService;
        this.gameService = gameService;
    }

    //returns a list with all games http method: get, mapping: /games
    @GetMapping("/games/rounds")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoundGetDTO> getAllRounds() {
        // fetch all rounds in the internal representation
        List<Round> rounds = roundService.getAllRounds();
        List<RoundGetDTO> roundGetDTOS = new ArrayList<RoundGetDTO>();

        for (Round round : rounds) {
            roundGetDTOS.add(RoundDTOMapper.INSTANCE.convertEntityToRoundGetDTO(round));
        }
        return roundGetDTOS;
    }

    @PostMapping("/games/{gameId}/hints")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public HintGetDTO createHint(@RequestBody HintPostDTO hintPostDTO, @PathVariable String gameId) {
        Hint inputHint = HintDTOMapper.INSTANCE.convertHintPostDTOToEntity(hintPostDTO);
        return null;

    }

/*
    //returns a list with all games http method: get, mapping: /games
    @PostMapping("/games/{id}/rounds")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoundGetDTO createRound(@PathVariable Long id) {
        // fetch all rounds in the internal representation

        Round round = gameService.addRound(id);

        return RoundDTOMapper.INSTANCE.convertEntityToRoundGetDTO(round);
    }
*/
/*

    //returns a list with all games http method: get, mapping: /games
    @GetMapping("/games/{id}/rounds")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoundGetDTO> getAllRoundsOfGame(@PathVariable Long gameId) {

        List<Round> rounds = roundService.getAllRoundsOfGame(gameId);
        List<RoundGetDTO> roundGetDTOS = new ArrayList<RoundGetDTO>();

        for(Round round : rounds){
            roundGetDTOS.add(RoundDTOMapper.INSTANCE.convertEntityToRoundGetDTO(round));
        }

        return roundGetDTOS;

    }


 */

}