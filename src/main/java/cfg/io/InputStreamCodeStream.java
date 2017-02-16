package cfg.io;

import java.io.*;
import java.util.*;

public class InputStreamCodeStream implements CodeStream {
    private InputStream is;
    private LinkedList<Integer> peeks = new LinkedList<Integer>();

    InputStreamCodeStream(InputStream _is) { is=_is; }

    public synchronized int peek(int shift) throws IOException {
	while (shift >= peeks.size()) {
	    peeks.add(_read());
	}
	return peeks.get(shift);
    }

    public synchronized int read() throws IOException {
	if (peeks.size() > 0) {
	    return peeks.removeFirst();
	} else {
	    return _read();
	}
    }

    public synchronized int read(int data[], int offset, int length) throws IOException {
	int ans = 0;
	while (length > 0) {
	    int code=read();
	    if (code >= 0) {
		data[offset]=code;
		++offset;
		--length;
		++ans;
	    } else {
		break;
	    }
	}
	return ans;
    }

    public synchronized int read(int data[]) throws IOException {
	return read(data,0,data.length);
    }

    private final int _read() throws IOException
    {
	int ans;

	for (;;) {
	    for (;;) { // loop past invalid starts
		ans = is.read();
		if ((ans & 0b10000000) == 0 || ans == -1) return ans;
		if ((ans & 0b11000000) != 0b10000000) break;
	    }
	    int extra = 0;
	    while ((ans & 0b10000000) != 0) {
		++extra;
		ans = (ans << 1);
	    }
	    ans = ((ans&0xFF) >> extra);
	    --extra;

	    while (extra > 0) {
		int part = is.read();
		if (part == -1) return -1;
		if ((part & 0b11000000) != 0b10000000) break;
		ans = (ans << 6) | (part & 0b00111111);
		--extra;
	    }
	    if (extra == 0) return ans;
	}
    }

    public void close() throws IOException { 
	if (is != null) is.close();
    }
}
