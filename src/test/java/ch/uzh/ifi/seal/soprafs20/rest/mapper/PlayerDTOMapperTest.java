package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.player.PlayerPutDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerDTOMapperTest {

    @Test
    public void testCreatePlayer_fromPlayerDTO_toUser_success() {

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setName("testPlayer");
        playerPostDTO.setUserToken("12345");

        Player player = PlayerDTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

        assertEquals(playerPostDTO.getName(), player.getName());
        assertEquals(playerPostDTO.getUserToken(), player.getUserToken());
    }

    @Test
    public void testGetUser_fromPlayer_toPlayerGetDTO_success() {
        Player testPlayer = new Player();
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setName("testPlayer");
        testPlayer.setStatus(PlayerStatus.NOT_READY);
        testPlayer.setScore(0);
        testPlayer.setRole(PlayerRole.HOST);
        testPlayer.setUserToken("12345");
        testPlayer.setElapsedTime(0L);

        PlayerGetDTO playerGetDTO = PlayerDTOMapper.INSTANCE.convertEntityToPlayerGetDTO(testPlayer);

        assertEquals(testPlayer.getId(), playerGetDTO.getId());
        assertEquals(testPlayer.getName(), playerGetDTO.getName());
        assertEquals(testPlayer.getStatus(), playerGetDTO.getStatus());
        assertEquals(testPlayer.getScore(), playerGetDTO.getScore());
        assertEquals(testPlayer.getRole(), playerGetDTO.getRole());
        assertEquals(testPlayer.getUserToken(), playerGetDTO.getUserToken());
        assertEquals(testPlayer.getElapsedTime(), playerGetDTO.getElapsedTime());
    }

    @Test
    public void testUpdatePlayer_fromPlayerPutDTO_toPlayer() {
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setName("testPlayer");

        Player player = PlayerDTOMapper.INSTANCE.convertPlayerPutDTOtoEntity(playerPutDTO);

        assertEquals(playerPutDTO.getName(), player.getName());
    }
}
