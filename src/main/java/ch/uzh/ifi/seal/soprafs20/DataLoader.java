package ch.uzh.ifi.seal.soprafs20;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.service.GameStatus.GameState;
import ch.uzh.ifi.seal.soprafs20.service.GameStatus.LobbyState;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import org.apache.tomcat.jni.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.persistence.Lob;
import java.io.*;
import java.lang.reflect.Array;
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
    private final Logger log = LoggerFactory.getLogger(DataLoader.class);

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
            //GameState gameState = new LobbyState(testGame);

            testGame.setName("testGame" + i);
            testGame.setCorrectCards(0);
            testGame.setStatus(gameStatus);
            gameRepository.save(testGame);
        }
        gameRepository.flush();
    }

    private void createInitialUsers() {

        String date = new Date().toString();

        for (int i = 1; i <= 4; i++) {
            User testUser = new User();
            Player testPlayer = new Player();

            testUser.setToken("abcdef-" + i);
            testUser.setStatus(UserStatus.ONLINE);
            testUser.setCreationDate(date);
            testUser.setUsername("testUser" + i);
            testUser.setPassword("testPassword");


            testPlayer.setName("testPlayer" + i);
            testPlayer.setStatus(i % 2 == 0 ? PlayerStatus.NOT_READY : PlayerStatus.READY);
            testPlayer.setScore(0);
            //defining a Host for each game
            testPlayer.setRole(i % 2 == 0 ? PlayerRole.GUEST : PlayerRole.HOST);
            testPlayer.setUserToken(testUser.getToken());
            testPlayer.setElapsedTime(0.00);
            //put two player player into each game, leave third game empty
            testPlayer.setGame(i < 3 ? gameRepository.findGameByGameId(1L) : gameRepository.findGameByGameId(2L));

            testUser.setPlayer(testPlayer);
            testPlayer.setUser(testUser);

            userRepository.save(testUser);
            playerRepository.save(testPlayer);
        }
        userRepository.flush();
        playerRepository.flush();
    }

    private void createInitialCards() throws IOException {
        int BATCHSIZE = 5;
        FileReader fileName = new FileReader(Objects.requireNonNull(DataLoader.class.getClassLoader().getResource("cards-EN.txt")).getPath());

        String[] termsSplitted;
        try (BufferedReader br = new BufferedReader(fileName)) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                if (!line.equals("")) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                line = br.readLine();
            }
            String everything = sb.toString();
            termsSplitted = everything.split("\n");
        }

        for (int i = BATCHSIZE; i < termsSplitted.length + BATCHSIZE; i = i + BATCHSIZE) {
            String[] termBatch = Arrays.copyOfRange(termsSplitted, i - BATCHSIZE, i);
            log.info(String.format("Init new Card with terms: %s, %s, %s, %s, %s", (Object[]) termBatch));
            Card card = new Card();
            card.setTerm1(termBatch[0]);
            card.setTerm2(termBatch[1]);
            card.setTerm3(termBatch[2]);
            card.setTerm4(termBatch[3]);
            card.setTerm5(termBatch[4]);
            cardRepository.save(card);
        }
        cardRepository.flush();
    }
}
