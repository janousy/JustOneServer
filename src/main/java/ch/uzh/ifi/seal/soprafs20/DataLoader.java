package ch.uzh.ifi.seal.soprafs20;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerRole;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.service.GameStatus.GameState;
import ch.uzh.ifi.seal.soprafs20.service.GameStatus.LobbyState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.persistence.Lob;
import java.util.Date;

/*
This Class automatically loads data into the H2 database. The run method is automatically
executed upon application startup. Comment out both methods in run() to get an empty DB.
 */
@Component
public class DataLoader implements ApplicationRunner {

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    @Autowired
    public DataLoader(@Qualifier("userRepository") UserRepository userRepository,
                      @Qualifier("gameRepository") GameRepository gameRepository,
                      @Qualifier("playerRepository") PlayerRepository playerRepository) {

        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public void run(ApplicationArguments args) {
        createInitialGames();
        createInitialUsers();
    }


    private void createInitialGames() {

        for (int i = 1; i <= 3; i++) {
            Game testGame = new Game();
            GameStatus gameStatus = GameStatus.IDLE;
            GameState gameState = new LobbyState(testGame);

            testGame.setName("testGame" + i);
            testGame.setCorrectCards(0);
            testGame.setGameState(gameState);
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
            testPlayer.setStatus(PlayerStatus.WAITING);
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
}
