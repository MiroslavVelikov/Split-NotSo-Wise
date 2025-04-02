package bg.sofia.uni.fmi.mjt.project.command;

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
    SWITCH_CURRENCY("switch-currency"),;

    private final String command;

    CommandType(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
