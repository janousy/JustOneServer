package ch.uzh.ifi.seal.soprafs20.entity.actions;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;

public interface ActionType {
    public Long getRoundId();

    public String getToken();

    public ActionTypeStatus getStatus();
}
