package zad1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

class MyFileVisitor implements FileVisitor<Path> {

  private static final Charset inCharset = Charset.forName("windows-1250");
  private static final Charset targetCharset = StandardCharsets.UTF_8;
  private final FileChannel fileOutputChannel;

  MyFileVisitor(FileChannel fileOutputChannel) {
    this.fileOutputChannel = fileOutputChannel;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
    try (RandomAccessFile randomAccessFileIn = new RandomAccessFile(path.toFile(), "r")) {
      FileChannel fileInChannel = randomAccessFileIn.getChannel();
      ByteBuffer buffer = ByteBuffer.allocate((int)fileInChannel.size());
      fileInChannel.read(buffer);
      buffer.flip();
      //System.out.println("readed: " + buffer.asCharBuffer());
      ByteBuffer encodedBuffer = targetCharset.encode(inCharset.decode(buffer));
      //System.out.println(encodedBuffer.asCharBuffer());
      fileOutputChannel.write(encodedBuffer);
      return FileVisitResult.CONTINUE;
    }
  }
  @Override
  public FileVisitResult visitFileFailed(Path path, IOException exc) throws IOException {
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
    return FileVisitResult.CONTINUE;
  }
}