package ch.uzh.ifi.seal.soprafs20.entity.actions;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;

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

    @Column(name = "status_GUESS")
    private ActionTypeStatus status;

    public ActionTypeStatus getStatus() {
        return status;
    }

    public void setStatus(ActionTypeStatus status) {
        this.status = status;
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

    public Guess() {
    }
}
