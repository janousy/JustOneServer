package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    //TODO herausfinden wieso diese nicht gehen da sie gleich sind wie UserRepositoryTests

    @Test
    public void findByName_success() {
        // given
        Game game = new Game();
        game.setGameId(1L);
        game.setName("Test Game 1");
        game.setStatus(GameStatus.LOBBY);
        game.setCorrectCards(0);


        entityManager.merge(game);
        entityManager.flush();

        // when
        Game found = gameRepository.findByName(game.getName());

        // then
        assertNotNull(found.getGameId());
        assertEquals(found.getName(), game.getName());
        assertEquals(found.getStatus(), game.getStatus());
        assertEquals(found.getCorrectCards(), game.getCorrectCards());

    }


    @Test
    public void findGameByGameId_success() {
        // given
        Game game = new Game();
        game.setGameId(1L);
        game.setName("Test Game 1");
        game.setStatus(GameStatus.LOBBY);
        game.setCorrectCards(0);

        entityManager.merge(game);
        entityManager.flush();

        // when
        Game found = gameRepository.findByName(game.getName());

        // then
        assertNotNull(found.getGameId());
        assertEquals(found.getName(), game.getName());
        assertEquals(found.getStatus(), game.getStatus());
        assertEquals(found.getCorrectCards(), game.getCorrectCards());
    }

}
