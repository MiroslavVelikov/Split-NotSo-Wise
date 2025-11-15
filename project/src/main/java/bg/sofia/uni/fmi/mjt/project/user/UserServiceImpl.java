package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.common.Utils;
import bg.sofia.uni.fmi.mjt.project.user.models.UserModel;
import bg.sofia.uni.fmi.mjt.project.user.models.UserOutput;

import java.util.UUID;

public class UserServiceImpl implements UserService {

    private static final String DEFAULT_CURRENCY = "BGN";

    private final UserRepository storage;

    public UserServiceImpl(UserRepository storage) {
        this.storage = storage;
    }

    @Override
    public UserOutput createUser(String username, String password, String firstName, String lastName) {
        UserModel model = new UserModel(UUID.randomUUID(), username, Utils.hashPassword(password),
            firstName, lastName, DEFAULT_CURRENCY);

        return Convertor.convertUserServiceToOutput(storage.addUser(model));
    }

    @Override
    public UserOutput getUserById(UUID id) {
        return Convertor.convertUserServiceToOutput(storage.getUserByID(id));
    }

    @Override
    public UserOutput getUserByUsername(String username) {
        return Convertor.convertUserServiceToOutput(storage.getUserByUsername(username));
    }

}
