package zad1.serwer.klient;

public class Klient {

    public static void main(String[] args) {
        log("Uruchamianie...");
        Gui.start(args);
    }

    public static void log(String message) {
        System.out.println("GUI:: " + message);
    }
}
