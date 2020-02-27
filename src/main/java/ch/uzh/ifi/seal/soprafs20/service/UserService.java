package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.SopraServiceException;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.text.ParseException;


import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    //create user service and adding it into the database
    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);

        checkIfUserExists(newUser);
        Date date = new Date();
        newUser.setCreationDate(date.toString());

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        List<User> users = getUsers();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws SopraServiceException
     * @see ch.uzh.ifi.seal.soprafs20.entity.User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        User userByName = userRepository.findByName(userToBeCreated.getName());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null && userByName != null) {
            throw new SopraServiceException(String.format(baseErrorMessage, "username and the name", "are"));
        }
        else if (userByUsername != null) {
            throw new SopraServiceException(String.format(baseErrorMessage, "username", "is"));
        }
        else if (userByName != null) {
            throw new SopraServiceException(String.format(baseErrorMessage, "name", "is"));
        }
    }


    //login user
    public User loginUser(User userInput){
        User userByUsername = userRepository.findByUsername(userInput.getUsername());

        if(userByUsername == null){
            throw new SopraServiceException("The username is not correct or the user does not exist");
        }
        else if(!userByUsername.getPassword().equals(userInput.getPassword())){
            throw new SopraServiceException("The password is not correct");
        }

        userByUsername.setStatus(UserStatus.ONLINE);
        userRepository.flush();

        log.debug("Logged in User: {}", userByUsername);
        return userByUsername;
    }

    //logout user
    public User logoutUser(User userInput){
        User toLogOutUser = userRepository.findByToken(userInput.getToken());

        toLogOutUser.setStatus(UserStatus.OFFLINE);

        return toLogOutUser;
    }

    //gets the user by its corresponding it
    //return: User
    public User getUserById(Long UserId){

        List<User> users = getUsers();
        User userById = null;

        for(User user:users){
            if(user.getId().equals(UserId)){
                userById = user;
            }
        }

        if(userById == null){
            throw new SopraServiceException("The id is not correct or the id does not exist");
        }

        log.debug("Found User by Id: {}", userById);
        return userById;
    }

    //updates a user
    //return: void
    public void updateUser(Long id, User userInput){

        //find user which should be updated
        User oldUser = getUserById(id);

        if(oldUser == null){
            throw new SopraServiceException("The user does not exist which should be updated");
        }

        if(userInput.getBirthDate() != null) {
            if (!validateDate(userInput.getBirthDate())) {
                throw new SopraServiceException("You did not input a correct date as birth date, please input the birth date in the format 1.1.1900");
            }
        }

        //update the two user fields
        if(userInput.getUsername() != null){
            oldUser.setUsername(userInput.getUsername());
        }
        if(userInput.getBirthDate() != null) {
            oldUser.setBirthDate(userInput.getBirthDate());
        }
        userRepository.flush();
        log.debug("Updated user: {}", oldUser);
    }

    //checks whether a date is correct
    public static boolean validateDate(String strDate) {
            SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
            date.setLenient(false);

            try
            {
                Date javaDate = date.parse(strDate);
            }
            // Date format is invalid
            catch (ParseException e)
            {
                return false;
            }

            // Return true if date format is valid
            return true;
    }

}
