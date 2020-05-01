package ch.uzh.ifi.seal.soprafs20.rest.dto.action;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;

import java.util.ArrayList;

public class HintPutDTO {
    private String token;
    private ActionTypeStatus marked;
    private ArrayList<Integer> similarity;
    private ArrayList<String> reporters;

    public ActionTypeStatus getMarked() {
        return marked;
    }

    public void setMarked(ActionTypeStatus marked) {
        this.marked = marked;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
}
