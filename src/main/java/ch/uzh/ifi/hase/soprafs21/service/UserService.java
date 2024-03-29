package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LogedinUserPostDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

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
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getUserById(Long id){
        return this.userRepository.findByUserID(id);
    }

    public User getUserByUsername(String username){
        return this.userRepository.findByUsername(username);
    }

    public User getUserByToken (String token) {return this.userRepository.findByToken(token);}

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);

        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "Add User failed because the %s provided %s not unique.";

        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }
    }

    public User checkIfLoginPossible(User userToBeLogedIn) {
        User userByUsername = userRepository.findByUsername(userToBeLogedIn.getUsername());

        if (userByUsername == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER DOES NOT EXIST");
        }

        //check if password is correct
        if (userToBeLogedIn.getPassword().equals(userByUsername.getPassword())){
            return userByUsername;
        } else{ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PASSWORD IS NOT CORRECT");}

    }

    public User updateUser(User user, LogedinUserPostDTO logedinUserPostDTO){
        User userByUsername = userRepository.findByUsername(logedinUserPostDTO.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the change could not be made!";
        if (userByUsername != null && userByUsername.getUserID()!=logedinUserPostDTO.getUserID()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
        }
        else{
            user.setUsername(logedinUserPostDTO.getUsername());
            return user;
        }
    }

    public void login(User user){
        user.setStatus(UserStatus.ONLINE);
    }

    public static void logout(User user){
        user.setStatus(UserStatus.OFFLINE);
        System.out.print("Loged out User with ID-number "+ user.getUserID());
    }
}
