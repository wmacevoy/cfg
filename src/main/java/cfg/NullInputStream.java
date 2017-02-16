package cfg;

import java.io.InputStream;

public class NullInputStream extends InputStream {
    @Override public int available() { return 0; }
    @Override public void close() {}
    @Override public void mark(int readlimit) {}
    @Override public boolean markSupported() { return true; }
    @Override public int read() { return -1; }
    @Override public int read(byte[] b) { return 0; }
    @Override public int read(byte[] b, int off, int len) { return 0; }
    @Override public void reset() { }
    @Override public long skip(long n) { return 0; }

    public static final NullInputStream NULL_INPUT_STREAM = 
	new NullInputStream();
}
