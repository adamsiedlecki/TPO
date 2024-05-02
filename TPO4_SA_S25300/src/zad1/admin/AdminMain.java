package zad1.admin;

import zad1.klient.KlientLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

public class AdminMain {
    public static void main(String[] args) throws IOException {

        AdminLogger.log("Uruchamianie...");
        AdminDataState dataState = new AdminDataState();
        Thread guiThread = new Thread(() -> {
            Gui.start(args, dataState); // start GUI
        });
        guiThread.start();

        SocketChannel channel = null;
        String server = "localhost";
        int port = 8080;

        try {
            // Utworzenie kanału
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(server, port));

            System.out.print("ADMIN: łączę się z serwerem ...");

            while (!channel.finishConnect()) {
                // ew. pokazywanie czasu łączenia (np. pasek postępu)
                // lub wykonywanie jakichś innych (krótkotrwałych) działań
            }

        } catch (UnknownHostException exc) {
            System.err.println("Uknown host " + server);
            // ...
        } catch (Exception exc) {
            exc.printStackTrace();
            // ...
        }

        AdminLogger.log("Polaczylem sie z serwerem");

        Charset charset = Charset.forName("ISO-8859-2");

        // Alokowanie bufora bajtowego
        // allocateDirect pozwala na wykorzystanie mechanizmów sprzętowych
        // do przyspieszenia operacji we/wy
        // Uwaga: taki bufor powinien być alokowany jednokrotnie
        // i wielokrotnie wykorzystywany w operacjach we/wy
        int rozmiar_bufora = 1024;
        ByteBuffer inBuf = ByteBuffer.allocateDirect(rozmiar_bufora);
        CharBuffer cbuf = null;


//        System.out.println("Klient: wysyłam - Hi");
        // "Powitanie" do serwera


        // pętla czytania
        while (true) {

            try {
                if (dataState.adminWantsToUpdateTopics) {
                    dataState.adminWantsToUpdateTopics = false;
                    channel.write(charset.encode("updateTopics," + String.join(",", dataState.allTopics)));
                } else if (!dataState.newsChanged.isEmpty()) {
                    Iterator<String> newsChangedIterator = dataState.newsChanged.iterator();
                    while (newsChangedIterator.hasNext()) {
                        String topic = newsChangedIterator.next();
                        List<String> news = dataState.newsOnTopics.get(topic);
                        channel.write(charset.encode("newsOnTopic," + topic + "," + String.join(",", news)));
                        newsChangedIterator.remove();
                    }
                }
            } catch (Exception e) {
                KlientLogger.log("blad podczas komunikacji z serwerem: " + e.getMessage());
                throw e;
            }


            //cbuf = CharBuffer.wrap("coś" + "\n");

            inBuf.clear();    // opróżnienie bufora wejściowego
            int readBytes = channel.read(inBuf); // czytanie nieblokujące
            // natychmiast zwraca liczbę
            // przeczytanych bajtów

            // System.out.println("readBytes =  " + readBytes);

            if (readBytes == 0) {                              // jeszcze nie ma danych
                //System.out.println("zero bajtów");

                // jakieś (krótkotrwałe) działania np. info o upływającym czasie

                continue;

            } else if (readBytes == -1) { // kanał zamknięty po stronie serwera
                // dalsze czytanie niemożlwe
                // ...
                AdminLogger.log("kanal zamknięty po stronie serwera");
                break;
            } else {        // dane dostępne w buforze
                //System.out.println("coś jest od serwera");

                inBuf.flip();    // przestawienie bufora

                // pobranie danych z bufora
                // ew. decyzje o tym czy mamy komplet danych - wtedy break
                // czy też mamy jeszcze coś do odebrania z serwera - kontynuacja
                cbuf = charset.decode(inBuf);

                String odSerwera = cbuf.toString();
                AdminLogger.log("Od serwera: " + odSerwera);
                cbuf.clear();
            }


        }
    }
}
