package ch.uzh.ifi.seal.soprafs20.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

@Entity
@Table(name = "CARD")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Card {

    @Id
    @GeneratedValue
    private Long id;


    @ManyToOne
    private Game game;

    @OneToOne
    @JoinColumn(name = "roundId", unique = true)
    private Round round;


    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
