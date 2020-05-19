package ch.uzh.ifi.seal.soprafs20.entity.actions;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.ArrayList;

@Embeddable
public class Hint implements ActionType {

    @Column(name = "roundId_Hint")
    private Long roundId;

    @Column(name = "content_Hint")
    private String content;

    @Column(name = "token_Hint")
    private String token;

    @Column(name = "status_Hint")
    private ActionTypeStatus status;

    @Column(name = "marked_Hint")
    private ActionTypeStatus marked;

    @Column(name = "invalidCounter_Hint")
    private int invalidCounter;

    @Column(name = "similarity_Hint")
    private ArrayList<Integer> similarity = new ArrayList<>();

    @Column(name = "reporters_Hint")
    private ArrayList<String> reporters = new ArrayList<>();

    public ActionTypeStatus getMarked() {
        return marked;
    }

    public void setMarked(ActionTypeStatus marked) {
        this.marked = marked;
    }

    public ArrayList<String> getReporters() {
        return reporters;
    }

    public void setReporters(ArrayList<String> reporter) {
        this.reporters = reporter;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getInvalidCounter() {
        return invalidCounter;
    }

    public void setInvalidCounter(int invalidCounter) {
        this.invalidCounter = invalidCounter;
    }

    public ArrayList<Integer> getSimilarity() {
        return similarity;
    }

    public void setSimilarity(ArrayList<Integer> similarity) {
        this.similarity = similarity;
    }

    public ActionTypeStatus getStatus() {
        return status;
    }

    public void setStatus(ActionTypeStatus status) {
        this.status = status;
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
