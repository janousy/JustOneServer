package ch.uzh.ifi.seal.soprafs20;

import ch.uzh.ifi.seal.soprafs20.constant.*;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

/*
This Class automatically loads data into the H2 database. The run method is automatically
executed upon application startup. Comment out both methods in run() to get an empty DB.
 */
@Component
public class DataLoader implements ApplicationRunner {

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;

    @Autowired
    public DataLoader(@Qualifier("userRepository") UserRepository userRepository,
                      @Qualifier("gameRepository") GameRepository gameRepository,
                      @Qualifier("playerRepository") PlayerRepository playerRepository,
                      @Qualifier("cardRepository") CardRepository cardRepository) {

        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
    }

    public void run(ApplicationArguments args) throws IOException {
        createInitialGames();
        createInitialUsers();
        createInitialCards();
    }

    private void createInitialGames() {

        for (int i = 1; i <= 3; i++) {
            Game testGame = new Game();
            GameStatus gameStatus = GameStatus.LOBBY;

            testGame.setName("testGame" + i);
            testGame.setCorrectCards(0);
            testGame.setStatus(gameStatus);
            gameRepository.save(testGame);
        }
        gameRepository.flush();
    }


    private void createInitialUsers() {
        int numberOfPlayers = 8;

        for (int i = 1; i <= numberOfPlayers; i++) {
            User testUser = new User();
            Player testPlayer = new Player();
            testUser.setToken("abcdef-" + i);
            testUser.setStatus(UserStatus.ONLINE);
            testUser.setUsername("testUser" + i);
            testUser.setPassword("testPassword");


            testPlayer.setName("testPlayer" + i);
            testPlayer.setStatus(i % numberOfPlayers / 2 == 0 ? PlayerStatus.NOT_READY : PlayerStatus.READY);
            testPlayer.setScore(0);
            //defining a Host for each game
            testPlayer.setRole(i % numberOfPlayers / 2 == 0 ? PlayerRole.HOST : PlayerRole.GUEST);
            testPlayer.setUserToken(testUser.getToken());
            testPlayer.setElapsedTime(0L);

            //put two player player into each game, leave third game empty
            testPlayer.setGame(i <= numberOfPlayers / 2 ? gameRepository.findGameByGameId(1L) : gameRepository.findGameByGameId(2L));
            testUser.setPlayer(testPlayer);
            testPlayer.setUser(testUser);
            testPlayer.setPlayerTermStatus(PlayerTermStatus.NOT_SET);

            userRepository.save(testUser);
            playerRepository.save(testPlayer);
        }

        userRepository.flush();
        playerRepository.flush();
    }


    private void createInitialCards() throws IOException {

        int batchsize = 5;
        InputStream inputStream = DataLoader.class.getClassLoader().getResourceAsStream("cards-EN.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();

        while (line != null) {
            if (!line.equals("")) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
            line = bufferedReader.readLine();
        }
        String everything = stringBuilder.toString();
        String[] termsSplitted = everything.split("\n");

        for (int i = batchsize; i < termsSplitted.length + batchsize; i = i + batchsize) {
            String[] termBatch = Arrays.copyOfRange(termsSplitted, i - batchsize, i);
            Card card = new Card();
            card.setWord1(termBatch[0]);
            card.setWord2(termBatch[1]);
            card.setWord3(termBatch[2]);
            card.setWord4(termBatch[3]);
            card.setWord5(termBatch[4]);
            cardRepository.save(card);
        }
        cardRepository.flush();

    }
}
