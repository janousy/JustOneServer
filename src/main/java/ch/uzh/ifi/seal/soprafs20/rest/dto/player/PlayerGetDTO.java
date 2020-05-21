package ch.uzh.ifi.seal.soprafs20.rest.dto.player;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerTermStatus;

public class PlayerGetDTO {
    private String name;
    private Long id;
    private PlayerStatus status;
    private int score;
    private PlayerRole role;
    private String userToken;
    private long elapsedTime;
    private PlayerTermStatus playerTermStatus;
    private int nrOfValidHints;
    private int nrOfValidGuesses;

    public int getNrOfValidHints() {
        return nrOfValidHints;
    }

    public void setNrOfValidHints(int nrOfValidHints) {
        this.nrOfValidHints = nrOfValidHints;
    }

    public int getNrOfValidGuesses() {
        return nrOfValidGuesses;
    }

    public void setNrOfValidGuesses(int nrOfValidGuesses) {
        this.nrOfValidGuesses = nrOfValidGuesses;
    }

    public PlayerTermStatus getPlayerTermStatus() {
        return playerTermStatus;
    }

    public void setPlayerTermStatus(PlayerTermStatus playerTermStatus) {
        this.playerTermStatus = playerTermStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public PlayerRole getRole() {
        return role;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
