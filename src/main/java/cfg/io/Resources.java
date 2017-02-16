package cfg.io;

import java.io.*;
import java.util.*;

public class Resources {
    public static Resource create(String name,byte [] bytes, int offset, int length) {
        return new InputStreamFactoryResource(name,new ByteArrayInputStreamFactory(bytes,offset,length));
    }

    public static Resource create(String name,byte[] bytes) {
	return create(name,bytes,0,bytes.length);
    }

    public static Resource create(String name,String string) {
        return create(name,string.getBytes(InputStreams.CHARSET_UTF8));
    }

    public static Resource create(File file) {
	return new FileResource(file);
    }
}
