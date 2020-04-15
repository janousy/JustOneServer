package ch.uzh.ifi.seal.soprafs20.rest.dto.action;

public class TermPostDTO implements ActionTypeDTO {
    private Long roundId;
    private String content;
    private Long wordId;

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }
}
