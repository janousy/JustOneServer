package ch.uzh.ifi.seal.soprafs20.rest.dto.action;

public class HintGetDTO {
    private Long roundId;
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
