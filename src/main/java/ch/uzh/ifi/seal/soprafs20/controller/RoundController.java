package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.*;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.round.RoundGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.GameDTOMapper;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.RoundDTOMapper;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.action.GuessDTOMapper;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.action.HintDTOMapper;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.action.TermDTOMapper;
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

    //returns a list with all rounds http method
    @GetMapping("/games/rounds")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoundGetDTO> getAllRounds() {
        // fetch all rounds in the internal representation
        List<Round> rounds = roundService.getAllRounds();
        List<RoundGetDTO> roundGetDTOS = new ArrayList<>();

        for (Round round : rounds) {
            roundGetDTOS.add(RoundDTOMapper.INSTANCE.convertEntityToRoundGetDTO(round));
        }

        return roundGetDTOS;
    }

    //returns a list with all rounds of a specific game
    //if lastRound=true the method returns the last, finished played round
    @GetMapping("/games/{gameId}/rounds")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoundGetDTO> getAllRoundsOfGame(@PathVariable Long gameId,
                                                @RequestParam(required = false, name = "lastRound", defaultValue = "false") String lastRound) {
        List<RoundGetDTO> roundGetDTOS = new ArrayList<>();

        //returns the last finished round
        if (lastRound.equals("true")) {
            Round round = roundService.getLastRoundOfGame(gameId);
            roundGetDTOS.add(RoundDTOMapper.INSTANCE.convertEntityToRoundGetDTO(round));
        }

        else {
            List<Round> rounds = roundService.getAllRoundsOfGame(gameId);

            for (Round round : rounds) {
                roundGetDTOS.add(RoundDTOMapper.INSTANCE.convertEntityToRoundGetDTO(round));
            }
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

    @GetMapping("/games/{gameId}/guesses")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GuessGetDTO getCurrentGuessOfGame(@PathVariable Long gameId) {
        Guess currentGuess = roundService.getGuessOfCurrentRound(gameId);
        return GuessDTOMapper.INSTANCE.convertEntityToGuessGetDTO(currentGuess);
    }

    //gibt alle hints zurück welche nicht invalide sind
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
        return GuessDTOMapper.INSTANCE.convertEntityToGuessGetDTO(createdGuess);
    }

    @PostMapping("/games/{gameId}/terms")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TermGetDTO createTerm(@RequestBody TermPostDTO termPostDTO, @PathVariable Long gameId) {
        Term inputTerm = TermDTOMapper.INSTANCE.convertTermPostDTOToEntity(termPostDTO);
        Term createdTerm = roundService.addTermToRound(inputTerm, gameId);
        return TermDTOMapper.INSTANCE.convertEntityToTermGetDTO(createdTerm);
    }

    //nimmt jeden hint einzeln zur validierung an
    // reports: token des players
    // similarity: markierte ähnlichkeiten zu anderen hints
    @PutMapping("/games/{gameId}/hints")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public HintGetDTO reportHints(@RequestBody HintPutDTO hintPutDTO, @PathVariable Long gameId) {
        Hint inputHint = HintDTOMapper.INSTANCE.convertHintPutDTOToEntity(hintPutDTO);
        Hint updatedHint = roundService.updateHint(inputHint, gameId);
        return HintDTOMapper.INSTANCE.convertEntityToHintGetDTO(updatedHint);
    }

    @DeleteMapping("/games/{gameId}/guesses")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO deleteGuess(@RequestBody GuessPostDTO guessPostDTO, @PathVariable Long gameId) {
        //guesser skips his guess
        Guess inputGuess = GuessDTOMapper.INSTANCE.convertGuessPostDTOtoEntity(guessPostDTO);
        Game currentGame = roundService.skipTermToBeGuessed(inputGuess, gameId);
        return GameDTOMapper.INSTANCE.convertEntityToGameGetDTO(currentGame);
    }
}