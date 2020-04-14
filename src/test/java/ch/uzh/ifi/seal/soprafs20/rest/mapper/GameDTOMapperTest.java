package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GameDeleteDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.game.GamePostDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation works.
 */
public class GameDTOMapperTest {
    @Test
    public void testCreateGame_fromGameDTO_toGame_success() {
        // create UserPostDTO
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setName("Test Game 1");


        // MAP -> Create user
        Game game = GameDTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);

        // check content
        assertEquals(gamePostDTO.getName(), game.getName());
    }

    @Test
    public void testGetGame_fromGame_toGameGetDTO_success() {
        // create User
        Game game = new Game();
        game.setName("TestGame 1");
        game.setStatus(GameStatus.LOBBY);
        game.setGameId(1L);
        game.setCorrectCards(0);
        game.setPlayerList(new ArrayList<Player>());


        // MAP -> Create UserGetDTO
        GameGetDTO gameGetDTO = GameDTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

        // check content
        assertEquals(game.getGameId(), gameGetDTO.getGameId());
        assertEquals(game.getName(), gameGetDTO.getName());
        assertEquals(game.getStatus(), gameGetDTO.getStatus());
        assertEquals(game.getCorrectCards(), gameGetDTO.getCorrectCards());
        assertEquals(game.getPlayerList(), gameGetDTO.getPlayerList());
    }

    @Test
    public void testDeleteGame_fromGamePutDTO_toGame_success() {
        // create UserPostDTO
        GameDeleteDTO gameDeleteDTO = new GameDeleteDTO();
        gameDeleteDTO.setGameId(1L);

        // MAP -> Create user
        Game game = GameDTOMapper.INSTANCE.convertGameDeleteDTOtoEntity(gameDeleteDTO);

        // check content
        assertEquals(gameDeleteDTO.getGameId(), game.getGameId());

    }
}
