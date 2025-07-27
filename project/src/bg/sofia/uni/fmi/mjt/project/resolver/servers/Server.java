package bg.sofia.uni.fmi.mjt.project.resolver.servers;

import bg.sofia.uni.fmi.mjt.project.resolver.servers.commands.CommandCreator;
import bg.sofia.uni.fmi.mjt.project.resolver.servers.commands.CommandExecutor;
import bg.sofia.uni.fmi.mjt.project.utils.HashUtils;
import bg.sofia.uni.fmi.mjt.project.utils.Utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

public class Server {
    private static final int BUFFER_SIZE = 4096;
    private static final String NEW_LINE_FORMAT = "(\\r\\n|\\n)$";

    private final CommandExecutor commandExecutor;
    private final int port;

    private boolean isServerWorking;
    private ByteBuffer buffer;
    private Selector selector;

    public Server(int port, CommandExecutor commandExecutor) {
        this.port = port;
        this.commandExecutor = commandExecutor;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking = true;

            startShutdownListener();

            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            String clientInput = trimNewlines(getClientInput(clientChannel));

                            if (clientInput == null) {
                                continue;
                            }

                            String clientId = HashUtils.sha256(clientChannel.getRemoteAddress().toString());
                            String output = commandExecutor.execute(CommandCreator
                                .newCommand(clientInput, clientId));

                            writeClientOutput(clientChannel, output);
                        } else if (key.isAcceptable()) {
                            accept(selector, key);
                        }

                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    System.out.println("Error occurred while processing client request: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error occurred while starting the server", e);
        }
    }

    private void startShutdownListener() {
        Thread shutdownListener = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (isServerWorking) {
                    String line = scanner.nextLine();
                    if ("stop".equalsIgnoreCase(line.trim())) {
                        stop();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in shutdown listener: " + e.getMessage());
            }
        });
        shutdownListener.setDaemon(true);
        shutdownListener.start();
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(Utils.HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put((validOutput(output))
            .getBytes(StandardCharsets.UTF_8));
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private static String validOutput(String input) {
        if (input.matches(NEW_LINE_FORMAT)) {
            return input + Utils.END_RESPONSE_MARKER;
        } else {
            return input + System.lineSeparator() + Utils.END_RESPONSE_MARKER;
        }
    }

    private static String trimNewlines(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("(\r\n|\r|\n)+$", "");
    }
}
