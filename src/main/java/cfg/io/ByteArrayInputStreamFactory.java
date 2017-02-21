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
    }

    public ByteArrayInputStreamFactory(byte[] _data) {
	data=_data;
	offset=0;
	length=_data.length;
    }

    @Override public InputStream create() { 
	return new ByteArrayInputStream(data,offset,length);
    }

}