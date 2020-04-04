package ch.uzh.ifi.seal.soprafs20.rest.dto.game;


import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.service.GameStatus.GameState;

public class GameGetDTO {

    private Long gameId;

    private String name;

    private GameStatus status;

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

    public String getStatus() {
        return status.getClass().toString();
    }

    public void setStatus(GameStatus gameStatus) {
        this.status = gameStatus;
    }

    public int getCorrectCards() {
        return correctCards;
    }

    public void setCorrectCards(int correctCards) {
        this.correctCards = correctCards;
    }

}
