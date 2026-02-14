package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.user.models.UserOutput;

import java.util.UUID;

public class UserResolverImpl implements UserResolver {

    private final UserService service;

    public UserResolverImpl(UserService service) {
        this.service = service;
    }

    @Override
    public UserOutput loginUser(String username, String password) {
        return null;
    }

    @Override
    public UserOutput registerUser(String username, String password, String firstName, String lastName) {
        return service.createUser(username, password, firstName, lastName);
    }

    @Override
    public UserOutput getUserById(UUID id) {
        return service.getUserById(id);
    }

    @Override
    public UserOutput getUserByUsername(String username) {
        return service.getUserByUsername(username);
    }

}
