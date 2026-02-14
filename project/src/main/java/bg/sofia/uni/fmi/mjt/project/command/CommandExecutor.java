package bg.sofia.uni.fmi.mjt.project.command;

import bg.sofia.uni.fmi.mjt.project.user.UserResolver;
import bg.sofia.uni.fmi.mjt.project.user.models.UserOutput;

public class CommandExecutor {
    private static final int REQUIRES_TWO_ARGUMENTS = 2;
    private static final int REQUIRES_THREE_ARGUMENTS = 3;
    private static final int REQUIRES_FOUR_ARGUMENTS = 4;

    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
        "Invalid count of arguments: \"%s\" expects %d arguments. Example: \"%s\"";

    private static final String LOGIN = "login";
    private static final String REGISTER = "register";
    private static final String ADD = "add-todo";
    private static final String COMPLETE = "complete-todo";
    private static final String LIST = "list";

    private UserResolver userResolver;

    public CommandExecutor(UserResolver userResolver) {
        this.userResolver = userResolver;
    }

    public String execute(Command cmd) {
        return switch (cmd.command()) {
            case LOGIN -> loginUser((cmd.arguments()));
            case REGISTER -> registerUser((cmd.arguments()));
            case ADD -> addToDo(cmd.arguments());
            case COMPLETE -> complete(cmd.arguments());
            case LIST -> list(cmd.arguments());
            default -> "Unknown command";
        };
    }

    private String loginUser(String[] args) {
        if (args.length != REQUIRES_TWO_ARGUMENTS) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, LOGIN, REQUIRES_TWO_ARGUMENTS, LOGIN + " <username> <password> <first_name> <last_name>");
        }

        String username = args[0];
        String password = args[1];

        UserOutput user = userResolver.loginUser(username, password);
        return user == null ? "Something went wrong logging a into user." : user.toString();
    }

    private String registerUser(String[] args) {
        if (args.length != REQUIRES_FOUR_ARGUMENTS) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, REGISTER, REQUIRES_FOUR_ARGUMENTS, REGISTER + " <username> <password> <first_name> <last_name>");
        }

        String username = args[0];
        String password = args[1];
        String firstName = args[2];
        String lastName = args[3];

        UserOutput user = userResolver.registerUser(username, password, firstName, lastName);
        return user == null ? "Something went wrong registering a new user." : user.toString();
    }

    private String addToDo(String[] args) {
//        if (args.length != 2) {
//            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, ADD, 2, ADD + " <username> <todo_item>");
//        }
//
//        String user = args[0];
//        String todo = args[1];
//
//        int todoID = storage.add(user, todo);
//        return String.format("Added new To Do with ID %s for user %s", todoID, user);
        return null;
    }

    private String complete(String[] args) {
//        if (args.length != 2) {
//            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, COMPLETE, 2,
//                COMPLETE + " <username> <todo_item_id>");
//        }
//
//        String user = args[0];
//        int todoID;
//        try {
//            todoID = Integer.parseInt(args[1]);
//        } catch (NumberFormatException e) {
//            return "Invalid ID provided for command \"complete-todo\": only integer values are allowed";
//        }
//
//        storage.remove(user, todoID);
//        return String.format("Completed To Do with ID %s for user %s", todoID, user);
        return null;
    }

    private String list(String[] args) {
//        if (args.length != 1) {
//            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, LIST, 1, LIST + " <username>");
//        }
//        String user = args[0];
//        var todos = storage.list(user);
//        if (todos.isEmpty()) {
//            return "No To-Do items found for user with name " + user;
//        }
//
//        StringBuilder response = new StringBuilder(String.format("To-Do list of user %s:%n", user));
//        todos.forEach((k, v) -> response.append(String.format("[%d] %s%n", k, v)));
//
//        return response.toString();
        return null;
    }
}