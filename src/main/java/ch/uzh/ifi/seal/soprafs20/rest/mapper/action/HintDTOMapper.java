package ch.uzh.ifi.seal.soprafs20.rest.mapper.action;

import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintPutDTO;
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

    @Mapping(source = "token", target = "token")
    @Mapping(source = "similarity", target = "similarity")
    @Mapping(source = "reporters", target = "reporters")
    @Mapping(source = "marked", target = "marked")
    Hint convertHintPutDTOToEntity(HintPutDTO hintPutDTO);

    @Mapping(source = "roundId", target = "roundId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "invalidCounter", target = "invalidCounter")
    @Mapping(source = "similarity", target = "similarity")
    @Mapping(source = "reporters", target = "reporters")
    HintGetDTO convertEntityToHintGetDTO(Hint hint);
}
