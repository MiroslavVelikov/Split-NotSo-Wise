package bg.sofia.uni.fmi.mjt.project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static bg.sofia.uni.fmi.mjt.project.common.Utils.QUIT_COMMAND;

public class ClientServer {

    private static final int SERVER_PORT = 4444;
    private static final String HOST = "localhost";

    static void main(String[] args) {

        try (Socket socket = new Socket(HOST, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            Thread.currentThread().setName("Echo client thread " + socket.getLocalPort());

            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();

                if (QUIT_COMMAND.equals(message)) {
                    break;
                }

                System.out.println("Sending message <" + message + "> to the server...");

                writer.println(message); // send the message to the server

                String reply = reader.readLine(); // read the response from the server
                System.out.println("The server replied <" + reply + ">");
            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

}
