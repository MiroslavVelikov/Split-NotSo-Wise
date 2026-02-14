package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.common.Utils;
import bg.sofia.uni.fmi.mjt.project.user.models.UserEntity;
import bg.sofia.uni.fmi.mjt.project.user.models.UserOutput;

import java.util.UUID;

public class UserServiceImpl implements UserService {

    private static final String DEFAULT_CURRENCY = "EUR";

    private final UserStorage storage;

    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public UserOutput authenticateUser(String username, String password) {
        UserEntity user = storage.getUserByUsername(username);
        return user != null && user.getPassword().equals(Utils.hashPassword(password))
            ? Convertor.convertUserServiceToOutput(user) : null;
    }

    @Override
    public UserOutput createUser(String username, String password, String firstName, String lastName) {
        UserEntity model = new UserEntity(UUID.randomUUID(), username, Utils.hashPassword(password),
            firstName, lastName, DEFAULT_CURRENCY);

        return Convertor.convertUserServiceToOutput(storage.createUser(model));
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
