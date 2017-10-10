package cfg.io;

import java.io.*;

//
// Crazy java ByteArrayInputStream can throw IndexOutOfBounds exception
// when reading past the end instead of just truncating the read like
// every other stream.
//
public class ByteArrayInputStream extends InputStream {
    private byte[] buffer;
    private int offset;
    private int end;
    private volatile int mark;
    private volatile int at;
    
    public ByteArrayInputStream(byte[] _buffer, int _offset, int _length) {
	buffer=_buffer;
	offset=_offset;
	end=_offset+_length;
	mark=_offset;
	at=_offset;
    }

    public ByteArrayInputStream(byte[] _buffer) {
	buffer=_buffer;
	offset=0;
	end=_buffer.length;
	mark=0;
	at=offset;
    }

    @Override public int available() {
	return end-at;
    }

    @Override public void close() {
	buffer=null;
	offset=0;
	end=0;
	at=0;
	mark=0;
    }

    @Override public int read(byte[] dest, int destOffset, int length) throws IOException {
	if (length <= 0) { return 0; }
	int start;

	synchronized(this) {
	    start=at;
	    length = Math.min(at+length,end)-at;
	    at += length;
	}

	if (length > 0 && dest != null) {
	    System.arraycopy(buffer,start,dest,destOffset,length);
	}
	return length > 0 ? length : -1;
    }

    @Override public long skip(long n) throws IOException {
	if (n <= 0) return 0;
	synchronized(this) {
	    n = Math.min(at+n,end)-at;
	    at += (int) n;
	}
	return n > 0 ? n : -1;
    }

    @Override public int read() throws IOException {
	int ans;
	synchronized(this) {
	    if (at < end) {
		ans = (0xFF & (int) buffer[at]);
		++at;
	    } else {
		ans = -1;
	    }
	}
	return ans;
    }
}
