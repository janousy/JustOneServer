package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.rest.dto.round.RoundGetDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


@Mapper
public interface RoundDTOMapper {

    RoundDTOMapper INSTANCE = Mappers.getMapper(RoundDTOMapper.class);

    @Mapping(source = "id", target = "id")
        //@Mapping(source = "guess", target = "guess")
        //@Mapping(source = "term", target = "term")
        //@Mapping(source = "hintList", target = "hintList")
        //@Mapping(source = "card", target = "card")
    RoundGetDTO convertEntityToRoundGetDTO(Round round);
}
