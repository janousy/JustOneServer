package ch.uzh.ifi.seal.soprafs20.rest.dto.game;

import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.service.GameStatus.GameState;

import java.util.List;

public class GameGetDTO {

    private Long gameId;

    private String name;

    private GameState state;

    private int correctCards;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameState getGameState() {
        return state;
    }

    public void setGameState(GameState gameState) {
        this.state = gameState;
    }

    public int getCorrectCards() {
        return correctCards;
    }

    public void setCorrectCards(int correctCards) {
        this.correctCards = correctCards;
    }

}
