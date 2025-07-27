package bg.sofia.uni.fmi.mjt.project.utils;

import javax.print.DocFlavor;

public class DatabaseUtils {
    public static final String CREATE_USER = """
        INSERT INTO users (id, username, password, first_name, last_name, currency) VALUES (?, ?, ?, ?, ?, ?::currency_type);
        """;
    public static final String GET_USER_BY_ID = """
        SELECT * FROM users WHERE id = ?;
        """;
    public static final String GET_USER_BY_USERNAME = """
        SELECT * FROM users WHERE username = ?;
        """;
    public static final String UPDATE_USER_CURRENCY_PREFERENCE = """
        UPDATE users SET currency = ? WHERE id = ?;
        """;

    public static final String CREATE_RELATION = """
        INSERT INTO relations (id, type, name) VALUES (?, ?, ?);
        """;
    public static final String GET_RELATION_BY_ID = """
        SELECT * FROM relations WHERE id = ?;
        """;
    public static final String CREATE_RELATION_MEMBER = """
        INSERT INTO relation_members (relation_id, user_id) VALUES (?, ?);
        """;
    public static final String GET_RELATIONS_OF_USER = """
        SELECT r.id, r.type, r.name
        FROM relations AS r
        JOIN relation_members AS rm ON r.id = rm.relation_id
        WHERE rm.user_id = ?;
        """;
    public static final String GET_RELATION_MEMBERS = """
        SELECT * FROM relation_members WHERE relation_id = ?;
        """;

    public static final String CREATE_SPLIT = """
        INSERT INTO splits (id, relation_id, amount, reason) VALUES (?, ?, ?, ?);
        """;
    public static final String GET_SPLIT_BY_ID = """
        SELECT * FROM splits WHERE id = ?;
        """;

    public static final String CREATE_TRANSACTION = """
        INSERT INTO transactions (id, split_id, sender_id, receiver_id, amount, paid)
            VALUES (?, ?, ?, ?, ?, ?);
        """;
    public static final String GET_TRANSACTION_BY_ID = """
        SELECT * FROM transactions WHERE id = ?;
        """;
    public static final String GET_TRANSACTIONS_BY_USER = """
        SELECT * FROM transactions WHERE sender_id = ? || receiver_id = ?;
        """;
    public static final String UPDATE_TRANSACTION = """
        UPDATE transactions SET amount = ? && paid = ? WHERE id = ?;
        """;

    public static final String CREATE_NOTIFICATION = """
        INSERT INTO notifications (id, transaction_id, receiver_id) VALUES(?, ?, ?);
        """;
    public static final String GET_NOTIFICATIONS_BY_USER_ID = """
        SELECT * FROM notifications WHERE receiver_id = ?;
        """;
    public static final String DELETE_NOTIFICATION_BY_ID = """
        DELETE FROM notifications WHERE receiver_id = ?;
        """;
}
