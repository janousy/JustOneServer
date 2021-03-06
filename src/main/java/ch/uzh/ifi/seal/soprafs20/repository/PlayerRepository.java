package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("playerRepository")
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByName(String name);
    List<Player> findByGameGameId(Long gameId); //special for joined column, see column naming in h2 db

    Player findByUserToken(String token);

    Player findPlayerById(Long id);
}
