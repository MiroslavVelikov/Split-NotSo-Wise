package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.user.models.UserModel;
import bg.sofia.uni.fmi.mjt.project.user.models.UserService;

import java.util.UUID;

public interface UserRepository {

    UserService addUser(UserModel user);
    UserService getUserByID(UUID id);
    UserService getUserByUsername(String username);

}
