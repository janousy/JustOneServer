package ch.uzh.ifi.seal.soprafs20.rest.mapper.action;

import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.GuessGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.GuessPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.action.HintPostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GuessDTOMapper {

    GuessDTOMapper INSTANCE = Mappers.getMapper(GuessDTOMapper.class);

    @Mapping(source = "content", target = "content")
    @Mapping(source = "roundId", target = "roundId")
    Guess convertGuessPostDTOtoEntity(GuessPostDTO guessPostDTO);

    @Mapping(source = "content", target = "content")
    @Mapping(source = "roundId", target = "roundId")
    GuessGetDTO convertEntityToGuessGetDTO(Guess guess);

}
