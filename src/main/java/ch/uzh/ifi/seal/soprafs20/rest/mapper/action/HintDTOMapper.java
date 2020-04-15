package ch.uzh.ifi.seal.soprafs20.rest.mapper.action;

import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintPostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HintDTOMapper {

    HintDTOMapper INSTANCE = Mappers.getMapper(HintDTOMapper.class);

    @Mapping(source = "roundId", target = "roundId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "token", target = "token")
    Hint convertHintPostDTOToEntity(HintPostDTO hintPostDTO);

    @Mapping(source = "roundId", target = "roundId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "token", target = "token")
    HintGetDTO convertEntityToHintGetDTO(Hint hint);

}
