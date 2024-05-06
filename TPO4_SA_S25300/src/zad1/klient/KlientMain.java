package zad1.klient;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

public class KlientMain {

    public static void main(String[] args) throws IOException {

        KlientLogger.log("Uruchamianie...");
        DataState dataState = new DataState();
        Thread guiThread = new Thread(() -> {
            Gui.start(args, dataState); // start GUI
        });
        guiThread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        SocketChannel channel = null;
        String server = "localhost";
        int port = 8080;

        try {
            // Utworzenie kanału
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(server, port));

            System.out.print("Klient: łączę się z serwerem ...");

            while (!channel.finishConnect()) {
                // ew. pokazywanie czasu łączenia (np. pasek postępu)
                // lub wykonywanie jakichś innych (krótkotrwałych) działań
            }

        } catch(UnknownHostException exc) {
            System.err.println("Uknown host " + server);
            // ...
        } catch(Exception exc) {
            exc.printStackTrace();
            // ...
        }

        KlientLogger.log("Polaczylem sie z serwerem");

        Charset charset  = Charset.forName("ISO-8859-2");

        // Alokowanie bufora bajtowego
        // allocateDirect pozwala na wykorzystanie mechanizmów sprzętowych
        // do przyspieszenia operacji we/wy
        // Uwaga: taki bufor powinien być alokowany jednokrotnie
        // i wielokrotnie wykorzystywany w operacjach we/wy
        int rozmiar_bufora = 1024;
        ByteBuffer inBuf = ByteBuffer.allocateDirect(rozmiar_bufora);
        CharBuffer cbuf = null;


        //System.out.println("Klient: wysyłam - Hi");
        // "Powitanie" do serwera


        // pętla czytania
        while (true) {

            if (dataState.clientWantsToUpdateTopics) {
                dataState.clientWantsToUpdateTopics = false;
                KlientLogger.log("Probuje sie zasubskrybowac na: " + dataState.userPickedTopics);
                channel.write(charset.encode("subscribe," + String.join(",", dataState.userPickedTopics)));
            }

            //cbuf = CharBuffer.wrap("coś" + "\n");

            inBuf.clear();    // opróżnienie bufora wejściowego
            int readBytes = channel.read(inBuf); // czytanie nieblokujące

            if (readBytes == 0) {                              // jeszcze nie ma danych
                continue;
            }
            else if (readBytes == -1) { // kanał zamknięty po stronie serwera
                // dalsze czytanie niemożlwe
                // ...
                KlientLogger.log("kanal zamknięty po stronie serwera");
                break;
            }
            else {        // dane dostępne w buforze
                //System.out.println("coś jest od serwera");

                inBuf.flip();    // przestawienie bufora

                // pobranie danych z bufora
                // ew. decyzje o tym czy mamy komplet danych - wtedy break
                // czy też mamy jeszcze coś do odebrania z serwera - kontynuacja
                cbuf = charset.decode(inBuf);

                String odSerwera = cbuf.toString();
                KlientLogger.log("Od serwera: " + odSerwera);
                String[] wiadomosciSerwera = odSerwera.split("\n");

                for (String wiadomosc: wiadomosciSerwera) {
                    handleSerwerMessage(dataState, wiadomosc);
                }

                Gui.updateDataState();
                cbuf.clear();

            }

        }


    }

    private static void handleSerwerMessage(DataState dataState, String odSerwera) {
        String[] split = odSerwera.split(",");
        String messageType = split[0];
        if(messageType.equals("topics")) {
            dataState.allTopics = new HashSet<>();
            dataState.allTopics.addAll(Arrays.asList(split).subList(1, split.length));
        } else if(messageType.equals("news")) {
            Map<String, List<String>> newsOnTopics = dataState.newsOnTopics;
            newsOnTopics.clear();
            String[] topicsSplit = split[1].split("\\|");
            for (String topicString : topicsSplit) {
                String[] topicSplit = topicString.split(";");
                String topicName = topicSplit[0];
                List<String> newsList = new ArrayList<>();
                newsOnTopics.put(topicName, newsList);
                newsList.addAll(Arrays.asList(topicSplit).subList(1, topicSplit.length));
            }
        }
    }
}
