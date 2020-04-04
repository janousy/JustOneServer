package ch.uzh.ifi.seal.soprafs20.service.GameStatus;


import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;

import java.util.List;

public class LobbyState implements GameState {
    private Game game;

    public LobbyState(Game game) {
        this.game = game;
    }


    @Override
    public Player addPlayerToGame(Player player) {
        return null;
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
