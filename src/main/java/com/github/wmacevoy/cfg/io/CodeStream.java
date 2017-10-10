package cfg.io;

import java.io.*;

public interface CodeStream extends Closeable {
    int peek(int offset) throws IOException;
    int read() throws IOException;
    int read(int data[], int offset, int length) throws IOException;
    int read(int data[]) throws IOException;
    void close() throws IOException;
}
