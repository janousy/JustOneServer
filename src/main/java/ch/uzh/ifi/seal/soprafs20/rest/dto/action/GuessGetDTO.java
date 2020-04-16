package ch.uzh.ifi.seal.soprafs20.rest.dto.action;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;

public class GuessGetDTO {
    private Long roundId;
    private String content;
    private String token;
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
}
