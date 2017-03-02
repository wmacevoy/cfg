package cfg.io;

import java.io.*;

public class TimeoutCachingInputStreamFactory implements InputStreamFactory {
    private InputStreamFactory factory;
    private long timeout;

    private CachingInputStreamFactory cachingFactory;
    private  long stale;
    
    public TimeoutCachingInputStreamFactory(InputStreamFactory _factory, long _timeout) {
	factory=_factory;
	timeout=_timeout;
	cachingFactory=null;
	stale=System.currentTimeMillis()-2*timeout-1;
    }

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
