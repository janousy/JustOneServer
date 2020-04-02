package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.player.HumanPlayer;
import ch.uzh.ifi.seal.soprafs20.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("playerRepository")
public interface PlayerRepository extends JpaRepository<HumanPlayer, Long> {
    HumanPlayer findByName(String name);
}
