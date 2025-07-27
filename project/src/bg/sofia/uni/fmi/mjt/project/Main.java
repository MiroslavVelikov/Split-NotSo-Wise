package bg.sofia.uni.fmi.mjt.project;

import bg.sofia.uni.fmi.mjt.project.config.Database;
import bg.sofia.uni.fmi.mjt.project.converters.DatabaseConverter;
import bg.sofia.uni.fmi.mjt.project.migration.Migration;
import bg.sofia.uni.fmi.mjt.project.repositories.DatabaseStorage;
import bg.sofia.uni.fmi.mjt.project.repositories.Storage;
import bg.sofia.uni.fmi.mjt.project.resolver.servers.Server;
import bg.sofia.uni.fmi.mjt.project.resolver.servers.commands.CommandExecutor;
import bg.sofia.uni.fmi.mjt.project.service.Service;
import bg.sofia.uni.fmi.mjt.project.utils.Utils;

import java.sql.Connection;

public class Main {
    private static final String DATABASE_NAME = "splitNotSoWise";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        Database db = new Database();

        try (Connection conn = db.connectToDB(DATABASE_NAME, USERNAME, PASSWORD)) {
            Migration.down(conn);
            Migration.up(conn);

            DatabaseConverter dbConverter = new DatabaseConverter();
            Storage storage = new DatabaseStorage(conn, dbConverter);

            Service service = new Service(storage);

            CommandExecutor executor = new CommandExecutor(service);
            Server server = new Server(Utils.SERVER_PORT, executor);

            server.start();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
    }
}