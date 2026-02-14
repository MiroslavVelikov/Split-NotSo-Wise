package bg.sofia.uni.fmi.mjt.project.server;

import bg.sofia.uni.fmi.mjt.project.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.project.command.CommandExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRequestHandler implements Runnable {

    private Socket socket;
    private final CommandExecutor commandExecutor;

    public ClientRequestHandler(Socket socket, CommandExecutor commandExecutor) {
        this.socket = socket;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("Client Request Handler for " + socket.getRemoteSocketAddress());

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String output = commandExecutor.execute(CommandCreator.newCommand(inputLine));

                System.out.println("Message received from client: " + inputLine);
                out.println("Answer: " + output);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}