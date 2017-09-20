package cfg.io;

import java.io.*;

public class TimeoutCachingInputStreamFactory extends DecoratedInputStreamFactory {
    private InputStreamFactory factory;
    private long timeout;

    private CachingInputStreamFactory cachingFactory;
    private  long stale;
    
    public TimeoutCachingInputStreamFactory(InputStreamFactory _factory, long _timeout) {
	factory=_factory;
	timeout=_timeout;
	cachingFactory=null;
	stale=Long.MIN_VALUE;
    }

    @Override public void close() throws IOException {
	try {
	    if (cachingFactory != null) cachingFactory.close();
	} finally {
	    if (factory != null) factory.close();
	}
    }

    @Override protected InputStreamFactory getDecorated() { return factory; }

    @Override public InputStream create() throws IOException {
	if (timeout <= 0) {
	    return factory.create();
	} else {
	    long now = System.currentTimeMillis();
	    if (now >= stale) {
		synchronized(this) {
		    if (now >= stale) {
			cachingFactory = new CachingInputStreamFactory(factory);
			stale = now + timeout;
		    }
		}
	    }
	    return cachingFactory.create();
	}
    }
}
