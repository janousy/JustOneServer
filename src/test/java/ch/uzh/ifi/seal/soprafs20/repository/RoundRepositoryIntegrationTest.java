package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Round;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class RoundRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoundRepository roundRepository;


    @Test
    public void findRoundById_success() {
        // given
        Round round = new Round();
        round.setId(1L);

        entityManager.merge(round);
        entityManager.flush();

        // when
        Round found = roundRepository.findRoundById(round.getId());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getGuess(), round.getGuess());
        assertEquals(found.getTerm(), round.getTerm());
        assertEquals(found.getCard(), round.getCard());
        assertEquals(found.getGame(), round.getGame());
    }

}
