package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerPutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlayerDTOMapper {

    PlayerDTOMapper INSTANCE = Mappers.getMapper(PlayerDTOMapper.class);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "game", target = "game")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "elapsedTime", target = "elapsedTime")
    PlayerGetDTO convertEntityToPlayerGetDTO(Player player); //TODO multiple mappers needed if bots added

    @Mapping(source = "name", target = "name")
    Player convertPlayerPostDTOtoEntity(PlayerPostDTO playerPostDTO);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "elapsedTime", target = "elapsedTime")
    Player convertPlayerPutDTOtoEntity(PlayerPutDTO playerPutDTO);

}
