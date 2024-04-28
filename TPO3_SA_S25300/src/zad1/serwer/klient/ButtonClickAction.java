package zad1.serwer.klient;

import javafx.scene.control.Label;
import zad1.serwer.Commons;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.*;

public class ButtonClickAction implements Runnable {

    private final Label labelWynikowy;
    private final String slowo;
    private final String jezykDocelowy;

    public ButtonClickAction(Label labelWynikowy, String slowo, String jezykDocelowy) {
        this.labelWynikowy = labelWynikowy;
        this.slowo = slowo;
        this.jezykDocelowy = jezykDocelowy;
    }

    @Override
    public void run() {
        int startingPort = 8080;
        boolean znalezionoPort = false;
        ServerSocket socketDlaKlientaSlownikowego = null;
        while (!znalezionoPort) {
            try {
                socketDlaKlientaSlownikowego = new ServerSocket(startingPort);
                znalezionoPort = true;
            } catch (IOException e) {
                startingPort++;
            }
        }
        int selectedPort = startingPort;

        ServerSocket socketDlaKlientaSlownikowego2 = socketDlaKlientaSlownikowego;
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<String> watekCzekajacyNaPolaczenieOdSlownikowego = es.submit(() -> {
            try (socketDlaKlientaSlownikowego2;
                 Socket serwerSlownikowySocket = socketDlaKlientaSlownikowego2.accept();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(serwerSlownikowySocket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(serwerSlownikowySocket.getOutputStream()))) {
                String val =  reader.readLine();
                Commons.writeFlush("ok", writer);
                return val;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try (Socket socket = new Socket("127.0.0.1", 8080);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            Commons.writeFlush("{" + slowo + "," + jezykDocelowy + "," + selectedPort+ "}", writer);
            String odSerwera = reader.readLine();
            Klient.log("Wiadomosc z serwera: " + odSerwera);
            if (odSerwera.toLowerCase().contains("brak")) {
                labelWynikowy.setText(odSerwera);
            } else {
                String s = watekCzekajacyNaPolaczenieOdSlownikowego.get(2000, TimeUnit.MILLISECONDS);
                labelWynikowy.setText(s);
            }
            Commons.writeFlush("ok", writer);
            es.shutdownNow();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
