package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.dto.user.UserGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.user.UserPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.user.UserPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    //returns a list with all users
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody

    public List<UserGetDTO> getAllUsers(@RequestParam(required = false, name = "sort_by") String sortMethod) {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }

        if (sortMethod != null) {
            userGetDTOs.sort(Comparator.comparing(UserGetDTO::getOverallScore).reversed());
        }

        return userGetDTOs;
    }

    //returns a specific user corresponding to the id
    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserByID(@PathVariable Long id) {

        //get the proper user
        User user = userService.getUserById(id);

        // convert internal representation of user back to API
        return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    //creation of a user
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = UserDTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    //login of a user
    @PutMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO loginUser(@RequestBody UserPutDTO userPutDTO) {
        // convert API user to internal representation
        User userInput = UserDTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        // login user
        User foundUser = userService.loginUser(userInput);

        // convert internal representation of user back to API
        return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(foundUser);
    }

    //logout of a user
    @PutMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO logoutUser(@RequestBody UserPutDTO userPutDTO) {
        // convert API user to internal representation
        User userInput = UserDTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        // logout user
        User loggedOutUser = userService.logoutUser(userInput);

        // convert internal representation of user back to API
        return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedOutUser);
    }

    //update of a user
    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public UserGetDTO updateUser(@PathVariable Long id, @RequestBody UserPutDTO userPutDTO) {
        //create a new User with the new values
        User userInput = UserDTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        //update user in UserService
        User updatedUser = userService.updateUser(id, userInput);

        return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);
    }

    //verify the password of a user
    @PostMapping("users/{id}/passwords")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO verifyPasswordOfUser(@PathVariable Long id, @RequestBody UserPostDTO userPostDTO) {

        User userInput = UserDTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        User verifiedUser = userService.verifyPasswordOfUser(id, userInput);

        return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(verifiedUser);

    }

}
