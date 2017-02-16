package cfg.io;

import java.io.*;

public class NullInputStreamFactory implements InputStreamFactory {
    @Override public InputStream create() { 
	return NullInputStream.NULL_INPUT_STREAM;
    }
    public static final NullInputStreamFactory NULL_INPUT_STREAM_FACTORY 
	= new NullInputStreamFactory();
}
