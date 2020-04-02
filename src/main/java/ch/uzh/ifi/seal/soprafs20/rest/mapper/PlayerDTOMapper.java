package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.player.Player;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerPutDTO;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface PlayerDTOMapper {

    PlayerDTOMapper INSTANCE = Mappers.getMapper(PlayerDTOMapper.class);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "elapsedTime", target = "elapsedTime")
    Player convertPlayerGetDTOtoEntity(PlayerGetDTO playerGetDTO);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "role", target = "role")
    Player convertPlayerPostDTOtoEntity(PlayerPostDTO playerPostDTO);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "elapsedTime", target = "elapsedTime")
    Player convertPlayerPutDTOtoEntity(PlayerPutDTO playerPutDTO);
}
