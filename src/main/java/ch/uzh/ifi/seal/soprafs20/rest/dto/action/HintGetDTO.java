package ch.uzh.ifi.seal.soprafs20.rest.dto.action;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;

import java.util.ArrayList;

public class HintGetDTO {
    private Long roundId;
    private String content;
    private String token;
    private ActionTypeStatus status;
    private ActionTypeStatus marked;
    private int invalidCounter;
    private ArrayList<Integer> similarity;
    private ArrayList<String> reporters;

    public int getInvalidCounter() {
        return invalidCounter;
    }

    public void setInvalidCounter(int invalidCounter) {
        this.invalidCounter = invalidCounter;
    }

    public ActionTypeStatus getMarked() {
        return marked;
    }

    public void setMarked(ActionTypeStatus marked) {
        this.marked = marked;
    }

    public ArrayList<String> getReporters() {
        return reporters;
    }

    public void setReporters(ArrayList<String> reporters) {
        this.reporters = reporters;
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
