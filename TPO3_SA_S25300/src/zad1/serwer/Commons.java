package zad1.serwer;

import java.io.IOException;
import java.io.Writer;

public class Commons {

    public static void writeFlush(String message, Writer writer) throws IOException {
        writer.write(message + "\n"); // \n potrzebne do readLine()
        writer.flush();
    }
}
