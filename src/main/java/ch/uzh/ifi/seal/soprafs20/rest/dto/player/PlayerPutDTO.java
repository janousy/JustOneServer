package ch.uzh.ifi.seal.soprafs20.rest.dto.player;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerTermStatus;

public class PlayerPutDTO {
    private String name;
    private PlayerStatus status;
    private int score;
    private PlayerRole role;
    private long elapsedTime;
    private PlayerTermStatus playerTermStatus;

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

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
