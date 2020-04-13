package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.rest.dto.round.RoundGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.RoundDTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.RoundService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RoundController {

    private final RoundService roundService;

    RoundController(RoundService roundService) {
        this.roundService = roundService;
    }

    //returns a list with all games http method: get, mapping: /games
    @GetMapping("/games/{id}/rounds")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoundGetDTO> getAllGames() {
        // fetch all rounds in the internal representation
        List<Round> rounds = roundService.getAllRounds();
        List<RoundGetDTO> roundGetDTOS = new ArrayList<RoundGetDTO>();

        for (Round round : rounds) {
            roundGetDTOS.add(RoundDTOMapper.INSTANCE.convertEntityToRoundGetDTO(round));
        }

        return roundGetDTOS;
    }

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