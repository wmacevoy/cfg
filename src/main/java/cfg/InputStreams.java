package cfg;

import java.io.*;
import java.util.*;
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

    public static final InputStream create(ExceptionalFactory<InputStream,IOException>... factories) {
	return new CatInputStream(ExceptionalIterators.<InputStream,IOException>factories(factories));
    }
}
