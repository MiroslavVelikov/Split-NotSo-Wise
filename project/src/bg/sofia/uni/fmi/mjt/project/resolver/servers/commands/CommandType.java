package bg.sofia.uni.fmi.mjt.project.resolver.servers.commands;

import java.util.Arrays;

public enum CommandType {
    LOGIN("login"),
    REGISTER("register"),
    LOGOUT("logout"),
    QUIT("quit"),
    ADD_FRIEND("add-friend"),
    CREATE_GROUP("create-group"),
    SPLIT("split"),
    SPLIT_GROUP("split-group"),
    GET_STATUS("get-status"),
    GET_HISTORY(("get-history")),
    PAYED("payed"),
    SWITCH_CURRENCY("switch-currency"),
    HELP("help");

    private final String command;

    CommandType(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static CommandType fromString(String input) {
        return Arrays.stream(CommandType.values())
            .filter(c -> c.getCommand().equalsIgnoreCase(input.trim()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + input));
    }
}
