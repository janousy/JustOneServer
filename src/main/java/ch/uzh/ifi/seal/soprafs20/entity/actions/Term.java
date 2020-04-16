package ch.uzh.ifi.seal.soprafs20.entity.actions;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Term implements ActionType {

    @Column(name = "roundId_Term")
    private Long roundId;

    @Column(name = "content_Term")
    private String content;

    @Column(name = "wordId_Term")
    private Long wordId;

    @Column(name = "token_Term")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
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
