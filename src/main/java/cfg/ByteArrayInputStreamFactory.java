package cfg;

import java.io.*;

public class ByteArrayInputStreamFactory {
    byte[] data;
    int offset;
    int length;
    ByteArrayInputStreamFactory(byte[] _data, int _offset, int _length) {
	data=_data;
	offset=_offset;
	length=_length;
    }
    ByteArrayInputStreamFactory(byte[] _data) {
	data=_data;
	offset=0;
	length=_data.length;
    }
    @Override public InputStream create() { 
	return new ByteArrayInputStream(data,offset,length);
    }

}