package cfg.io;

import java.io.InputStream;

public class NullInputStream extends InputStream {
    @Override public int available() { return 0; }
    @Override public void close() {}
    @Override public void mark(int readlimit) {}
    @Override public boolean markSupported() { return true; }
    @Override public int read() { return -1; }
    @Override public int read(byte[] b) { return b.length > 0 ? -1 : 0; }
    @Override public int read(byte[] b, int off, int len) { return len > 0 ? -1 : 0; }
    @Override public void reset() { }
    @Override public long skip(long n) { return n > 0 ? -1 : 0; }

    public static final NullInputStream NULL_INPUT_STREAM = 
	new NullInputStream();
}
