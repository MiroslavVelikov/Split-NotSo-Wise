package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.user.models.UserOutput;

import java.util.UUID;

public interface UserService {

    UserOutput createUser(String username, String password, String firstName, String lastName);
    UserOutput getUserById(UUID id);
    UserOutput getUserByUsername(String username);

}
