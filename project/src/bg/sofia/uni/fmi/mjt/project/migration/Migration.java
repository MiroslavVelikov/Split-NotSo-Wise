package bg.sofia.uni.fmi.mjt.project.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Migration {
    private static final String CREATE_RELATION_TABLE = """
        CREATE TABLE IF NOT EXISTS relations (
            id UUID PRIMARY KEY,
            type TEXT NOT NULL CHECK(type IN ('friend', 'group')),
            name VARCHAR(32)
        );
        """;
    private static final String CREATE_CURRENCY_TYPE = """
        DO $$
        BEGIN
            IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'currency_type') THEN
                CREATE TYPE currency_type AS ENUM ('BGN', 'EUR', 'USD', 'GBP', 'JPY', 'CAD', 'CHF', 'TRY');
            END IF;
        END
        $$;
        """;
    private static final String CREATE_USER_TABLE = """
        CREATE TABLE IF NOT EXISTS users (
            id UUID PRIMARY KEY,
            username VARCHAR(32) NOT NULL UNIQUE,
            password VARCHAR(64) NOT NULL,
            first_name VARCHAR(32) NOT NULL,
            last_name VARCHAR(32) NOT NULL,
            currency currency_type NOT NULL
        );
        """;
    private static final String CREATE_RELATION_MEMBER_TABLE = """
        CREATE TABLE IF NOT EXISTS relation_members (
            relation_id UUID REFERENCES relations(id) ON DELETE CASCADE,
            user_id UUID REFERENCES users(id) ON DELETE CASCADE,
            PRIMARY KEY (relation_id, user_id)
        );
        """;
    private static final String CREATE_SPLIT_TABLE = """
        CREATE TABLE IF NOT EXISTS splits (
            id UUID PRIMARY KEY,
            relation_id UUID REFERENCES relations(id) ON DELETE CASCADE,
            amount NUMERIC(10, 2) NOT NULL,
            reason TEXT
        );
        """;
    private static final String CREATE_TRANSACTION_TABLE = """
        CREATE TABLE IF NOT EXISTS transactions (
            id UUID PRIMARY KEY,
            split_id UUID REFERENCES splits(id) ON DELETE CASCADE,
            sender_id UUID REFERENCES users(id) ON DELETE CASCADE,
            receiver_id UUID REFERENCES users(id) ON DELETE CASCADE,
            amount NUMERIC(10, 2) NOT NULL CHECK (amount > 0),
            paid BOOLEAN NOT NULL DEFAULT FALSE
        );
        """;
    private static final String CREATE_NOTIFICATION_TABLE = """
        CREATE TABLE IF NOT EXISTS notifications (
            id UUID PRIMARY KEY,
            transaction_id UUID REFERENCES transactions(id) ON DELETE CASCADE,
            receiver_id UUID REFERENCES users(id) ON DELETE CASCADE
        );
        """;

    private static final String DROP_USER_TABLE = """
        DROP TABLE IF EXISTS users;
        """;
    private static final String DROP_RELATION_TABLE = """
        DROP TABLE IF EXISTS relations;
        """;
    private static final String DROP_RELATION_MEMBER_TABLE = """
        DROP TABLE IF EXISTS relation_members;
        """;
    private static final String DROP_SPLIT_TABLE = """
        DROP TABLE IF EXISTS splits;
        """;
    private static final String DROP_TRANSACTION_TABLE = """
        DROP TABLE IF EXISTS transactions;
        """;
    private static final String DROP_NOTIFICATION_TABLE = """
        DROP TABLE IF EXISTS notifications;
        """;
    private static final String DROP_CURRENCY_TYPE = """
        DROP TYPE IF EXISTS currency_type;
        """;

    public static void up(Connection conn) throws SQLException {
        conn.setAutoCommit(false);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_CURRENCY_TYPE);
            stmt.execute(CREATE_USER_TABLE);
            stmt.execute(CREATE_RELATION_TABLE);
            stmt.execute(CREATE_RELATION_MEMBER_TABLE);
            stmt.execute(CREATE_SPLIT_TABLE);
            stmt.execute(CREATE_TRANSACTION_TABLE);
            stmt.execute(CREATE_NOTIFICATION_TABLE);

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public static void down(Connection conn) throws SQLException {
        conn.setAutoCommit(false);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(DROP_NOTIFICATION_TABLE);
            stmt.execute(DROP_TRANSACTION_TABLE);
            stmt.execute(DROP_SPLIT_TABLE);
            stmt.execute(DROP_RELATION_MEMBER_TABLE);
            stmt.execute(DROP_RELATION_TABLE);
            stmt.execute(DROP_USER_TABLE);
            stmt.execute(DROP_CURRENCY_TYPE);

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
}
