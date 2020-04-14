package ch.uzh.ifi.seal.soprafs20.entity.actions;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Guess {

    @Column(name = "roundId_Guess")
    private Long roundId;
    @Column(name = "content_Guess")
    private String content;

    public Guess() {
    }

    public Long getRoundId() {
        return roundId;
    }

    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
