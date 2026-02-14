package bg.sofia.uni.fmi.mjt.project.server;

import bg.sofia.uni.fmi.mjt.project.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.project.common.Utils;
import bg.sofia.uni.fmi.mjt.project.user.UserResolver;
import bg.sofia.uni.fmi.mjt.project.user.UserResolverImpl;
import bg.sofia.uni.fmi.mjt.project.user.UserService;
import bg.sofia.uni.fmi.mjt.project.user.UserServiceImpl;
import bg.sofia.uni.fmi.mjt.project.user.UserStorage;
import bg.sofia.uni.fmi.mjt.project.user.UserStorageImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static bg.sofia.uni.fmi.mjt.project.common.Utils.QUIT_COMMAND;

public class Server {

    private static final int SERVER_PORT = 4444;
    private static final int MAX_EXECUTOR_THREADS = 10;

    private static final String SERVER_NAME = "NotSoWise Server";

    private static final UserStorage userStorage = new UserStorageImpl(Utils.DEFAULT_USER_REPO_PATH);
    private static final UserService userService = new UserServiceImpl(userStorage);
    private static final UserResolver userResolver = new UserResolverImpl(userService);

    public static void main(String[] args) {
        Thread.currentThread().setName(SERVER_NAME);

        startGracefulShutdownListener();

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
             ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);) {

            System.out.println("Server started and listening for connect requests");
            Socket clientSocket;

            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection request from client " + clientSocket.getInetAddress());

                ClientRequestHandler clientHandler = new ClientRequestHandler(clientSocket, new CommandExecutor(userResolver));
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }

    private static void startGracefulShutdownListener() {
        Thread consoleThread = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    String command = scanner.nextLine();
                    if (QUIT_COMMAND.equalsIgnoreCase(command.trim())) {
                        System.out.println("Shutting down server...");
                        System.exit(0);
                    }
                }
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

}
