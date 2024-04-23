package zad1.serwer.glowny;

import zad1.serwer.Commons;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlownyServer {

    public static final int PORT = 8080;
    public static final int MGMT_PORT = 8079;

    private static final Map<String, InetSocketAddress> serwerySlownikowe = new HashMap<>();

    public static void main(String[] args) throws IOException {
        log("Uruchamianie serwera glownego");
        ServerSocket managementServerSocket = new ServerSocket(MGMT_PORT);
        new Thread(() -> {
            while(true) {
                try {
                    Socket clientSocket = managementServerSocket.accept();
                    handleServerRegister(clientSocket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "registering servers").start();

        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        while(true) {
            Socket clientSocket = serverSocket.accept();
            executor.submit(() -> {
                log("Podlaczyl sie nowy klient");
                handleClients(clientSocket);
            });
        }
    }

    private static void handleClients(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            clientSocket){
            String clientRequest = in.readLine();
            log("Otrzymano wiadomosc od klienta: "+ clientRequest);
            clientRequest = clientRequest.replaceAll("[{}]", "").replaceAll("\"", "");
            String[] split = clientRequest.split(",");

            String polskieSlowo;
            String kodJezykaDocelowego;
            int portKlienta;

            polskieSlowo = split[0].trim();
            kodJezykaDocelowego = split[1].trim();
            portKlienta = Integer.parseInt(split[2]);

            InetSocketAddress inetSocketAddress = serwerySlownikowe.get(kodJezykaDocelowego);
            if (inetSocketAddress == null) {
                String jezykiDostepne = String.join(", ", serwerySlownikowe.keySet());
                Commons.writeFlush("Brak slownika dla jezyka: " + kodJezykaDocelowego + ". Dostepne jezyki: " + jezykiDostepne, out);
            } else {
                Commons.writeFlush("Serwer przetwarza...", out);
                try (Socket serwerSlownikowySocket = new Socket(inetSocketAddress.getAddress(), inetSocketAddress.getPort());
                     BufferedReader slownikIn = new BufferedReader(new InputStreamReader(serwerSlownikowySocket.getInputStream()));
                     BufferedWriter slownikOut = new BufferedWriter(new OutputStreamWriter(serwerSlownikowySocket.getOutputStream()))) {
                        Commons.writeFlush("{" + polskieSlowo + "," + clientSocket.getInetAddress().getHostAddress() + ", " + portKlienta + "}", slownikOut);
                         log("Przekazano wiadomosc do serwera slownikowego. Jego odpowiedz: " + slownikIn.readLine());
                     }
            }
            log("odpowiedz klienta: "+ in.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleServerRegister(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
             clientSocket){
            String input = in.readLine();
            String[] split = input.split(",");

            String ip = split[0];
            int port = Integer.parseInt(split[1]);
            String jezyk = split[2];
            serwerySlownikowe.put(jezyk, new InetSocketAddress(ip, port));
            Commons.writeFlush("ok", out);
            log("Pomyslnie zarejestrowano serwer dla jezyka: " + jezyk + " - " + ip + ":" + port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void log(String message) {
        System.out.println("GLOWNY SERVER:: " + message);
    }
}
