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
    public static final Map<SocketChannel, ServerClientHandler> clientHandlers = new HashMap<>();

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
        for (SocketChannel socketChannel : disconnected) {
            clientHandlers.remove(socketChannel);
            ServerLogger.log("Usuwam rozlaczonego klienta: " + socketChannel.getLocalAddress());
        }
    }

    private void acceptClient() throws IOException {
        SocketChannel clientSocketChannel = serverSocketChannel.accept();
        clientSocketChannel.configureBlocking(false);
        SelectionKey key = clientSocketChannel.register(selector, SelectionKey.OP_READ);
        ServerClientHandler serverClientHandler = new ServerClientHandler(clientSocketChannel, key);
        clientHandlers.put(clientSocketChannel, serverClientHandler);
        ServerLogger.log("zaakceptowano nowego klienta. Aktualna liczba klientow: " + clientHandlers.size());
        serverClientHandler.sendTopicsToUsers(); // nadmiarowo do wszystkich a nie tylko do nowego - nie chce mi się kompliokować kodu
    }

    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ServerClientHandler serverClientHandler = clientHandlers.get(clientSocketChannel);
        try {
            serverClientHandler.readMessage();
        } catch (Exception e) {
            ServerLogger.log("Wydarzyl sie blad podczas odczytu od klienta" + e.getMessage());
            clientHandlers.remove(clientSocketChannel);
            clientSocketChannel.close();
        }
    }

}
