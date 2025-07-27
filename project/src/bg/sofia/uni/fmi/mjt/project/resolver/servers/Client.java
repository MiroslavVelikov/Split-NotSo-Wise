package bg.sofia.uni.fmi.mjt.project.resolver.servers;

import bg.sofia.uni.fmi.mjt.project.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(Utils.HOST, Utils.SERVER_PORT));

            System.out.println("Connected to the server.");
            while (true) {
                System.out.print("> ");
                String message = scanner.nextLine();

                if ("quit".equals(message)) {
                    break;
                }

                writer.println(message);

                String reply;
                while ((reply = reader.readLine()) != null) {
                    if ("END".equals(reply)) {
                        break;
                    }
                    System.out.println(reply);
                }
            }

            writer.println("logout");
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}
