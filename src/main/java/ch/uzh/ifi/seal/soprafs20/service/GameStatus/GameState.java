package ch.uzh.ifi.seal.soprafs20.service.GameStatus;

import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Player;

import javax.persistence.Embeddable;
import java.util.List;

@Embeddable
public interface GameState {

    public Player addPlayerToGame(Player player);

    public Player removePlayerFromGame(Player player);

    public List<Player> updatePlayerList();

    public List<Player> _getPlayerList();

    public Card _getNextCard();

    public Card removeCard();

    public void updateCorrectCards();


}
