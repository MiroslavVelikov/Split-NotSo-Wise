package bg.sofia.uni.fmi.mjt.project.repositories;

import bg.sofia.uni.fmi.mjt.project.converters.DatabaseConverter;
import bg.sofia.uni.fmi.mjt.project.models.database.Notification;
import bg.sofia.uni.fmi.mjt.project.models.database.relation.Relation;
import bg.sofia.uni.fmi.mjt.project.models.database.relation.RelationMember;
import bg.sofia.uni.fmi.mjt.project.models.database.transaction.Split;
import bg.sofia.uni.fmi.mjt.project.models.database.transaction.Transaction;
import bg.sofia.uni.fmi.mjt.project.models.database.user.Currency;
import bg.sofia.uni.fmi.mjt.project.models.database.user.User;
import bg.sofia.uni.fmi.mjt.project.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DatabaseStorage implements Storage {
    private final String ROLLBACK_FAILED = "Rollback failed: ";

    private final Connection connection;
    private final DatabaseConverter converter;

    public DatabaseStorage(Connection conn, DatabaseConverter converter) {
        this.connection = conn;
        this.converter = converter;
    }

    @Override
    public void createUser(User newUser) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.CREATE_USER)) {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }

            bindParams(stmt,
                newUser.getId(),
                newUser.getUsername(),
                newUser.getPassword(),
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getCurrencyPreference().getCurrency()
            );

            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println(ROLLBACK_FAILED + rollbackEx.getMessage());
            }
            String message = "Fail creating new user with credentials: " + newUser.toString();

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public User getUserById(UUID userId) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.GET_USER_BY_ID)) {
            bindParams(stmt, userId);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return converter.convertResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            String message = "Fail getting user with this id: " + userId;

            System.out.println(message);
            throw new RuntimeException(message);
        }

        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.GET_USER_BY_USERNAME)) {
             bindParams(stmt, username);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return converter.convertResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            String message = "Fail getting user with this username: " + username;

            System.out.println(message);
            throw new RuntimeException(message);
        }

        return null;
    }

    @Override
    public void updateUserCurrencyType(User user, Currency currency) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.UPDATE_USER_CURRENCY_PREFERENCE)) {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }

            bindParams(stmt,
                user.getId(),
                currency.getCurrency()
            );

            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println(ROLLBACK_FAILED + rollbackEx.getMessage());
            }
            String message = "Fail updating user with this id: " + user.getId();

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public void createRelation(Relation newRelation) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.CREATE_RELATION)) {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }

            bindParams(stmt,
                newRelation.getId(),
                newRelation.getType(),
                newRelation.getName()
            );

            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println(ROLLBACK_FAILED + rollbackEx.getMessage());
            }
            String message = "Fail creating new relation";

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Relation getRelationById(UUID relationId) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.GET_RELATION_BY_ID)) {
            bindParams(stmt, relationId);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return converter.convertResultSetToRelation(resultSet);
            }
        } catch (SQLException e) {
            String message = "Fail getting relation with this id: " + relationId;

            System.out.println(message);
            throw new RuntimeException(message);
        }

        return null;
    }

    @Override
    public void addUserToRelation(RelationMember newMember) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.CREATE_RELATION_MEMBER)) {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }

            bindParams(stmt,
                newMember.getRelationId(),
                newMember.getMemberId()
            );

            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println(ROLLBACK_FAILED + rollbackEx.getMessage());
            }
            String message = "Fail creating new member relation";

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Set<Relation> getRelationsContainingUser(UUID userId) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.GET_RELATIONS_OF_USER)) {
            bindParams(stmt, userId);

            Set<Relation> relations = new HashSet<>();
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                relations.add(converter.convertResultSetToRelation(resultSet));
            }

            return relations;
        } catch (SQLException e) {
            String message = "Fail getting relations of this user with id: " + userId;

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Set<RelationMember> getMembersOfRelationById(UUID relationId) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.GET_RELATION_MEMBERS)) {
            bindParams(stmt, relationId);

            Set<RelationMember> relations = new HashSet<>();
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                relations.add(converter.convertResultSetToRelationMember(resultSet));
            }

            return relations;
        } catch (SQLException e) {
            String message = "Fail getting member relations with this relation id: " + relationId;

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public void createSplit(Split newSplit) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.CREATE_SPLIT)) {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }

            bindParams(stmt,
                newSplit.getId(),
                newSplit.getRelationId(),
                newSplit.getAmount(),
                newSplit.getRelationId()
            );

            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println(ROLLBACK_FAILED + rollbackEx.getMessage());
            }
            String message = "Fail creating new split";

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Split getSplitById(UUID splitId) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.GET_SPLIT_BY_ID)) {
            bindParams(stmt, splitId);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return converter.convertResultSetToSplit(resultSet);
            }
        } catch (SQLException e) {
            String message = "Fail getting split with this id: " + splitId;

            System.out.println(message);
            throw new RuntimeException(message);
        }

        return null;
    }

    @Override
    public void createTransaction(Transaction newTransaction) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.CREATE_TRANSACTION)) {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }

            bindParams(stmt,
                newTransaction.getId(),
                newTransaction.getSplitId(),
                newTransaction.getSenderId(),
                newTransaction.getReceiverId(),
                newTransaction.getAmount(),
                newTransaction.isPaid()
            );

            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println(ROLLBACK_FAILED + rollbackEx.getMessage());
            }
            String message = "Fail creating new transaction";

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Transaction getTransactionById(UUID transactionId) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.GET_TRANSACTION_BY_ID)) {
            bindParams(stmt, transactionId);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return converter.convertResultSetToTransaction(resultSet);
            }
        } catch (SQLException e) {
            String message = "Fail getting transaction with this id: " + transactionId;

            System.out.println(message);
            throw new RuntimeException(message);
        }

        return null;
    }

    @Override
    public Set<Transaction> getTransactionsByUser(UUID userId) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.GET_TRANSACTIONS_BY_USER)) {
            bindParams(stmt, userId);

            Set<Transaction> relations = new HashSet<>();
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                relations.add(converter.convertResultSetToTransaction(resultSet));
            }

            return relations;
        } catch (SQLException e) {
            String message = "Fail getting transactions with this user: " + userId;

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public void updateTransaction(Transaction newTransaction) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.UPDATE_TRANSACTION)) {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }

            bindParams(stmt,
                newTransaction.getAmount(),
                newTransaction.isPaid(),
                newTransaction.getId()
            );

            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println(ROLLBACK_FAILED + rollbackEx.getMessage());
            }
            String message = "Fail updating transaction with this id: " + newTransaction.getId();

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public void createNotification(Notification newNotification) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.CREATE_NOTIFICATION)) {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }

            bindParams(stmt,
                newNotification.getId(),
                newNotification.getTransactionId(),
                newNotification.getUserId()
            );

            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println(ROLLBACK_FAILED + rollbackEx.getMessage());
            }
            String message = "Fail creating new notification";

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Set<Notification> getAllNotificationByUserId(UUID userId) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.GET_NOTIFICATIONS_BY_USER_ID)) {
            bindParams(stmt, userId);

            Set<Notification> relations = new HashSet<>();
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                relations.add(converter.convertResultSetToNotification(resultSet));
            }

            return relations;
        } catch (SQLException e) {
            String message = "Fail getting notifications for user with this id: " + userId;

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public void deleteNotification(UUID notificationsId) {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseUtils.DELETE_NOTIFICATION_BY_ID)) {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }

            bindParams(stmt, notificationsId);

            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println(ROLLBACK_FAILED + rollbackEx.getMessage());
            }
            String message = "Fail deleting notification with id: " + notificationsId;

            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    private static void bindParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}
