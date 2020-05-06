package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
//TODO extend this test
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setToken("1");
        testUser.setOverallScore(0);
        testUser.setStatus(UserStatus.OFFLINE);

        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findUserById(Mockito.any())).thenReturn(testUser);

    }

    @Test
    public void createUser_validInputs_success() {
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        // when -> any object is being save in the userRepository -> return the dummy testUser
        User createdUser = userService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
    }

    @Test
    public void createUser_duplicateInputs_throwsException() {

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void loginUser_validInput_success() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        User loggedInUser = userService.loginUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(Mockito.any());

        assertEquals(testUser.getId(), loggedInUser.getId());
        assertEquals(testUser.getUsername(), loggedInUser.getUsername());
        assertNotNull(loggedInUser.getToken());
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
        assertEquals(testUser.getPassword(), loggedInUser.getPassword());
    }

    @Test
    public void loginUser_userDoesNotExist_throwsException() {
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        // then
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser));
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(Mockito.any());
    }

    @Test
    public void loginUser_passwordWrong_throwsException() {
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("testUsername");
        testUser2.setPassword("wrongPassword");
        testUser2.setToken("2");
        testUser2.setOverallScore(0);
        testUser2.setStatus(UserStatus.OFFLINE);

        // then
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser2));
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(Mockito.any());
    }

    @Test
    public void loginUser_userAlreadyLoggedIn_throwsException() {
        testUser.setStatus(UserStatus.ONLINE);

        // then
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser));
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(Mockito.any());

    }

    @Test
    public void logoutUser_validInput_success() {
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);

        testUser.setStatus(UserStatus.ONLINE);

        User logoutUser = userService.logoutUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(userRepository, Mockito.times(1)).findByToken(Mockito.any());

        assertEquals(testUser.getId(), logoutUser.getId());
        assertEquals(testUser.getUsername(), logoutUser.getUsername());
        assertNotNull(logoutUser.getToken());
        assertEquals(UserStatus.OFFLINE, logoutUser.getStatus());
        assertEquals(testUser.getPassword(), logoutUser.getPassword());
    }

    @Test
    public void logoutUser_invalidInput_throwsException() {
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(null);

        // then
        assertThrows(ResponseStatusException.class, () -> userService.logoutUser(testUser));
        Mockito.verify(userRepository, Mockito.times(1)).findByToken(Mockito.any());
    }

    @Test
    public void getUserById_validInput_success() {
        User userById = userService.getUserById(1L);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).findUserById(Mockito.any());

        assertEquals(testUser.getId(), userById.getId());
        assertEquals(testUser.getUsername(), userById.getUsername());
        assertNotNull(userById.getToken());
        assertEquals(UserStatus.OFFLINE, userById.getStatus());
        assertEquals(testUser.getPassword(), userById.getPassword());
    }

    @Test
    public void getUserById_userDoesNotExist_throwsException() {
        Mockito.when(userRepository.findUserById(Mockito.any())).thenReturn(null);

        // then
        assertThrows(ResponseStatusException.class, () -> userService.getUserById(1L));
        Mockito.verify(userRepository, Mockito.times(1)).findUserById(Mockito.any());
    }

    @Test
    public void updateUser_validInput_success() {
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        User userInput = new User();
        userInput.setUsername("newUsername");
        userInput.setPassword("newPassword");

        User updatedUser = userService.updateUser(1L, userInput);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), updatedUser.getId());
        assertEquals(testUser.getUsername(), updatedUser.getUsername());
        assertNotNull(updatedUser.getToken());
        assertEquals(UserStatus.OFFLINE, updatedUser.getStatus());
        assertEquals(testUser.getPassword(), updatedUser.getPassword());
    }

    @Test
    public void updateUser_usernameAlreadyExists_throwsException() {
        User userInput = new User();
        userInput.setUsername("username");
        userInput.setPassword("newPassword");

        // then
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(1L, userInput));
        Mockito.verify(userRepository, Mockito.times(1)).findUserById(Mockito.any());
    }

    @Test
    public void verifyPasswordOfUser_validInput_success() {
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        User userInput = new User();
        userInput.setUsername("newUsername");
        userInput.setPassword("newPassword");

        User updatedUser = userService.updateUser(1L, userInput);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), updatedUser.getId());
        assertEquals(testUser.getUsername(), updatedUser.getUsername());
        assertNotNull(updatedUser.getToken());
        assertEquals(UserStatus.OFFLINE, updatedUser.getStatus());
        assertEquals(testUser.getPassword(), updatedUser.getPassword());
    }

    @Test
    public void verifyPasswordOfUser_userDoesNotExist_throwsException() {

    }

    @Test
    public void verifyPasswordOfUser_passwordsDontMatch_emptyUser() {

    }

    @Test
    public void checkIfUserExists_noUserExists_success() {

    }

    @Test
    public void checkIfUserExists_UserExists_throwsException() {

    }

}
