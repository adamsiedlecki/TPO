package zad1.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
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
    private StringBuffer reqString = new StringBuffer();

    public ServerClientHandler(SocketChannel clientSocketChannel, SelectionKey key) {
        this.clientSocketChannel = clientSocketChannel;
        this.key = key;
        buffer = ByteBuffer.allocate(1024);
    }

    public void readMessage() {
        ServerLogger.log("czytam");
        buffer.clear();

        try {
            List<String> messages = new ArrayList<>();
            readLoop:                    // Czytanie jest nieblokujące
            while (true) {               // kontynujemy je dopóki
                int n = clientSocketChannel.read(buffer);   // nie natrafimy na koniec wiersza
                if (n == -1) {
                    return;
                }
                if (n == 0 && reqString.length() == 0) {
                    break readLoop;
                }
                if (n > 0) {
                    buffer.flip();
                    CharBuffer cbuf = charset.decode(buffer);
                    while(cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        //System.out.println(c);
                        if (c == '\r' || c == '\n')  {
                            messages.add(reqString.toString()); // serwer moze doświiadczyć wielu wiadomości naraz
                            reqString.setLength(0);
                            if (!cbuf.hasRemaining()) {
                                break readLoop;
                            }
                        }
                        else {
                            //System.out.println(c);
                            reqString.append(c);
                        }
                    }
                }
            }
            if (!messages.isEmpty()) {
                buffer.clear();
                for (String message: messages) {
                    ServerLogger.log("otrzymalem wiadomosc: "+ message);
                    processMessage(message, clientSocketChannel);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
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
            ServerLogger.log("Stan bazy newsOnTopic: " + newsOnTopic);
            ServerLogger.log("Stan bazy userTopics: " + userTopics);
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
            if (!client.isConnected()) {
                continue;
            }
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
            newsBuilder.append("\n");
            String news = newsBuilder.toString();
            ServerLogger.log("Wysylam do klienta newsy: " + news);
            clientSocketChannel.write(charset.encode(news));
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
            }
            topicsBuilder.append("\n");
            clientSocketChannel.write(charset.encode(topicsBuilder.toString()));
            ServerLogger.log("Wysylam topics do usera: "+ topicsBuilder.toString());
        } catch (IOException e) {
            ServerLogger.log("Wydarzyl sie blad podczas wysylania topics do klienta: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
