package cfg.io;

import java.io.*;
import java.util.*;
import cfg.util.*;
import java.nio.charset.Charset;

public class InputStreams {
    public static final java.nio.charset.Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    public static final NullInputStream create() {
	return NullInputStream.NULL_INPUT_STREAM;
    }

    public static final FileInputStream create(File file) throws IOException {
	return new FileInputStream(file);
    }

    public static final ByteArrayInputStream create(String data) {
	return new ByteArrayInputStream(data.getBytes(CHARSET_UTF8));
    }

    public static final ByteArrayInputStream create(byte[] bytes, int offset, int length) {
	return new ByteArrayInputStream(bytes,offset,length);
    }
    
    public static final ByteArrayInputStream create(byte[] bytes) {
	return new ByteArrayInputStream(bytes);
    }
			      
    public static final ByteArrayInputStream create(int[] codepoints, int offset, int length) {
	return create(new String(codepoints,offset,length));
    }

    public static final ByteArrayInputStream create(int[] codepoints) {
	return create(new String(codepoints,0,codepoints.length));	
    }

    @SafeVarargs
    public static final InputStream create(ExceptionalFactory<InputStream,IOException>... factories) {
	return new CatInputStream(ExceptionalIterators.<InputStream,IOException>factories(factories));
    }

    public static byte[] bytes(InputStream stream) throws IOException {
	ArrayList<byte[]> parts = new ArrayList<byte[]>();
	int length = 0;
	for (;;) {
	    byte[] bytes = new byte[1024];
	    int n = stream.read(bytes);
	    if (n <= 0) break;
	    if (n < bytes.length) {
		bytes = Arrays.copyOf(bytes,n);
	    }
	    parts.add(bytes);
	    length += bytes.length;
	}
	byte[] all = new byte[length];
	length = 0;
	for (byte[] part : parts) {
	    System.arraycopy(part,0,all,length,part.length);
	    length += part.length;
	}
	return all;
    }

    static String string(InputStream stream) throws IOException {
	return new String(bytes(stream),CHARSET_UTF8);
    }
}
