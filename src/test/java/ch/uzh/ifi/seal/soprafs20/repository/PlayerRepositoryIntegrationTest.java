package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerTermStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import javax.persistence.AttributeOverride;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void findByName_success() {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setName("Test Player");
        player.setStatus(PlayerStatus.NOT_READY);
        player.setScore(0);
        player.setRole(PlayerRole.HOST);
        player.setUserToken("12345abc");
        player.setElapsedTime(0L);
        player.setPlayerTermStatus(PlayerTermStatus.NOT_SET);

        entityManager.merge(player);
        entityManager.flush();

        // when
        Player found = playerRepository.findByName(player.getName());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getName(), player.getName());
        assertEquals(found.getStatus(), player.getStatus());
        assertEquals(found.getScore(), player.getScore());
        assertEquals(found.getRole(), player.getRole());
        assertEquals(found.getUserToken(), player.getUserToken());
        assertEquals(found.getElapsedTime(), player.getElapsedTime());
        assertEquals(found.getPlayerTermStatus(), player.getPlayerTermStatus());

    }

    @Test
    public void findByUserToken_success() {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setName("Test Player");
        player.setStatus(PlayerStatus.NOT_READY);
        player.setScore(0);
        player.setRole(PlayerRole.HOST);
        player.setUserToken("12345abc");
        player.setElapsedTime(0L);
        player.setPlayerTermStatus(PlayerTermStatus.NOT_SET);

        entityManager.merge(player);
        entityManager.flush();

        // when
        Player found = playerRepository.findByUserToken(player.getUserToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getName(), player.getName());
        assertEquals(found.getStatus(), player.getStatus());
        assertEquals(found.getScore(), player.getScore());
        assertEquals(found.getRole(), player.getRole());
        assertEquals(found.getUserToken(), player.getUserToken());
        assertEquals(found.getElapsedTime(), player.getElapsedTime());
        assertEquals(found.getPlayerTermStatus(), player.getPlayerTermStatus());
    }

    @Test
    public void findByPlayerId_success() {
        // given
        Player player = new Player();
        player.setId(3L);
        player.setName("Test Player");
        player.setStatus(PlayerStatus.NOT_READY);
        player.setScore(0);
        player.setRole(PlayerRole.HOST);
        player.setUserToken("12345abc");
        player.setElapsedTime(0L);
        player.setPlayerTermStatus(PlayerTermStatus.NOT_SET);

        entityManager.merge(player);
        entityManager.flush();

        // when
        List<Player> playerList = playerRepository.findAll();
        long id = playerList.get(0).getId();

        Player found = playerRepository.findPlayerById(id);

        // then
        assertNotNull(found.getId());
        assertEquals(found.getName(), player.getName());
        assertEquals(found.getStatus(), player.getStatus());
        assertEquals(found.getScore(), player.getScore());
        assertEquals(found.getRole(), player.getRole());
        assertEquals(found.getUserToken(), player.getUserToken());
        assertEquals(found.getElapsedTime(), player.getElapsedTime());
        assertEquals(found.getPlayerTermStatus(), player.getPlayerTermStatus());
    }

    @Test
    public void findByGameGameId_success() {
        // given
        Game game = new Game();
        game.setGameId(3L);
        game.setName("Test Game 1");
        game.setStatus(GameStatus.LOBBY);
        game.setCorrectCards(0);

        entityManager.merge(game);
        entityManager.flush();

        // given
        Player player = new Player();
        player.setId(1L);
        player.setName("Test Player");
        player.setStatus(PlayerStatus.NOT_READY);
        player.setScore(0);
        player.setRole(PlayerRole.HOST);
        player.setUserToken("12345abc");
        player.setElapsedTime(0L);
        player.setPlayerTermStatus(PlayerTermStatus.NOT_SET);

        entityManager.merge(player);
        entityManager.flush();

        List<Game> games = gameRepository.findAll();
        game = games.get(0);
        List<Player> playerList = playerRepository.findAll();
        player = playerList.get(0);

        game.addPlayer(player);


        // when
        List<Player> foundList = playerRepository.findByGameGameId(player.getGame().getGameId());
        Player found = foundList.get(0);

        // then
        assertNotNull(found.getId());
        assertEquals(found.getName(), player.getName());
        assertEquals(found.getStatus(), player.getStatus());
        assertEquals(found.getScore(), player.getScore());
        assertEquals(found.getRole(), player.getRole());
        assertEquals(found.getUserToken(), player.getUserToken());
        assertEquals(found.getElapsedTime(), player.getElapsedTime());
        assertEquals(found.getPlayerTermStatus(), player.getPlayerTermStatus());

    }

}
