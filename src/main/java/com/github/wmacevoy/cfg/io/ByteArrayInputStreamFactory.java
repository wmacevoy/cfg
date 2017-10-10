package cfg.io;

import java.io.*;

public class ByteArrayInputStreamFactory implements InputStreamFactory {
    byte[] data;
    int offset;
    int length;

    public ByteArrayInputStreamFactory(byte[] _data, int _offset, int _length) {
	data=_data;
	offset=_offset;
	length=_length;
	if (_offset < 0 || offset+length > (data != null ? data.length : 0)) {
	    throw new IllegalArgumentException("illegal ByteArrayInputStreamFactory(data=" + data + ",offset=" + offset + ",length = " + length +")");
	}
    }

    public ByteArrayInputStreamFactory(byte[] _data) {
	data=_data;
	offset=0;
	length=_data != null ? _data.length : 0;
    }

    @Override public InputStream create() { 
	return length > 0 ? new cfg.io.ByteArrayInputStream(data,offset,length) : NullInputStream.NULL_INPUT_STREAM;
    }

    @Override public void close() {
	data = null;
	offset = 0;
	length = 0;
    }
}
