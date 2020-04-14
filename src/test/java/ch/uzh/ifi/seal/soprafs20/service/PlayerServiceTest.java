package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;


    @InjectMocks
    private PlayerService playerService;


    private Player testPlayer;


    public void setup() {
        MockitoAnnotations.initMocks(this);

        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setName("testPlayer");
        //testPlayer.setStatus(PlayerStatus.WAITING);
        //testPlayer.setScore(0);
        //testPlayer.setRole(PlayerRole.HOST);
        //testPlayer.setUserToken("12345");
        //testPlayer.setElapsedTime(0.00);

        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setToken("12345");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setPassword("password");
        testUser.setCreationDate((new Date()).toString());

        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

    }


    public void createPlayer_validInputs_success() {
        Player createdPlayer = playerService.createPlayer(testPlayer, 1L);

        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getName(), createdPlayer.getName());
        assertEquals(testPlayer.getStatus(), createdPlayer.getStatus());
        assertEquals(testPlayer.getScore(), createdPlayer.getScore());
        assertEquals(testPlayer.getRole(), createdPlayer.getRole());
        assertEquals(testPlayer.getUserToken(), createdPlayer.getUserToken());
        assertEquals(testPlayer.getElapsedTime(), createdPlayer.getElapsedTime());
    }
}
