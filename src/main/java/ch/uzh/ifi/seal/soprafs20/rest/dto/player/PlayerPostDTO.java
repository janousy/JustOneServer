package ch.uzh.ifi.seal.soprafs20.rest.dto.player;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;

public class
PlayerPostDTO {
    private String name;
    private String userToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
