package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import ch.uzh.ifi.seal.soprafs20.rest.dto.round.RoundGetDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoundDTOMapperTest {

    @Test
    void testGetRound_fromRound_toRoundGetDTO_success() {
        // create Round
        Round round = new Round();
        round.setId(1L);
        round.setGuess(new Guess());
        round.setTerm(new Term());

        // MAP -> Create UserGetDTO
        RoundGetDTO roundGetDTO = RoundDTOMapper.INSTANCE.convertEntityToRoundGetDTO(round);

        // check content
        assertEquals(round.getId(), roundGetDTO.getId());
        assertEquals(round.getTerm(), roundGetDTO.getTerm());
        assertEquals(round.getGuess(), roundGetDTO.getGuess());
        assertEquals(round.getHintList(), roundGetDTO.getHintList());
    }
}
