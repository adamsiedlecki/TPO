package zad1.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

public class ServerClientHandler {


    private static final Set<String> allTopics = new HashSet<>();
    private static final Map<String, List<String>> newsOnTopic = new HashMap<>();
    private static final Map<SocketChannel, Set<String>> userTopics = new HashMap<>();


    private final Charset charset  = Charset.forName("ISO-8859-2");

    private SocketChannel clientSocketChannel;
    private SelectionKey key;
    private ByteBuffer buffer;

    public ServerClientHandler(SocketChannel clientSocketChannel, SelectionKey key) {
        this.clientSocketChannel = clientSocketChannel;
        this.key = key;
        buffer = ByteBuffer.allocate(1024);
    }

    public void readMessage() {
        ServerLogger.log("czytam");

        try {
            int bytesRead = clientSocketChannel.read(buffer);
            if (bytesRead == -1) {
//                clientSocketChannel.close(); // chce trzymac polaczenia
//                key.cancel();
                return;
            }
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String message = new String(bytes).trim();
            ServerLogger.log("otrzymalem wiadomosc: "+ message);
            processMessage(message, clientSocketChannel);

            buffer.clear();
        } catch (IOException e) {
            ServerLogger.log(e.getMessage());
            //throw new RuntimeException(e);
        }

    }

    private void processMessage(String message, SocketChannel clientSocketChannel) {
        String[] split = message.split(",");
        String messageType = split[0];

        if (messageType.equals("subscribe")) {
            ServerLogger.log("Otrzymano zapytanie subscribe...");
            Set<String> userSelectedTopics = userTopics.get(clientSocketChannel);
            if (userSelectedTopics == null) {
                userSelectedTopics = new HashSet<>();
            }
            userSelectedTopics.clear();
            for (int i = 1; i <split.length ; i++) {
                userSelectedTopics.add(split[i]);
            }
            userTopics.put(clientSocketChannel, userSelectedTopics);
            sendNewsToUser(clientSocketChannel); // od razu mu sie aktualizuje
        } else if(messageType.equals("updateTopics")) {
            ServerLogger.log("Otrzymano zapytanie updateTopics...");
            allTopics.removeIf(x-> true);
            for (int i = 1; i < split.length; i++) {
                allTopics.add(split[i]);
            }
            userTopics.forEach(((socketChannel, strings) -> strings.removeIf(s -> !allTopics.contains(s))));

            List<Map.Entry<String, List<String>>> entriesNewsOnTopic = new ArrayList<>(newsOnTopic.entrySet());
            for (int i = 0; i < entriesNewsOnTopic.size(); i++) {
                String topic = entriesNewsOnTopic.get(i).getKey();
                if (!allTopics.contains(topic)) {
                    newsOnTopic.remove(topic);
                }
            }
            sendTopicsToUsers();
            sendNewsToUsers(); // od razu aktualizacja
        }else if(messageType.equals("newsOnTopic")) {
            ServerLogger.log("Otrzymano zapytanie newsOnTopic...");
            String topic = split[1];
            List<String> news = new ArrayList<>();
            for (int i = 2; i < split.length; i++) {
                news.add(split[i]);
            }
            newsOnTopic.put(topic, news);
            sendNewsToUsers(); // od razu aktualizacja
        } else {
            ServerLogger.log("Otrzymano inny rodzaj wiadomosci: " + messageType);
        }
    }

    private void sendNewsToUsers() {
        Set<SocketChannel> clients = ServerMain.clientHandlers.keySet();
        for(SocketChannel client: clients) {
            sendNewsToUser(client);
        }
    }

    public void sendTopicsToUsers() {
        Set<SocketChannel> clients = ServerMain.clientHandlers.keySet();
        for(SocketChannel client: clients) {
            sendTopicsToUser(client);
        }
    }

    private void sendNewsToUser(SocketChannel clientSocketChannel) {
        try {
            Set<String> topics = userTopics.get(clientSocketChannel);
            if (topics == null) {
                return;
            }
            StringBuilder newsBuilder = new StringBuilder();
            //news,sport;news1;news2;news3|gry;xd1;xd2;
            newsBuilder.append("news,");
            for(String topic:  topics) {
                List<String> news = newsOnTopic.get(topic);
                if (news == null || news.isEmpty()) {
                    newsBuilder.append(topic);
                    newsBuilder.append(";");
                    newsBuilder.append("Brak newsow dla tego topic'a");
                    newsBuilder.append("|");
                    continue;
                }
                newsBuilder.append(topic);
                newsBuilder.append(";");
                newsBuilder.append(String.join(";", news));
                newsBuilder.append("|");
            }
            clientSocketChannel.write(charset.encode(newsBuilder.toString()));
            buffer.clear();
        } catch (IOException e) {
            ServerLogger.log("Wydarzyl sie blad podczas wysylania news do klienta: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void sendTopicsToUser(SocketChannel clientSocketChannel) {
        try {
            StringBuilder topicsBuilder = new StringBuilder();
            topicsBuilder.append("topics,");
            for(String topic:  allTopics) {
                topicsBuilder.append(topic);
                topicsBuilder.append(",");
                clientSocketChannel.write(charset.encode(topicsBuilder.toString()));
                ServerLogger.log("Wysylam topics do usera: "+ topicsBuilder.toString());
            }
        } catch (IOException e) {
            ServerLogger.log("Wydarzyl sie blad podczas wysylania topics do klienta: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
