package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.ActionType;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.*;
import ch.uzh.ifi.seal.soprafs20.rest.dto.round.RoundGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.RoundDTOMapper;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.action.GuessDTOMapper;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.action.HintDTOMapper;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.action.TermDTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
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

    @GetMapping("/games/{gameId}/terms")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TermGetDTO getCurrentTermOfGame(@PathVariable Long gameId) {
        Term currentTerm = roundService.getCurrentTermFromRound(gameId);
        return TermDTOMapper.INSTANCE.convertEntityToTermGetDTO(currentTerm);
    }

    @GetMapping("/games/{gameId}/hints")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<HintGetDTO> getCurrentHintsOfGame(@PathVariable Long gameId) {
        List<Hint> currentHints = roundService.getAllHintsFromRound(gameId);
        List<HintGetDTO> hintGetDTOs = new ArrayList<>();

        for (Hint hint : currentHints) {
            hintGetDTOs.add(HintDTOMapper.INSTANCE.convertEntityToHintGetDTO(hint));
        }
        return hintGetDTOs;
    }

    @GetMapping("/games/{gameId}/guesses")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GuessGetDTO getCurentGuessOfGame(@PathVariable Long gameId) {
        Guess currentGuess = roundService.getGuessOfCurrentRound(gameId);
        return GuessDTOMapper.INSTANCE.convertEntitytoGuessGetDTO(currentGuess);
    }


    @PostMapping("/games/{gameId}/hints")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public HintGetDTO createHint(@RequestBody HintPostDTO hintPostDTO, @PathVariable Long gameId) {
        Hint inputHint = HintDTOMapper.INSTANCE.convertHintPostDTOToEntity(hintPostDTO);
        Hint createdHint = roundService.addHintToRound(inputHint, gameId);
        return HintDTOMapper.INSTANCE.convertEntityToHintGetDTO(createdHint);
    }

    @PostMapping("/games/{gameId}/guesses")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GuessGetDTO createGuess(@RequestBody GuessPostDTO guessPostDTO, @PathVariable Long gameId) {
        Guess inputGuess = GuessDTOMapper.INSTANCE.convertGuessPostDTOtoEntity(guessPostDTO);
        Guess createdGuess = roundService.addGuessToRound(inputGuess, gameId);
        return GuessDTOMapper.INSTANCE.convertEntitytoGuessGetDTO(createdGuess);
    }

    @PostMapping("/games/{gameId}/terms")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TermGetDTO createTerm(@RequestBody TermPostDTO termPostDTO, @PathVariable Long gameId) {
        Term inputTerm = TermDTOMapper.INSTANCE.convertTermPostDTOToEntity(termPostDTO);
        Term createdTerm = roundService.addTermToRound(inputTerm, gameId);
        return TermDTOMapper.INSTANCE.convertEntityToTermGetDTO(createdTerm);
    }

/*
    //returns a list with all games http method: get, mapping: /games

    /*
    //adds a new round to a game
    @PostMapping("/games/{id}/rounds")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoundGetDTO createRound(@PathVariable Long id) {
        // fetch all rounds in the internal representation

        //Round round = gameService.addRound(id);

        return RoundDTOMapper.INSTANCE.convertEntityToRoundGetDTO(round);
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