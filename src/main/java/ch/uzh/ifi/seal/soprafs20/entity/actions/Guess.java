package ch.uzh.ifi.seal.soprafs20.entity.actions;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Guess {

    @Column(name = "roundId_Guess")
    private Long roundId;

    @Column(name = "content_Guess")
    private String content;

    @Column(name = "token_Guess")
    private String token;

    public Guess() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
