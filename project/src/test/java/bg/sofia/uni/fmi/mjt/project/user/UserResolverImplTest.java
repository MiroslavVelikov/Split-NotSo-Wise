package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.user.models.UserOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UserResolverImplTest {

    private final UserOutput expected = new UserOutput(
        UUID.randomUUID(),
        "username",
        "fname lname"
    );

    @Mock
    private UserService userService;

    private UserResolverImpl resolver;

    @BeforeEach
    void setUp() {
        resolver = new UserResolverImpl(userService);
    }

    @Test
    void registerValidUser() {
        String username = "username";
        String password = "password";
        String firstName = "fname";
        String lastName = "lname";

        when(userService.createUser(username, password, firstName, lastName)).thenReturn(expected);

        UserOutput actual = resolver.registerUser(username, password, firstName, lastName);

        assertSame(expected, actual, "Resolver should return exactly what service returns");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getExistingUserById() {
        UUID id = UUID.randomUUID();

        when(userService.getUserById(id)).thenReturn(expected);

        UserOutput actual = resolver.getUserById(id);

        assertSame(expected, actual, "Resolver should return exactly what service returns");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getExistingUserByUsername() {
        String username = "user";

        when(userService.getUserByUsername(username)).thenReturn(expected);

        UserOutput actual = resolver.getUserByUsername(username);

        assertSame(expected, actual, "Resolver should return exactly what service returns");
        verifyNoMoreInteractions(userService);
    }

}
