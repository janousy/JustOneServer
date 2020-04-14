package ch.uzh.ifi.seal.soprafs20.entity.actions;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Hint {

    @Column(name = "roundId_Hint")
    private Long roundId;
    @Column(name = "content_Hint")
    private String content;

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
