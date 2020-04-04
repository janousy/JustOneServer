package ch.uzh.ifi.seal.soprafs20.rest.dto.game;


import ch.uzh.ifi.seal.soprafs20.service.GameStatus.GameState;

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


    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public int getCorrectCards() {
        return correctCards;
    }

    public void setCorrectCards(int correctCards) {
        this.correctCards = correctCards;
    }

}
