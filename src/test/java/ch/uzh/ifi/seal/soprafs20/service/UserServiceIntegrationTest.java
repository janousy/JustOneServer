package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */

@WebAppConfiguration
@SpringBootTest
class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_validInputs_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("test");

        // when
        User createdUser = userService.createUser(testUser);

        // then
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
    }

    @Test
    void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        //create a testUser
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("test");

        //create User
        User createdUser = userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();

        // change the name but forget about the username
        testUser2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    @Test
    void loginUser_validInput_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        //create a TestUser
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        userRepository.save(testUser);

        //when
        User loggedInUser = userService.loginUser(testUser);

        //verify the loggedInUser
        assertEquals(testUser.getId(), loggedInUser.getId());
        assertEquals(testUser.getUsername(), loggedInUser.getUsername());
        assertNotNull(loggedInUser.getToken());
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
        assertEquals(testUser.getPassword(), loggedInUser.getPassword());
    }

    @Test
    void loginUser_userDoesNotExist_throwsException() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        //create a TestUser
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");

        // then
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser));
    }

    @Test
    void loginUser_passwordWrong_throwsException() {
        //create a testUser
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setToken("1");
        testUser.setStatus(UserStatus.OFFLINE);
        userRepository.save(testUser);

        //the information of the user who wants to login
        User testUser2 = new User();
        testUser2.setUsername("testUsername");
        testUser2.setPassword("wrongPassword");


        // then
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser2));
    }

    @Test
    void loginUser_userAlreadyLoggedIn_throwsException() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        //create a TestUser
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setToken("1");
        userRepository.save(testUser);


        // then
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser));
    }

    @Test
    void logoutUser_validInput_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        //create a TestUser
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setToken("1");
        userRepository.save(testUser);

        //when
        User loggedOutUser = userService.logoutUser(testUser);

        //verify the loggedInUser
        assertEquals(testUser.getId(), loggedOutUser.getId());
        assertEquals(testUser.getUsername(), loggedOutUser.getUsername());
        assertNotNull(loggedOutUser.getToken());
        assertEquals(UserStatus.OFFLINE, loggedOutUser.getStatus());
        assertEquals(testUser.getPassword(), loggedOutUser.getPassword());
    }

    @Test
    void logoutUser_invalidInput_throwsException() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        //create a testUser
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        userRepository.save(testUser);

        //the information of the user who wants to login
        User testUser2 = new User();
        testUser2.setUsername("testUsername2");
        testUser2.setPassword("wrongPassword");

        // then
        assertThrows(ResponseStatusException.class, () -> userService.logoutUser(testUser2));
    }

    @Test
    void getUserById_validInput_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        //create a testUser
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        userRepository.save(testUser);

        //when
        User userById = userService.getUserById(testUser.getId());

        //verify the userById
        assertEquals(testUser.getId(), userById.getId());
        assertEquals(testUser.getUsername(), userById.getUsername());
        assertNotNull(userById.getToken());
        assertEquals(UserStatus.OFFLINE, userById.getStatus());
        assertEquals(testUser.getPassword(), userById.getPassword());
    }

    @Test
    void getUserById_userDoesNotExist_throwsException() {
        //no data needs to be created
        // then
        assertThrows(ResponseStatusException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_validInput_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        //create a testUser
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        userRepository.save(testUser);

        User updateForUser = new User();
        updateForUser.setUsername("newUsername");
        updateForUser.setPassword("newPassword");

        //call the method
        User updatedUser = userService.updateUser(testUser.getId(), updateForUser);

        //verify that the user has been updated correctly
        assertEquals(testUser.getId(), updatedUser.getId());
        assertEquals(updateForUser.getUsername(), updatedUser.getUsername());
        assertNotNull(updatedUser.getToken());
        assertEquals(UserStatus.OFFLINE, updatedUser.getStatus());
        assertEquals(updatedUser.getPassword(), updatedUser.getPassword());
    }

    @Test
    void updateUser_usernameAlreadyExists_throwsException() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        //create a testUser
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        userRepository.save(testUser);

        //create a second user such that the name is already taken
        User testUser2 = new User();
        testUser2.setUsername("testUsername2");
        testUser2.setPassword("testPassword2");
        testUser2.setStatus(UserStatus.OFFLINE);
        testUser2.setToken("2");
        userRepository.save(testUser2);

        //create the update for the user
        User updateForUser = new User();
        updateForUser.setUsername("testUsername2");
        updateForUser.setPassword("testPassword2");

        //verify that a exception is thrown because the username already exists
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(1L, updateForUser));
    }

    @Test
    void verifyPasswordOfUser_validInput_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        //create a testUser
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        userRepository.save(testUser);

        //create the user input
        User userInput = new User();
        userInput.setPassword("testPassword");

        User verifiedUser = userService.verifyPasswordOfUser(testUser.getId(), userInput);

        //verify that the verified user is correct
        assertEquals(testUser.getId(), verifiedUser.getId());
        assertEquals(testUser.getUsername(), verifiedUser.getUsername());
        assertNotNull(verifiedUser.getToken());
        assertEquals(UserStatus.OFFLINE, verifiedUser.getStatus());
        assertEquals(testUser.getPassword(), verifiedUser.getPassword());
    }

    @Test
    void verifyPasswordOfUser_passwordsDontMatch() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        //create a testUser
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        userRepository.save(testUser);

        //create the user input
        User userInput = new User();
        userInput.setPassword("wrongPassword");

        User verifiedUser = userService.verifyPasswordOfUser(testUser.getId(), userInput);

        // verify that we get an empty user back
        assertNull(verifiedUser.getId());
        assertNull(verifiedUser.getUsername());
        assertNull(verifiedUser.getToken());
        assertNull(verifiedUser.getStatus());
        assertNull(verifiedUser.getPassword());
    }

    @Test
    void checkIfUserExists_UserExists_throwsException() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        //create a testUser
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        userRepository.save(testUser);

        //create a second user such that the name is already taken
        User testUser2 = new User();
        testUser2.setUsername("testUsername");

        // then
        assertThrows(ResponseStatusException.class, () -> userService.checkIfUserExists(testUser2));
    }
}
