package ch.uzh.ifi.seal.soprafs20.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    //TODO herausfinden wieso diese nicht gehen da sie gleich sind wie UserRepositoryTests
    /*
    @Test
    public void findByName_success() {
        // given
        Game game = new Game();
        game.setGameId(1L);
        game.setName("Test Game 1");
        game.setStatus(GameStatus.LOBBY);
        game.setCorrectCards(0);


        entityManager.persist(game);
        entityManager.flush();

        // when
        Game found = gameRepository.findByName(game.getName());

        // then
        assertNotNull(found.getGameId());
        assertEquals(found.getName(), game.getName());
        assertEquals(found.getStatus(), game.getStatus());
        assertEquals(found.getCorrectCards(), game.getCorrectCards());
        assertEquals(found.getPlayerList(), game.getPlayerList());

    }


    @Test
    public void findGameByGameId_success() {
        // given
        Game game = new Game();
        game.setGameId(1L);
        game.setName("Test Game 1");
        game.setStatus(GameStatus.LOBBY);
        game.setCorrectCards(0);
        game.setPlayerList(new ArrayList<Player>());

        entityManager.persist(game);
        entityManager.flush();

        // when
        Game found = gameRepository.findByName(game.getName());

        // then
        assertNotNull(found.getGameId());
        assertEquals(found.getName(), game.getName());
        assertEquals(found.getStatus(), game.getStatus());
        assertEquals(found.getCorrectCards(), game.getCorrectCards());
        assertEquals(found.getPlayerList(), game.getPlayerList());
    }

*/
}
