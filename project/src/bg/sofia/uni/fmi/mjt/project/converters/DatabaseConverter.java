package bg.sofia.uni.fmi.mjt.project.converters;

import bg.sofia.uni.fmi.mjt.project.models.database.Notification;
import bg.sofia.uni.fmi.mjt.project.models.database.relation.Relation;
import bg.sofia.uni.fmi.mjt.project.models.database.relation.RelationMember;
import bg.sofia.uni.fmi.mjt.project.models.database.transaction.Split;
import bg.sofia.uni.fmi.mjt.project.models.database.transaction.Transaction;
import bg.sofia.uni.fmi.mjt.project.models.database.user.Currency;
import bg.sofia.uni.fmi.mjt.project.models.database.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseConverter {
    private static final String TABLE_ID_PROPERTY = "id";
    private static final String TABLE_RELATION_ID_PROPERTY = "relation_id";
    private static final String TABLE_AMOUNT_PROPERTY = "amount";

    private static final String TABLE_USER_USERNAME = "username";
    private static final String TABLE_USER_PASSWORD = "password";
    private static final String TABLE_USER_FIRST_NAME = "first_name";
    private static final String TABLE_USER_LAST_NAME = "last_name";
    private static final String TABLE_USER_CURRENCY = "currency";

    private static final String TABLE_RELATION_TYPE = "type";
    private static final String TABLE_RELATION_NAME = "name";

    private static final String TABLE_RELATION_MEMBER_USER_ID = "user_id";

    private static final String TABLE_SPLIT_REASON = "reason";

    private static final String TABLE_TRANSACTION_SPLIT_ID = "split_id";
    private static final String TABLE_TRANSACTION_SENDER_ID = "sender_id";
    private static final String TABLE_TRANSACTION_RECEIVER_ID = "receiver_id";
    private static final String TABLE_TRANSACTION_IS_PAID = "paid";

    private static final String TABLE_NOTIFICATION_TRANSACTION_ID = "transaction_id";
    private static final String TABLE_NOTIFICATION_RECEIVER_ID = "receiver_id";

    public User convertResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User(
            UUID.fromString(resultSet.getString(TABLE_ID_PROPERTY)),
            resultSet.getString(TABLE_USER_USERNAME),
            resultSet.getString(TABLE_USER_PASSWORD),
            resultSet.getString(TABLE_USER_FIRST_NAME),
            resultSet.getString(TABLE_USER_LAST_NAME),
            Currency.valueOf(resultSet.getString(TABLE_USER_CURRENCY))
        );
    }

    public Relation convertResultSetToRelation(ResultSet resultSet) throws SQLException {
        return new Relation(
            UUID.fromString(resultSet.getString(TABLE_ID_PROPERTY)),
            resultSet.getString((TABLE_RELATION_TYPE)),
            resultSet.getString((TABLE_RELATION_NAME))
        );
    }

    public RelationMember convertResultSetToRelationMember(ResultSet resultSet) throws SQLException {
        return new RelationMember(
            UUID.fromString(resultSet.getString(TABLE_RELATION_ID_PROPERTY)),
            UUID.fromString(resultSet.getString(TABLE_RELATION_MEMBER_USER_ID))
        );
    }

    public Split convertResultSetToSplit(ResultSet resultSet) throws SQLException {
        return new Split(
            UUID.fromString(resultSet.getString(TABLE_ID_PROPERTY)),
            resultSet.getDouble(TABLE_AMOUNT_PROPERTY),
            resultSet.getString(TABLE_SPLIT_REASON),
            UUID.fromString(resultSet.getString(TABLE_RELATION_ID_PROPERTY))
        );
    }

    public Transaction convertResultSetToTransaction(ResultSet resultSet) throws SQLException {
        return new Transaction(
            UUID.fromString(resultSet.getString(TABLE_ID_PROPERTY)),
            UUID.fromString(resultSet.getString(TABLE_TRANSACTION_SPLIT_ID)),
            resultSet.getDouble(TABLE_AMOUNT_PROPERTY),
            UUID.fromString(resultSet.getString(TABLE_TRANSACTION_SENDER_ID)),
            UUID.fromString(resultSet.getString(TABLE_TRANSACTION_RECEIVER_ID)),
            resultSet.getBoolean(TABLE_TRANSACTION_IS_PAID)
        );
    }

    public Notification convertResultSetToNotification(ResultSet resultSet) throws SQLException {
        return new Notification(
            UUID.fromString(resultSet.getString(TABLE_ID_PROPERTY)),
            UUID.fromString(resultSet.getString(TABLE_NOTIFICATION_TRANSACTION_ID)),
            UUID.fromString(resultSet.getString(TABLE_NOTIFICATION_RECEIVER_ID))
        );
    }
}
