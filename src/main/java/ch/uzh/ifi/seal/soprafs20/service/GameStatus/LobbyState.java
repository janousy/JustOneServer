package ch.uzh.ifi.seal.soprafs20.service.GameStatus;


import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Embeddable;
import java.util.List;

@Embeddable
public class LobbyState implements GameState {
    private Game game;

    public LobbyState(Game game) {
        this.game = game;
    }


    @Override
    public Player addPlayerToGame(Player playerToBeAdded) {
        //throw an error if too many players want to join the game
        if (game.getPlayerList().size() == 7) {
            String baseErrorMessage = "The lobby has already the maximum amount of players(7)";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, baseErrorMessage);
        }

        game.addPlayer(playerToBeAdded);

        return playerToBeAdded;
    }

    @Override
    public Player removePlayerFromGame(Player player) {
        return null;
    }

    @Override
    public List<Player> updatePlayerList() {
        return null;
    }

    @Override
    public List<Player> _getPlayerList() {
        return null;
    }

    @Override
    public Card _getNextCard() {
        return null;
    }

    @Override
    public Card removeCard() {
        return null;
    }

    @Override
    public void updateCorrectCards() {

    }
}
