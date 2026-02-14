package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.user.models.UserEntity;
import bg.sofia.uni.fmi.mjt.project.user.models.UserModel;
import bg.sofia.uni.fmi.mjt.project.user.models.UserOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;

// I should isolate the convertor
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private final UserOutput expected = new UserOutput(
        UUID.fromString("3f5a8c2e-91b4-4c7d-a2e9-6d8f1b3c9a47"),
        "username",
        "fname lname"
    );

    private final UserEntity returns = new UserEntity(
        UUID.fromString("3f5a8c2e-91b4-4c7d-a2e9-6d8f1b3c9a47"),
        "username",
        "fname lname",
        "EUR"
    );

    @Mock
    private UserStorage userStorage;

    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(userStorage);
    }

    @Test
    void createValidUser() {
        String username = "username";
        String password = "password";
        String firstName = "fname";
        String lastName = "lname";

        when(userStorage.createUser(any(UserModel.class))).thenReturn(returns);

        UserOutput actual = service.createUser(username, password, firstName, lastName);
        assertEquals(expected.username(), actual.username());
        assertEquals(expected.fullName(), actual.fullName());
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void getExistingUserById() {
        UUID id = UUID.fromString("3f5a8c2e-91b4-4c7d-a2e9-6d8f1b3c9a47");

        when(userStorage.getUserByID(id)).thenReturn(returns);

        UserOutput actual = service.getUserById(id);
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.username(), actual.username());
        assertEquals(expected.fullName(), actual.fullName());
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void getExistingUserByUsername() {
        String username = "username";

        when(userStorage.getUserByUsername(username)).thenReturn(returns);

        UserOutput actual = service.getUserByUsername(username);
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.username(), actual.username());
        assertEquals(expected.fullName(), actual.fullName());
        verifyNoMoreInteractions(userStorage);
    }

}
