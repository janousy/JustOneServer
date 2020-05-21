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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

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
    void getAllUsers_validInputs_success() {
        List<User> usersList = new ArrayList<User>();
        usersList.add(testUser);

        Mockito.when(userRepository.findAll()).thenReturn(usersList);

        // when -> any object is being save in the userRepository -> return the dummy testUser
        List<User> allUsers = userService.getUsers();

        // then
        Mockito.verify(userRepository, Mockito.times(1)).findAll();

        assertEquals(testUser.getId(), allUsers.get(0).getId());
        assertEquals(testUser.getUsername(), allUsers.get(0).getUsername());
        assertNotNull(allUsers.get(0).getToken());
        assertEquals(UserStatus.OFFLINE, allUsers.get(0).getStatus());
        assertEquals(testUser.getPassword(), allUsers.get(0).getPassword());
    }

    @Test
    void createUser_validInputs_success() {
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
    void createUser_duplicateInputs_throwsException() {

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void loginUser_validInput_success() {
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
    void loginUser_userDoesNotExist_throwsException() {
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        // then
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser));
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(Mockito.any());
    }

    @Test
    void loginUser_passwordWrong_throwsException() {
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
    void loginUser_userAlreadyLoggedIn_throwsException() {
        testUser.setStatus(UserStatus.ONLINE);

        // then
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser));
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(Mockito.any());

    }

    @Test
    void logoutUser_validInput_success() {
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
    void logoutUser_invalidInput_throwsException() {
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(null);

        // then
        assertThrows(ResponseStatusException.class, () -> userService.logoutUser(testUser));
        Mockito.verify(userRepository, Mockito.times(1)).findByToken(Mockito.any());
    }

    @Test
    void getUserById_validInput_success() {
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
    void getUserById_userDoesNotExist_throwsException() {
        Mockito.when(userRepository.findUserById(Mockito.any())).thenReturn(null);

        // then
        assertThrows(ResponseStatusException.class, () -> userService.getUserById(1L));
        Mockito.verify(userRepository, Mockito.times(1)).findUserById(Mockito.any());
    }

    @Test
    void updateUser_validInput_success() {
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
    void updateUser_usernameAlreadyExists_throwsException() {
        User userInput = new User();
        userInput.setUsername("username");
        userInput.setPassword("newPassword");

        // then
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(1L, userInput));
        Mockito.verify(userRepository, Mockito.times(1)).findUserById(Mockito.any());
    }

    @Test
    void verifyPasswordOfUser_validInput_success() {
        User userInput = new User();
        userInput.setPassword("password");

        User verifiedUser = userService.verifyPasswordOfUser(1L, userInput);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).findUserById(Mockito.any());

        assertEquals(testUser.getId(), verifiedUser.getId());
        assertEquals(testUser.getUsername(), verifiedUser.getUsername());
        assertNotNull(verifiedUser.getToken());
        assertEquals(UserStatus.OFFLINE, verifiedUser.getStatus());
        assertEquals(testUser.getPassword(), verifiedUser.getPassword());
    }

    @Test
    void verifyPasswordOfUser_passwordsDontMatch() {
        User userInput = new User();
        userInput.setPassword("wrongPassword");

        User verifiedUser = userService.verifyPasswordOfUser(1L, userInput);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).findUserById(Mockito.any());

        assertNull(verifiedUser.getId());
        assertNull(verifiedUser.getUsername());
        assertNull(verifiedUser.getToken());
        assertNull(verifiedUser.getStatus());
        assertNull(verifiedUser.getPassword());
    }

    @Test
    void checkIfUserExists_UserExists_throwsException() {
        User userInput = new User();
        userInput.setUsername("username");
        userInput.setPassword("newPassword");

        // then
        assertThrows(ResponseStatusException.class, () -> userService.checkIfUserExists(userInput));
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(Mockito.any());
    }

}
