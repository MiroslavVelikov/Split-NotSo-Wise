package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.user.models.UserEntity;

import java.util.UUID;

public interface UserStorage {

    UserEntity createUser(UserEntity user);
    UserEntity getUserByID(UUID id);
    UserEntity getUserByUsername(String username);
    UserEntity updateUser(UserEntity user);

}
