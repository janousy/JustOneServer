package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.SopraServiceException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final GameRepository gameRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    //get all Games as a list
    public List<Game> getAllGames() {
        return null;
    }

    //create game
    //
    public Game createGame(Game newGame) {

        return newGame;
    }

    //delete a specific game with its id
    public Game deleteGame(Game gametobedeleted) {
        return null;
    }

    public List<Player> getAllPlayers(Long gameId) {
        return null;
    }


    public Player addPlayerToGame(Player player, Long GameId) {
        return null;
    }

    public Player removePlayerFromGame(Player player) {
        return null;
    }

    public List<Player> updatePlayerList() {
        return null;
    }

    public List<Player> getPlayerList() {
        return null;
    }

    public Card getCurrentCard() {
        return null;
    }

    public Card getNextCard() {
        return null;
    }

    public Card removeCard() {
        return null;
    }

    public void updateCorrectCards() {

    }

    public Game checkIfGameExists() {
        return null;
    }

    public void changePlayerRoles() {

    }

    public void newRound() {

    }

    public void updateScores() {

    }


}