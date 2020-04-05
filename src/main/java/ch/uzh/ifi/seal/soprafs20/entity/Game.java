package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.service.GameStatus.GameState;
import ch.uzh.ifi.seal.soprafs20.service.GameStatus.LobbyState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long gameId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int correctCards;

    @OneToMany(mappedBy = "game")
    private List<Player> playerList = new ArrayList<Player>();

    @OneToMany(mappedBy = "game")
    private List<Round> roundList = new ArrayList<Round>();

    @OneToMany(mappedBy = "game")
    private List<Card> cardList = new ArrayList<Card>();

    @Column
    private GameState gameState = new LobbyState(this);

    @Column(nullable = false)
    private GameStatus status;


    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long id) {
        this.gameId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCorrectCards() {
        return correctCards;
    }

    public void setCorrectCards(int correctCards) {
        this.correctCards = correctCards;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public List<Round> getRoundList() {
        return roundList;
    }

    public void setRoundList(List<Round> roundList) {
        this.roundList = roundList;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}