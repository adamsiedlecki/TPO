package zad1.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.stream.Collectors;


public class ServerMain {


    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    public static final Map<SocketChannel, ClientHandler> clientHandlers = new HashMap<>();

    public static void main(String[] args) {
        ServerMain server = new ServerMain();
        server.start();
    }

    public ServerMain() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress("localhost", 8080));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server started...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                for (SelectionKey key : selectedKeys) {
                    if (key.isAcceptable()) {
                        acceptClient();
                    } else if (key.isReadable()) {
                        readMessage(key);
                    }
                }
                selectedKeys.clear();
                usunRozlaczonych();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void usunRozlaczonych() throws IOException {
        List<SocketChannel> disconnected = clientHandlers.keySet().stream().filter(s -> !s.isConnected()).collect(Collectors.toList());
        for (int i = 0; i < disconnected.size(); i++) {
            clientHandlers.remove(disconnected.get(i));
            ServerLogger.log("Usuwam rozlaczonego klienta: " + disconnected.get(i).getLocalAddress());
        }
    }

    private void acceptClient() throws IOException {
        SocketChannel clientSocketChannel = serverSocketChannel.accept();
        clientSocketChannel.configureBlocking(false);
        SelectionKey key = clientSocketChannel.register(selector, SelectionKey.OP_READ);
        ClientHandler clientHandler = new ClientHandler(clientSocketChannel, key);
        clientHandlers.put(clientSocketChannel, clientHandler);
        ServerLogger.log("zaakceptowano nowego klienta. Aktualna liczba klientow: " + clientHandlers.size());
    }

    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ClientHandler clientHandler = clientHandlers.get(clientSocketChannel);
        clientHandler.readMessage();
    }

}
