package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Card;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
public class CardRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CardRepository cardRepository;

    @Test
    public void findCardById_success() {
        // given
        Card card = new Card();
        card.setId(1L);
        card.setWord1("Word1");
        card.setWord2("Word2");
        card.setWord3("Word3");
        card.setWord4("Word4");
        card.setWord5("Word5");

        entityManager.merge(card);
        entityManager.flush();

        // when
        Card found = cardRepository.findCardById(1L);

        // then
        assertNotNull(found.getId());
        assertEquals(found.getWord1(), card.getWord1());
        assertEquals(found.getWord2(), card.getWord2());
        assertEquals(found.getWord3(), card.getWord3());
        assertEquals(found.getWord4(), card.getWord4());
        assertEquals(found.getWord5(), card.getWord5());
    }

}