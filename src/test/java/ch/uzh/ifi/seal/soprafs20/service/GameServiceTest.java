package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.CONSTANTS;
import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerTermStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Card;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private GameService gameService;


    private Game testGame;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setName("testName");
        testGame.setCorrectCards(0);
        testGame.setStatus(GameStatus.LOBBY);


        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
    }


    @Test
    public void createGame_validInputs_success() {
        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Game createdGame = gameService.createGame(testGame);

        // then
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testGame.getGameId(), createdGame.getGameId());
        assertEquals(testGame.getName(), createdGame.getName());
        assertEquals(GameStatus.LOBBY, createdGame.getStatus());
        assertEquals(testGame.getCorrectCards(), createdGame.getCorrectCards());
        assertEquals(testGame.getPlayerList(), createdGame.getPlayerList());
        assertEquals(testGame.getCardList(), createdGame.getCardList());
        assertEquals(testGame.getRoundList(), createdGame.getRoundList());
    }

    @Test
    public void createGame_duplicateInputs_throwsException() {
        // given -> a first game has already been created
        gameService.createGame(testGame);

        // when -> setup additional mocks for GameRepository
        Mockito.when(gameRepository.findByName(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(testGame);

        // then -> attempt to create second game with same name -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(testGame));
    }

    @Test
    public void getGameById_validInputs_success() {
        // when -> setup additional mocks for GameRepository
        Mockito.when(gameRepository.findByName(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(testGame);

        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Game gameById = gameService.getGameById(1L);

        Mockito.verify(gameRepository, Mockito.times(1)).findGameByGameId(Mockito.any());


        // then
        assertEquals(testGame.getGameId(), gameById.getGameId());
        assertEquals(testGame.getName(), gameById.getName());
        assertEquals(GameStatus.LOBBY, gameById.getStatus());
        assertEquals(testGame.getCorrectCards(), gameById.getCorrectCards());
        assertEquals(testGame.getPlayerList(), gameById.getPlayerList());
        assertEquals(testGame.getCardList(), gameById.getCardList());
        assertEquals(testGame.getRoundList(), gameById.getRoundList());
    }

    @Test
    public void getGameById_wrongInputs_throwsException() {
        // when -> setup additional mocks for GameRepository
        Mockito.when(gameRepository.findByName(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> gameService.getGameById(1L));
    }

    @Test
    public void deleteGameById_validInput_success() {
        testGame.setStatus(GameStatus.DELETE);
        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(testGame);

        Game deletedGame = gameService.deleteGameById(1L);

        Mockito.verify(gameRepository, Mockito.times(1)).findGameByGameId(Mockito.any());

        // then
        assertEquals(testGame.getGameId(), deletedGame.getGameId());
        assertEquals(testGame.getName(), deletedGame.getName());
        assertEquals(GameStatus.DELETE, deletedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), deletedGame.getCorrectCards());
        assertEquals(testGame.getPlayerList(), deletedGame.getPlayerList());
        assertEquals(testGame.getCardList(), deletedGame.getCardList());
        assertEquals(testGame.getRoundList(), deletedGame.getRoundList());

    }

    @Test
    public void deleteGameById_wrongInput_returnsTheSame() {
        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(testGame);

        Game deletedGame = gameService.deleteGameById(1L);


        Mockito.verify(gameRepository, Mockito.times(1)).findGameByGameId(Mockito.any());

        // then
        assertEquals(testGame.getGameId(), deletedGame.getGameId());
        assertEquals(testGame.getName(), deletedGame.getName());
        assertEquals(GameStatus.LOBBY, deletedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), deletedGame.getCorrectCards());
        assertEquals(testGame.getPlayerList(), deletedGame.getPlayerList());
        assertEquals(testGame.getCardList(), deletedGame.getCardList());
        assertEquals(testGame.getRoundList(), deletedGame.getRoundList());
    }

    @Test
    public void checkGameReady_validInput_success() {

        //adjusting the testgame in order to let it start
        Player player1 = new Player();
        player1.setStatus(PlayerStatus.READY);
        Player player2 = new Player();
        player2.setStatus(PlayerStatus.READY);
        Player player3 = new Player();
        player3.setStatus(PlayerStatus.READY);

        testGame.addPlayer(player1);
        testGame.addPlayer(player2);
        testGame.addPlayer(player3);

        testGame.setStatus(GameStatus.RECEIVING_TERM);

        //adjust the gameService in order to make it possible to mock void method
        GameService gameService = Mockito.spy(this.gameService);
        Mockito.doNothing().when(gameService).prepareGameToPlay(testGame);

        //call method we want to test
        Game checkedGame = gameService.checkGameReady(testGame);


        Mockito.verify(gameService, Mockito.times(1)).prepareGameToPlay(Mockito.any());

        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.RECEIVING_TERM, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());
        assertEquals(testGame.getPlayerList(), checkedGame.getPlayerList());
        assertEquals(testGame.getCardList(), checkedGame.getCardList());
        assertEquals(testGame.getRoundList(), checkedGame.getRoundList());

    }

    @Test
    public void checkGameReady_playerListSmaller3_returnsTheSame() {

        Game checkedGame = gameService.checkGameReady(testGame);

        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.LOBBY, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());
        assertEquals(testGame.getPlayerList(), checkedGame.getPlayerList());
        assertEquals(testGame.getCardList(), checkedGame.getCardList());
        assertEquals(testGame.getRoundList(), checkedGame.getRoundList());

    }

    @Test
    public void checkGameReady_playerListNotReady_returnsTheSame() {
        //adjusting the testGame in order to let it start
        Player player1 = new Player();
        Player player2 = new Player();
        Player player3 = new Player();
        testGame.addPlayer(player1);
        testGame.addPlayer(player2);
        testGame.addPlayer(player3);

        Game checkedGame = gameService.checkGameReady(testGame);

        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.LOBBY, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());
        assertEquals(testGame.getPlayerList(), checkedGame.getPlayerList());
        assertEquals(testGame.getCardList(), checkedGame.getCardList());
        assertEquals(testGame.getRoundList(), checkedGame.getRoundList());

    }

    @Test
    public void checkIfPlayersKnowTerm_success() {
        //prepare the game
        Player player1 = new Player();
        player1.setStatus(PlayerStatus.CLUE_GIVER);
        player1.setPlayerTermStatus(PlayerTermStatus.KNOWN);
        testGame.addPlayer(player1);

        Player player2 = new Player();
        player2.setStatus(PlayerStatus.CLUE_GIVER);
        player2.setPlayerTermStatus(PlayerTermStatus.KNOWN);
        testGame.addPlayer(player2);

        //call the to test method
        Game checkedGame = gameService.checkIfPlayersKnowTerm(testGame);


        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.RECEIVING_HINTS, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());
        assertEquals(testGame.getPlayerList(), checkedGame.getPlayerList());
        assertEquals(testGame.getCardList(), checkedGame.getCardList());
        assertEquals(testGame.getRoundList(), checkedGame.getRoundList());

    }

    @Test
    public void checkIfPlayersKnowTerm_nrOfUnknownsToLarge() {
        //prepare the game
        //add a player to the game
        Player player1 = new Player();
        player1.setStatus(PlayerStatus.CLUE_GIVER);
        player1.setPlayerTermStatus(PlayerTermStatus.KNOWN);
        testGame.addPlayer(player1);

        //add a player to the game
        Player player2 = new Player();
        player2.setStatus(PlayerStatus.CLUE_GIVER);
        player2.setPlayerTermStatus(PlayerTermStatus.UNKNOWN);
        testGame.addPlayer(player2);

        //add a round to the game
        testGame.addRound(new Round());

        //mock the playerRepository and gameRepository call
        Mockito.when(gameRepository.findGameByGameId(Mockito.any())).thenReturn(testGame);

        //call the to be tested method
        Game checkedGame = gameService.checkIfPlayersKnowTerm(testGame);


        Mockito.verify(gameRepository, Mockito.times(1)).findGameByGameId(Mockito.any());

        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.RECEIVING_TERM, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());
        assertEquals(testGame.getPlayerList(), checkedGame.getPlayerList());
        assertEquals(testGame.getCardList(), checkedGame.getCardList());
        assertEquals(testGame.getRoundList(), checkedGame.getRoundList());

    }

    @Test
    public void checkIfPlayersKnowTerm_NotAllClueGiversHaveReported() {
        //prepare the game
        Player player1 = new Player();
        player1.setStatus(PlayerStatus.CLUE_GIVER);
        player1.setPlayerTermStatus(PlayerTermStatus.NOT_SET);
        testGame.addPlayer(player1);

        //call the to test method
        Game checkedGame = gameService.checkIfPlayersKnowTerm(testGame);


        // then
        assertEquals(testGame.getGameId(), checkedGame.getGameId());
        assertEquals(testGame.getName(), checkedGame.getName());
        assertEquals(GameStatus.LOBBY, checkedGame.getStatus());
        assertEquals(testGame.getCorrectCards(), checkedGame.getCorrectCards());
        assertEquals(testGame.getPlayerList(), checkedGame.getPlayerList());
        assertEquals(testGame.getCardList(), checkedGame.getCardList());
        assertEquals(testGame.getRoundList(), checkedGame.getRoundList());
    }

    @Test
    public void addCardsToGame_validInput_success() {
        //prepare the cardRepository.findAll mock
        List<Card> cardList = new ArrayList<Card>();
        for (int i = 0; i < 50; i++) {
            cardList.add(new Card());
        }

        Mockito.when(cardRepository.findAll()).thenReturn(cardList);

        //prepare the cardRepository.findCardById mock
        Card card = new Card();
        Mockito.when(cardRepository.findCardById(Mockito.any())).thenReturn(card);

        gameService.addCardsToGame(testGame);

        Mockito.verify(cardRepository, Mockito.times(1)).findAll();
        Mockito.verify(cardRepository, Mockito.times(CONSTANTS.NUMBER_OF_ROUNDS)).findCardById(Mockito.any());

        assertEquals(testGame.getCardList().size(), CONSTANTS.NUMBER_OF_ROUNDS);
    }

    @Test
    public void findRoundByGameId_validInput_success() {
        //prepare the gameRepository mock
        Round roundToAdd = new Round();
        roundToAdd.setId(1L);
        testGame.addRound(roundToAdd);

        Round round = gameService.findRoundByGameId(testGame);

        assertEquals(roundToAdd.getId(), round.getId());
        assertEquals(roundToAdd.getGame(), round.getGame());
    }

    @Test
    public void findRoundByGameId_gameIsNull() {

        assertThrows(ResponseStatusException.class, () -> gameService.findRoundByGameId(testGame));
    }

    @Test
    public void findRoundByGameId_RoundListIsTooSmall() {

        assertThrows(ResponseStatusException.class, () -> gameService.findRoundByGameId(testGame));
    }

}
