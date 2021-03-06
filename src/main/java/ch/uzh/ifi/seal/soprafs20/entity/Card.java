package ch.uzh.ifi.seal.soprafs20.entity;


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
    private List<Game> gameList = new ArrayList<>();

    @Column
    private String word1;

    @Column
    private String word2;

    @Column
    private String word3;

    @Column
    private String word4;

    @Column
    private String word5;

    public Card() {
        //empty constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord1() {
        return word1;
    }

    public void setWord1(String word1) {
        this.word1 = word1;
    }

    public String getWord2() {
        return word2;
    }

    public void setWord2(String word2) {
        this.word2 = word2;
    }

    public String getWord3() {
        return word3;
    }

    public void setWord3(String word3) {
        this.word3 = word3;
    }

    public String getWord4() {
        return word4;
    }

    public void setWord4(String word4) {
        this.word4 = word4;
    }

    public String getWord5() {
        return word5;
    }

    public void setWord5(String word5) {
        this.word5 = word5;
    }

    public String[] getTerms() {
        return new String[]{word1, word2, word3, word4, word5};
    }

    public List<Game> getGamelist() {
        return gameList;
    }

    public void setGame(List<Game> gameList) {
        this.gameList = gameList;
    }

}
