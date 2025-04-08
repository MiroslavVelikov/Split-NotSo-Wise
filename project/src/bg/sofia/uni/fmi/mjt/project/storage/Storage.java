package bg.sofia.uni.fmi.mjt.project.storage;

public interface Storage {
    Boolean authenticateUser(String username, String password);

    String createUser(String username, String password, String fullName);

    String addFriendConnection(String user, String friendUsername);

    String createGroup(String groupName, String[] users);

    String[] getHistory(String username);

    String[] getConnectionHistory(String username, String connectionName);

    String createPayment(String forUser, Double amount, String fromUser);

    String split(String fromUser, Double amount, String connectionName, String reason);

    String changeCurrencyForUser(String forUser, String currency);

    String[] getStatusForUser(String username);

    void save();
}
