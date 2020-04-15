package ch.uzh.ifi.seal.soprafs20.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CARD")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "cardList")
    private List<Game> gameList = new ArrayList<Game>();

    @Column
    private String term1;

    @Column
    private String term2;

    @Column
    private String term3;

    @Column
    private String term4;

    @Column
    private String term5;


    public Card() {
    }

    public String getTerm1() {
        return term1;
    }

    public void setTerm1(String term1) {
        this.term1 = term1;
    }

    public String getTerm2() {
        return term2;
    }

    public void setTerm2(String term2) {
        this.term2 = term2;
    }

    public String getTerm3() {
        return term3;
    }

    public void setTerm3(String term3) {
        this.term3 = term3;
    }

    public String getTerm4() {
        return term4;
    }

    public void setTerm4(String term4) {
        this.term4 = term4;
    }

    public String getTerm5() {
        return term5;
    }

    public void setTerm5(String term5) {
        this.term5 = term5;
    }

    public String[] getTerms() {
        return new String[]{term1, term2, term3, term4, term5};
    }

    public List<Game> getGamelist() {
        return gameList;
    }

    public void setGame(List<Game> gameList) {
        this.gameList = gameList;
    }

}
