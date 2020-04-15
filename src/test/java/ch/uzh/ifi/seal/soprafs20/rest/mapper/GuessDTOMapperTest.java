package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.GuessGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.GuessPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.action.GuessDTOMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuessDTOMapperTest {
    @Test
    public void testCreateGuess_fromGuessDTO_toGuess_success() {

        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setContent("testGuess");
        guessPostDTO.setToken("testToken");

        Guess guess = GuessDTOMapper.INSTANCE.convertGuessPostDTOtoEntity(guessPostDTO);

        assertEquals(guessPostDTO.getContent(), guess.getContent());
        assertEquals(guessPostDTO.getToken(), guess.getToken());
    }

    @Test
    void testCreateGuessGetDTO_fromEntity_toDTO_success() {
        Guess guess = new Guess();
        guess.setRoundId(1L);
        guess.setContent("testGuess");
        guess.setToken("testToken");

        GuessGetDTO guessGetDTO = GuessDTOMapper.INSTANCE.convertEntityToGuessGetDTO(guess);

        assertEquals(guess.getRoundId(), guessGetDTO.getRoundId());
        assertEquals(guess.getContent(), guessGetDTO.getContent());
        assertEquals(guess.getToken(), guessGetDTO.getToken());
    }
}
