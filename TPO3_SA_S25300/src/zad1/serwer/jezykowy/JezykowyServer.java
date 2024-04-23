package zad1.serwer.jezykowy;

import zad1.serwer.Commons;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class JezykowyServer {

    private static String jezyk;
    private static int port;
    private static Map<String, String> tlumaczenia = new HashMap();

    // "ANG" "9090" "kot-cat;dom-house;pies-dog;roslina-plant;energia-energy"
    // "FR" "9091" "kot-chat;dom-Maison;pies-chien;roslina-usine;energia-énergie"
    public static void main(String[] args) {
        jezyk = args[0];
        port = Integer.parseInt(args[1]);
        tlumaczenia = Arrays.stream(args[2].split(";")).collect(
                Collectors.toMap(val -> val.split("-")[0], val -> val.split("-")[1]));
        log("Uruchomiono");

        try (Socket glownyServerSocket = new Socket("127.0.0.1", 8079);
             BufferedReader in = new BufferedReader(new InputStreamReader(glownyServerSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(glownyServerSocket.getOutputStream()))) {
            Commons.writeFlush("127.0.0.1," + port + "," + jezyk, out);
            if (in.readLine().equals("ok")) {
                log("Pomyslnie zarejestrowano w glownym serwerze");
            } else {
                log("Cos poszlo nie tak przy rejestracji");
            }
        } catch (IOException e) {
            log("Nie mozna polaczyc sie z glowym serwerem w celu rejestracji :(");
            throw new RuntimeException(e);
        }

        uruchomUsluge();
    }

    private static void uruchomUsluge() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            ExecutorService executor = Executors.newFixedThreadPool(10);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> {
                    try (clientSocket;
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                         BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
                        String zapytanie = in.readLine();
                        zapytanie = zapytanie.replaceAll("[{}]", "");
                        log("Otrzymano zapytanie: " + zapytanie);
                        Commons.writeFlush("ok", out);
                        String[] split = zapytanie.split(",");
                        String polskieSlowo = split[0].trim();
                        String adresKlienta = split[1];
                        int portKlienta = Integer.parseInt(split[2].trim());
                        log("Usiluje polaczyc sie z klientem: " + adresKlienta + ":" + portKlienta);
                        try (Socket socketUdostepnionyPrzezKlienta = new Socket(adresKlienta, portKlienta);
                             BufferedReader cin = new BufferedReader(new InputStreamReader(socketUdostepnionyPrzezKlienta.getInputStream()));
                             BufferedWriter cout = new BufferedWriter(new OutputStreamWriter(socketUdostepnionyPrzezKlienta.getOutputStream()))) {
                            String tlumaczenie = tlumaczenia.get(polskieSlowo) == null ? "Nie znaleziono tlumaczenia dla tego slowa" : tlumaczenia.get(polskieSlowo);
                            log("Wysylam tlumaczenie klientowi: " + tlumaczenie);
                            Commons.writeFlush(tlumaczenie, cout);
                            if (cin.readLine().equals("ok")) {
                                log("Pomyslnie przekazano tlumaczenie klientowi");
                            } else {
                                log("Cos poszlo nie tak przy przekazywaniu tlumaczenia do klienta");
                            }
                        } catch (IOException e) {
                            log("Nie mozna polaczyc sie z glowym serwerem w celu rejestracji :(");
                            throw new RuntimeException(e);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void log(String message) {
        System.out.println(jezyk + " Jezykowy SERVER:: " + message);
    }
}
