package zad1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Futil{
  public static void processDir(String dirName, String resultFileName) {
    try {
      process(dirName, resultFileName);
    } catch (Exception e) {
       throw new RuntimeException(e);
    }

  }

  private static void process(String dirName, String resultFileName) throws IOException {
    try(RandomAccessFile randomAccessFileOutput = new RandomAccessFile(resultFileName, "rw")) {
      FileChannel fileOutputChannel = randomAccessFileOutput.getChannel();
      Files.walkFileTree(Paths.get(dirName), new MyFileVisitor(fileOutputChannel));
    }
  }
}