package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GameDeleteDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GamePostDTO;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameDTOMapper {

    GameDTOMapper INSTANCE = Mappers.getMapper(GameDTOMapper.class);

    @Mapping(source = "name", target = "name")
    Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

    //TODO hier noch roundNr sachen rauswerfen sowie in der DTO
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "correctCards", target = "correctCards")
    //@Mapping(source = "roundNr", target = "roundNr")
    GameGetDTO convertEntityToGameGetDTO(Game game);

    @Mapping(source = "gameId", target = "gameId")
    Game convertGameDeleteDTOtoEntity(GameDeleteDTO gameDeleteDTO);
}
