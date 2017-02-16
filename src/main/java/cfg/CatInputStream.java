package cfg;

import java.io.*;

public class CatInputStream extends InputStream {
    private ExceptionalIterator<InputStream,IOException> iterator;
    private InputStream stream;
    private boolean eof = false;
    public CatInputStream(ExceptionalIterator<InputStream,IOException> _iterator) {
	iterator = _iterator;
	stream = NullInputStream.NULL_INPUT_STREAM;
    }

    private void nextStream() throws IOException {
	if (iterator.hasNext()) {
	    stream.close();
	    stream = iterator.next();
	} else if (!eof) {
	    stream.close();
	    stream = NullInputStream.NULL_INPUT_STREAM;
	    eof = true;
	}
    }
    
    @Override public int read() throws IOException {
	for (;;) {
	    int result = stream.read();
	    if (result != -1 || eof) {
		return result;
	    }
	    nextStream();
	}
    }
    
    @Override public int read(byte bytes[], int offset, int length) 
	throws IOException 
    {
	int result = 0;
	while (length > 0 && !eof) {
	    int partial = stream.read(bytes,offset,length);
	    if (partial > 0) {
		offset += partial;
		length -= partial;
		result += partial;
	    } else {
		nextStream();
	    }
	}
	return result != 0 ? result : (eof ? -1 : 0);
    }
	
    @Override public long skip(long length) throws IOException {
	int result = 0;
	while (length > 0 && !eof) {
	    long partial = stream.skip(length);
	    if (partial > 0) {
		length -= partial;
		result += partial;
	    } else {
		nextStream();
	    }
	}
	return result;
    }

    @Override public int available() throws IOException {
	while (!eof && stream == NullInputStream.NULL_INPUT_STREAM) {
	    nextStream();
	}
	return stream.available();
    }

    @Override public void close() throws IOException {
	stream.close();
    }
}
