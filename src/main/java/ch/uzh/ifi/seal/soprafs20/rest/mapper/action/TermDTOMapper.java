package ch.uzh.ifi.seal.soprafs20.rest.mapper.action;

import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.TermGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.TermPostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TermDTOMapper {

    TermDTOMapper INSTANCE = Mappers.getMapper(TermDTOMapper.class);

    @Mapping(source = "content", target = "content")
    @Mapping(source = "roundId", target = "roundId")
    @Mapping(source = "wordId", target = "wordId")
    Term convertTermPostDTOToEntity(TermPostDTO termPostDTO);

    @Mapping(source = "content", target = "content")
    @Mapping(source = "roundId", target = "roundId")
    @Mapping(source = "wordId", target = "wordId")
    TermGetDTO convertEntityToTermGetDTO(TermGetDTO termGetDTO);
}
