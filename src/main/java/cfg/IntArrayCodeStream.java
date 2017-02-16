package cfg;

import java.io.*;
import java.util.*;

public class IntArrayCodeStream implements CodeStream {
    private int data[];
    private int offset;
    private int length;
    private int at;

    public IntArrayCodeStream(int [] _data, int _offset, int _length) {
	data=_data;
	offset=_offset;
	length=_length;
	at=0;
    }

    public IntArrayCodeStream(int [] _data) {
	data=_data;
	offset=0;
	length=_data.length;
	at=0;
    }

    @Override public synchronized int peek(int shift) throws IOException {
	int _at = at + shift;
	return (_at < length) ? data[offset+_at] : -1;
    }

    @Override public synchronized int read() throws IOException {
	if (at < length) { 
	    int ans = data[offset+at];
	    ++at;
	    return ans;
	} else {
	    return -1;
	}
    }

    @Override public synchronized int read(int dest[], int destOffset, int destLength) throws IOException {
	int ans = 0;
	int n = Math.min(length-at,destLength);
	if (n > 0) {
	    System.arraycopy(data,offset+at,dest,destOffset,n);
	    ans += n;
	    at += n;
	}
	return ans;
    }

    @Override public synchronized int read(int data[]) throws IOException {
	return read(data,0,data.length);
    }

    @Override public void close() { }

}
