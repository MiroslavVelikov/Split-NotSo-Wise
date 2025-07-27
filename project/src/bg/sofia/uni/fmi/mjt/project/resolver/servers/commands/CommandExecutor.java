package bg.sofia.uni.fmi.mjt.project.resolver.servers.commands;

import bg.sofia.uni.fmi.mjt.project.exceptions.InvalidUser;
import bg.sofia.uni.fmi.mjt.project.models.database.relation.Relation;
import bg.sofia.uni.fmi.mjt.project.models.database.transaction.Split;
import bg.sofia.uni.fmi.mjt.project.models.database.transaction.Transaction;
import bg.sofia.uni.fmi.mjt.project.models.database.user.User;
import bg.sofia.uni.fmi.mjt.project.models.output.NotificationManager;
import bg.sofia.uni.fmi.mjt.project.models.output.NotificationOutput;
import bg.sofia.uni.fmi.mjt.project.service.Service;
import bg.sofia.uni.fmi.mjt.project.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandExecutor {
    private static final String UNKNOWN_COMMAND = "$ Unknown command (Use help to see the available commands)" + System.lineSeparator();
    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
        "$ Invalid count of arguments: \"%s\" expects %d arguments. Example: \"%s\"" + System.lineSeparator();
    private static final String INVALID_USERNAME_OR_PASSWORD_MESSAGE =
        "$ Invalid username or password" + System.lineSeparator();
    private static final String INVALID_PERSONAL_DATA_MESSAGE =
        "$ Invalid first or last name format" + System.lineSeparator();
    private static final String USERNAME_AND_PASSWORD_FORMAT = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{3,}$";
    private static final String PERSONAL_NAME_FORMAT = "^[a-zA-Z]{3,}$";

    private static final int REQUIRE_ONE_ARGUMENTS = 1;
    private static final int REQUIRE_TWO_ARGUMENTS = 2;
    private static final int REQUIRE_THREE_ARGUMENTS = 3;
    private static final int REQUIRE_FOUR_ARGUMENTS = 4;

    private final Service service;
    private Map<String, UUID> sessions;

    public CommandExecutor(Service service) {
        this.service = service;
        this.sessions = new HashMap<>();
    }

    public String execute(Command cmd) {
        try {
            CommandType command = CommandType.fromString(cmd.command());
            return switch (command) {
                case LOGIN -> login(cmd.arguments(), cmd.clientId());
                case REGISTER -> register(cmd.arguments(), cmd.clientId());
                case LOGOUT -> logout(cmd.clientId());
                case ADD_FRIEND -> addFriend(cmd.clientId(), cmd.arguments());
                case CREATE_GROUP -> createGroup(cmd.clientId(), cmd.arguments());
                case SPLIT -> split(cmd.clientId(), cmd.arguments());
                case SPLIT_GROUP -> splitGroup(cmd.clientId(), cmd.arguments());
                case GET_STATUS -> getStatus(cmd.clientId());
                case GET_HISTORY -> getHistory(cmd.clientId());
                case PAYED -> payed(cmd.clientId(), cmd.arguments());
                case SWITCH_CURRENCY -> switchCurrency(cmd.clientId(), cmd.arguments());
                case HELP -> help(cmd.clientId());
                default -> UNKNOWN_COMMAND;
            };
        } catch (Exception e) {
            return UNKNOWN_COMMAND;
        }
    }

    private String login(String[] args, String clientId) {
        if (sessions.containsKey(clientId)) {
            return UNKNOWN_COMMAND;
        }

        if (args.length != REQUIRE_TWO_ARGUMENTS) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, CommandType.LOGIN, REQUIRE_TWO_ARGUMENTS,
                CommandType.LOGIN + " <username> <password>");
        }

        String username = args[0], password = args[1];
        NotificationManager notificationManager = service.login(username, password);
        if (notificationManager == null) {
            return INVALID_USERNAME_OR_PASSWORD_MESSAGE;
        }

        StringBuilder output = new StringBuilder();
        output.append("Successful login!").append(System.lineSeparator());
        if (notificationManager.friendsNotifications().isEmpty()
            && notificationManager.groupsNotifications().isEmpty()) {
            output.append("No notifications to show.").append(System.lineSeparator());
        } else {
            output.append("Friends:").append(System.lineSeparator());
            if (notificationManager.friendsNotifications().isEmpty()) {
                output.append("No notifications from friends.").append(System.lineSeparator());
            }
            for (NotificationOutput notification : notificationManager.friendsNotifications()) {
                output.append(notification.notification()).append(System.lineSeparator());
            }

            output.append(System.lineSeparator()).append("Groups:").append(System.lineSeparator());
            for (Map.Entry<String, Set<NotificationOutput>> group : notificationManager.groupsNotifications().entrySet()) {
                output.append(String.format("* %s:", group.getKey())).append(System.lineSeparator());
                for (NotificationOutput notification : group.getValue()) {
                    output.append(notification.notification()).append(System.lineSeparator());
                }
                output.append(System.lineSeparator());
            }
        }

        sessions.put(clientId, notificationManager.user().getId());
        return output.toString();
    }

    private String register(String[] args, String clientId) {
        if (sessions.containsKey(clientId)) {
            return UNKNOWN_COMMAND;
        }

        if (args.length != REQUIRE_FOUR_ARGUMENTS) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, CommandType.REGISTER, REQUIRE_FOUR_ARGUMENTS,
                CommandType.REGISTER + " <username> <password> <first_name> <last_name>");
        }

        String username = args[0], password = args[1];
        if (!validateString(username, false) || !validateString(password, false)) {
            return String.format(INVALID_USERNAME_OR_PASSWORD_MESSAGE);
        }

        String firstName = args[2], lastName = args[3];
        if (!validateString(firstName, true) || !validateString(lastName, true)) {
            return String.format(INVALID_PERSONAL_DATA_MESSAGE);
        }

        User newUser = service.register(username, password, firstName, lastName);
        if (newUser != null) {
            sessions.put(clientId, newUser.getId());

            return "Successful register! Now you are logged in!" + System.lineSeparator();
        }

        return "Failed to register user" + System.lineSeparator();
    }

    private String logout(String clientId) {
        if (!sessions.containsKey(clientId)) {
            return UNKNOWN_COMMAND;
        }

        sessions.remove(clientId);
        return "Logged out.";
    }

    private String addFriend(String clientId, String[] args) {
        if (!sessions.containsKey(clientId)) {
            return UNKNOWN_COMMAND;
        }

        if (args.length != REQUIRE_ONE_ARGUMENTS) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, CommandType.ADD_FRIEND, REQUIRE_ONE_ARGUMENTS,
                CommandType.ADD_FRIEND + " <username>");
        }

        String friendUsername = args[0];
        if (validateString(friendUsername, false)) {
            return "Invalid friend's username.";
        }

        return service.addFriend(sessions.get(clientId), friendUsername) != null
            ? String.format("%s is now your friend.", friendUsername)
            : String.format("You and %s are already friends.", friendUsername);
    }

    private String createGroup(String clientId, String[] args) {
        if (!sessions.containsKey(clientId)) {
            return UNKNOWN_COMMAND;
        }

        if (args.length < REQUIRE_THREE_ARGUMENTS) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, CommandType.ADD_FRIEND, REQUIRE_THREE_ARGUMENTS,
                CommandType.CREATE_GROUP + " <group_name> <username> <username> ...");
        }

        String groupName = args[0];
        Set<String> usernames = Arrays.stream(Arrays.copyOfRange(args, 1, args.length - 1))
            .collect(Collectors.toSet());
        if (usernames.size() != args.length - 1) {
            return "Cannot have repetitive usernames.";
        }

        return service.createGroup(sessions.get(clientId), groupName, usernames) == null
            ? "A problem occurred while creating the group"
            : "Group was successfully created";
    }

    private String split(String clientId, String[] args) {
        if (!sessions.containsKey(clientId)) {
            return UNKNOWN_COMMAND;
        }

        if (args.length != REQUIRE_THREE_ARGUMENTS) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, CommandType.SPLIT, REQUIRE_THREE_ARGUMENTS,
                CommandType.SPLIT + " <amount> <username> <reason_for_payment>");
        }

        try {
            Double amount = Double.parseDouble(args[0]);
            String friendUsername = args[1], reason = args[2];

            User currentClient, friend;
            try {
                friend = getValidUser(friendUsername);
                currentClient = getValidUser(sessions.get(clientId));
            } catch (InvalidUser e) {
                return e.getMessage();
            }

            Relation friendRelation = service.getMutualRelations(
                    currentClient.getId(), friend.getId()
            ).stream()
                .filter(r -> r.getType().equals(Utils.FRIEND_TYPE))
                .findFirst()
                .orElse(null);

            if (friendRelation == null) {
                return String.format("You and %s are not friends.", friendUsername);
            }

            Split createSplit = service.createSplit(
                currentClient.getId(), friendRelation.getId(), amount, reason
            );
            if (createSplit == null) {
                return "$ Something went wrong creating the split with " + friendUsername;
            }

            return String.format("""
                $ Splitted %f %s between you and %s.
                [%s]
                """, amount, currentClient.getCurrencyPreference().getCurrency(),
                friendUsername, reason);
        } catch (Exception e) {
            return "Invalid data arguments";
        }
    }

    private String splitGroup(String clientId, String[] args) {
        if (!sessions.containsKey(clientId)) {
            return UNKNOWN_COMMAND;
        }

        if (args.length != REQUIRE_THREE_ARGUMENTS) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, CommandType.SPLIT, REQUIRE_THREE_ARGUMENTS,
                CommandType.SPLIT_GROUP + " <amount> <group_id> <reason_for_payment>");
        }

        User currentClient;
        try {
            currentClient = getValidUser(sessions.get(clientId));
        } catch (InvalidUser e) {
            return e.getMessage();
        }

        Double amount = Double.parseDouble(args[0]);
        UUID groupId = UUID.fromString(args[1]);
        String reason = args[2];

        Relation group = service.getRelationById(groupId);
        if (group == null) {
            return "$ Something went wrong finding group with id: " + groupId;
        }

        Split newSplit = service.createSplit(currentClient.getId(), group.getId(), amount, reason);
        if (newSplit == null) {
            return "$ Something went wrong creating split with group: " + groupId;
        }

        return String.format("$ Splitted %f %s successfully in group %s",
            amount, currentClient.getCurrencyPreference(), group.getName());
    }

    private String getStatus(String clientId) {
        if (!sessions.containsKey(clientId)) {
            return UNKNOWN_COMMAND;
        }

        User currentClient = service.getUserById(sessions.get(clientId));
        Set<Transaction> notPayedTransactions = service.getUserNotPayedTransactions(currentClient.getId());
        if (notPayedTransactions.isEmpty()) {
            return "You do not have any not payed transactions at the moment.";
        }

        StringBuilder statusStr = new StringBuilder();
        for (Transaction transaction : notPayedTransactions) {
            String messageFormat;
            User user;

            if (transaction.getReceiverId().equals(currentClient.getId())) {
                messageFormat = "%s %s %s (%s) payed you %f %s [%s]";
                user = service.getUserById(transaction.getSenderId());
            } else {
                messageFormat = "%s You payed %s %s (%s) %f %s [%s]";
                user = service.getUserById(transaction.getReceiverId());
            }

            Split split = service.getSplitById(transaction.getSplitId());
            Relation relation = service.getRelationById(split.getRelationId());
            String preFix = relation.getType().equals("friend") ?
                "* " : String.format("* (%s) ", relation.getName());

            statusStr.append(String.format(messageFormat,
                    preFix, user.getFirstName(),
                    user.getLastName(), user.getUsername(), transaction.getAmount(),
                    currentClient.getCurrencyPreference(), split.getReason()))
                .append(System.lineSeparator());
        }

        return statusStr.toString();
    }

    private String getHistory(String clientId) {
        if (!sessions.containsKey(clientId)) {
            return UNKNOWN_COMMAND;
        }

        User currentClient = service.getUserById(sessions.get(clientId));
        Set<Transaction> payedTransactions = service.getUserPayedTransactions(currentClient.getId());
        if (payedTransactions.isEmpty()) {
            return "You do not have any payed transactions yet.";
        }

        StringBuilder historyStr = new StringBuilder();
        for (Transaction transaction : payedTransactions) {
            String messageFormat;
            User user;
            if (transaction.getReceiverId().equals(currentClient.getId())) {
                messageFormat = "%s %s %s (%s): Owes you %f %s [%s]";
                user = service.getUserById(transaction.getSenderId());
            } else {
                messageFormat = "%s %s %s (%s): You owe %f %s [%s]";
                user = service.getUserById(transaction.getReceiverId());
            }

            Split split = service.getSplitById(transaction.getSplitId());
            Relation relation = service.getRelationById(split.getRelationId());
            String preFix = relation.getType().equals("friend") ?
                "* " : String.format("* (%s) ", relation.getName());

            historyStr.append(String.format(messageFormat,
                    preFix, user.getFirstName(),
                    user.getLastName(), user.getUsername(), transaction.getAmount(),
                    currentClient.getCurrencyPreference(), split.getReason()))
                .append(System.lineSeparator());
        }

        return historyStr.toString();
    }

    private String payed(String clientId, String[] args) {
        if (!sessions.containsKey(clientId)) {
            return UNKNOWN_COMMAND;
        }

        if (args.length != REQUIRE_TWO_ARGUMENTS) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, CommandType.PAYED, REQUIRE_TWO_ARGUMENTS,
                CommandType.PAYED + " <amount> <username>");
        }

        Double amount = Double.parseDouble(args[0]);
        String friendUsername = args[1];

        if (amount < 0) {
            return "Cannot be payed negative amount";
        }
        User friendUser, currentClient;
        try {
            friendUser = getValidUser(friendUsername);
            currentClient = getValidUser(sessions.get(clientId));
        } catch (InvalidUser e) {
            return e.getMessage();
        }

        Double leftToPay = service.payTransaction(sessions.get(clientId), amount, friendUser.getId());

        return String.format("%s %s (%s) payed you %f %s.",
            friendUser.getFirstName(), friendUser.getLastName(),
            friendUser.getUsername(), amount, currentClient.getCurrencyPreference()) +
            System.lineSeparator() +
            String.format("Current status: Owes you %f %s", leftToPay, currentClient.getCurrencyPreference()) +
            System.lineSeparator();
    }

    private String switchCurrency(String clientId, String[] args) {
        return "";
    }

    private String help(String clientId) {
        if (sessions.containsKey(clientId)) {
            return """
            $ add-friend   <username>
           
            $ create-group <group_name> <username> <username> ... <username>
           
            $ payed        <amount> <username>
            $ split        <amount> <username>   <reason_for_payment>
            $ split-group  <amount> <group_id>   <reason_for_payment>
           
            $ change-currency <currency_type>
          
            $ history
            $ history <username/group> !!!!!
          
            $ help
            $ get-status
            $ logout
           """;
        } else {
            return """
            $ login        <username> <password>
            $ register     <username> <password> <first_name> <last_name>
            
            $ help
            """;
        }
    }

    private boolean validateString(String str, boolean isPersonalData) {
        return isPersonalData ? str.matches(PERSONAL_NAME_FORMAT) : str.matches(USERNAME_AND_PASSWORD_FORMAT);
    }

    private User getValidUser(String username) {
        User user = service.getUserByUsername(username);

        if (user == null) {
            throw new InvalidUser(Utils.INVALID_USER_MESSAGE);
        }

        return user;
    }

    private User getValidUser(UUID userId) {
        User user = service.getUserById(userId);

        if (user == null) {
            throw new InvalidUser(Utils.INVALID_USER_MESSAGE);
        }

        return user;
    }
}
