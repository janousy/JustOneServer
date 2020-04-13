package ch.uzh.ifi.seal.soprafs20.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Round {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "round")
    private List<Hint> hintList = new ArrayList<Hint>();

    //@OneToOne(mappedBy = "round", cascade = CascadeType.ALL)
    private Guess guess;

    //@OneToOne(mappedBy = "round", cascade = CascadeType.ALL)
    private Term term;

    @OneToOne(mappedBy = "round", cascade = CascadeType.ALL)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameId")
    private Game game;


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
        hint.setRound(this);
    }

    public void removeHint(Hint hint) {
        hintList.remove(hint);
        hint.setRound(null);
    }
}
