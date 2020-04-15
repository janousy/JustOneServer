package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ROUND")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<Hint> hintList = new ArrayList<Hint>();

    @Embedded
    private Guess guess;

    @Embedded
    private Term term;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameId")
    private Game game;


    public Round() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Hint> getHintList() {
        return hintList;
    }

    public void setHintList(List<Hint> hintList) {
        this.hintList = hintList;
    }

    public Guess getGuess() {
        return guess;
    }

    public void setGuess(Guess guess) {
        this.guess = guess;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }


    public void addHint(Hint hint) {
        hintList.add(hint);
        //hint.setRoundId(id);
    }

    public void removeHint(Hint hint) {
        hintList.remove(hint);
        //hint.setRoundId(null);
    }


}
