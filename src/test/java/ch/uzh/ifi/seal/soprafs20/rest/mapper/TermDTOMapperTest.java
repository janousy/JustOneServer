package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.TermGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.TermPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.action.TermDTOMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TermDTOMapperTest {
    @Test
    public void testCreateTerm_fromTermDTO_toTerm_success() {

        TermPostDTO termPostDTO = new TermPostDTO();
        termPostDTO.setWordId(0L);
        termPostDTO.setToken("testToken");

        Term term = TermDTOMapper.INSTANCE.convertTermPostDTOToEntity(termPostDTO);

        assertEquals(termPostDTO.getWordId(), term.getWordId());
        assertEquals(termPostDTO.getToken(), term.getToken());
    }

    @Test
    void testCreateTermGetDTO_fromEntity_toDTO_success() {
        Term term = new Term();
        term.setRoundId(1L);
        term.setContent("testTerm");
        term.setToken("testToken");
        term.setWordId(0L);

        TermGetDTO termGetDTO = TermDTOMapper.INSTANCE.convertEntityToTermGetDTO(term);

        assertEquals(term.getRoundId(), termGetDTO.getRoundId());
        assertEquals(term.getContent(), termGetDTO.getContent());
        assertEquals(term.getToken(), termGetDTO.getToken());
        assertEquals(term.getWordId(), termGetDTO.getWordId());
    }
}
