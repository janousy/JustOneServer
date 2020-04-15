package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.GuessGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.action.GuessDTOMapper;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.action.HintDTOMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HintDTOMapperTest {

    @Test
    public void testCreateHint_fromHintDTO_toHint_success() {
        HintPostDTO hintPostDTO = new HintPostDTO();
        hintPostDTO.setContent("testHint");
        hintPostDTO.setToken("testToken");

        Hint hint = HintDTOMapper.INSTANCE.convertHintPostDTOToEntity(hintPostDTO);

        assertEquals(hintPostDTO.getContent(), hint.getContent());
        assertEquals(hintPostDTO.getToken(), hint.getToken());
    }

    @Test
    void testCreateGuessGetDTO_fromEntity_toDTO_success() {
        Hint hint = new Hint();
        hint.setRoundId(1L);
        hint.setContent("testHint");
        hint.setToken("testToken");

        HintGetDTO hintGetDTO = HintDTOMapper.INSTANCE.convertEntityToHintGetDTO(hint);

        assertEquals(hint.getRoundId(), hintGetDTO.getRoundId());
        assertEquals(hint.getContent(), hintGetDTO.getContent());
        assertEquals(hint.getToken(), hintGetDTO.getToken());
    }
}
