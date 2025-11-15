package bg.sofia.uni.fmi.mjt.project.server;

import bg.sofia.uni.fmi.mjt.project.relation.RelationResolver;
import bg.sofia.uni.fmi.mjt.project.relation.models.Relation;
import bg.sofia.uni.fmi.mjt.project.user.UserResolver;
import bg.sofia.uni.fmi.mjt.project.user.models.UserOutput;

import java.util.Set;

public class ApiHandler {

    private final UserResolver userResolver;
    private final RelationResolver relationResolver;

    public ApiHandler(UserResolver userResolver, RelationResolver relationResolver) {
        this.userResolver = userResolver;
        this.relationResolver = relationResolver;
    }

    public UserOutput register(String username, String password, String firstName, String lastName) {
        //TBI
        return null;
    }

    public UserOutput login(String username, String password) {
        //TBI
        return null;
    }

    public Relation addFriend(String username) {
        //TBI
        return null;
    }

    public Relation createGroup(String name, Set<String> usernames) {
        //TBI
        return null;
    }

    public void split(Float amount, String from, String to, String reason) {
        //TBA
    }

    public void splitGroup(Float amount, String name, String reason) {
        //TBA
    }

    public void getStatus(String username) {
        //TBA
    }

    public void payed(Float amount, String from, String to) {
        //TBA
    }

    public void history(String username) {
        //TBA
    }

}
