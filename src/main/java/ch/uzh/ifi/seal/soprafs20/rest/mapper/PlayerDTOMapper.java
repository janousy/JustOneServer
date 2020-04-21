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
    @Mapping(source = "role", target = "role")
    @Mapping(source = "userToken", target = "userToken")
    @Mapping(source = "elapsedTime", target = "elapsedTime")
    @Mapping(source = "playerTermStatus", target = "playerTermStatus")
    PlayerGetDTO convertEntityToPlayerGetDTO(Player player);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "userToken", target = "userToken")
    Player convertPlayerPostDTOtoEntity(PlayerPostDTO playerPostDTO);

    @Mapping(source = "status", target = "status")
    @Mapping(source = "playerTermStatus", target = "playerTermStatus")
    Player convertPlayerPutDTOtoEntity(PlayerPutDTO playerPutDTO);
}
