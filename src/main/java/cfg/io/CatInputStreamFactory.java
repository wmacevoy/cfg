package cfg.io;

import java.io.*;
import java.util.*;
import cfg.util.*;

public class CatInputStreamFactory implements InputStreamFactory {
    protected ExceptionalIterable<InputStream,IOException> iterable;
    public CatInputStreamFactory(ExceptionalIterable<InputStream,IOException> _iterable) {
	iterable=_iterable;
    }
    
    @Override public InputStream create() throws IOException {
	return new CatInputStream(iterable.iterator());
    }

    @Override public void close() {
	iterable = null;
    }
}
