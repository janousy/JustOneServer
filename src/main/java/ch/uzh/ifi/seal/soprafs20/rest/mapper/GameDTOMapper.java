package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GameDeleteDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GamePostDTO;

import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GamePutDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameDTOMapper {

    GameDTOMapper INSTANCE = Mappers.getMapper(GameDTOMapper.class);

    @Mapping(source = "name", target = "name")
    Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "correctCards", target = "correctCards")
    GameGetDTO convertEntityToGameGetDTO(Game game);

    @Mapping(source = "gameId", target = "gameId")
    Game convertGameDeleteDTOtoEntity(GameDeleteDTO gameDeleteDTO);

    @Mapping(source = "status", target = "status")
    Game convertGamePutDTOtoEntity(GamePutDTO gamePutDTO);
}
