package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//TODO hier noch alle roundNR sachen rauswerfen

@Entity
@Table(name = "GAME")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "gameId")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int correctCards;

    @Column(nullable = false)
    private int roundNr;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> playerList = new ArrayList<Player>();

    @OneToMany(mappedBy = "game")
    private List<Round> roundList = new ArrayList<Round>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "game_card", joinColumns = @JoinColumn(name = "game_gameId"), inverseJoinColumns = @JoinColumn(name = "card_id"))
    private List<Card> cardList = new ArrayList<Card>();

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

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public int getRoundNr() {
        return roundNr;
    }

    public void setRoundNr(int roundNr) {
        this.roundNr = roundNr;
    }

    public void addPlayer(Player player) {
        playerList.add(player);
        player.setGame(this);
    }

    public void removePlayer(Player player) {
        playerList.remove(player);
        player.setGame(null);
    }

    public void addRound(Round round) {
        roundList.add(round);
        round.setGame(this);
    }

    public void removeRound(Round round) {
        roundList.remove(round);
        round.setGame(null);
    }


    public void addCard(Card card) {
        cardList.add(card);
        card.getGamelist().add(this);
    }

    public void removeCard(Card card) {
        cardList.remove(card);
        card.getGamelist().remove(this);
    }

}