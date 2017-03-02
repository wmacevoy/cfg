package cfg.io;

import java.util.*;
import java.io.*;
import cfg.util.*;


public class CachingInputStreamFactory implements InputStreamFactory, Closeable {
    public static final int BLOCKSIZE = 4096;

    private volatile ArrayList<byte[]> chunks = null;
    private volatile InputStream stream = null;
    private volatile boolean closed = false;
    private volatile long cached = 0;
    private Object lock = new Object();

    private InputStreamFactory factory;

    public CachingInputStreamFactory(InputStreamFactory _factory) {
        factory=_factory;
    }

    @Override public void close() throws IOException {
        synchronized(lock) {
            chunks = null;
            factory = null;
            if (stream != null) {
                InputStream _stream = stream;
                stream = null;
                _stream.close();
            }
        }
    }

    class ExIterator implements
                         ExceptionalIterator<InputStream,IOException> {
        int k = 0;
        @Override public boolean hasNext() {
            return k < (chunks != null ? chunks.size() : 0);
        }
        @Override public InputStream next() {
            int tmp = k;
            ++k;
	    int length = (int) Math.min(cached-tmp*BLOCKSIZE,BLOCKSIZE);	    
            return new cfg.io.ByteArrayInputStream(chunks.get(tmp),0,length);
        }
    }

    @Override public InputStream create() {
	return new CachingInputStream();
    }

    class CachingInputStream extends InputStream {
        long at = 0;

        private long _read(byte[] buf, int offset, long length) throws IOException {
            if (length <= 0) { return 0; }
	    long start,end;

	    synchronized(this) {
		synchronized(lock) {
		    start=at;
		    at += (int) Math.min(cached-at,length);
		    end=at;
		    
		    while (!closed && at-start < length) {
			if (stream == null) {
			    stream = factory.create();
			}
			int chunkOffset = (int) (at % BLOCKSIZE);
			int chunkIndex = (int) (at / BLOCKSIZE);
			byte[] chunk = (chunkOffset != 0) ?
			    chunks.get(chunkIndex) : new byte[BLOCKSIZE];
			int n = (int) Math.min(length-(at-start),BLOCKSIZE-chunkOffset);
			int status = stream.read(chunk,chunkOffset,n);
			if (status > 0) {
			    if (chunkOffset == 0) {
				if (chunkIndex == 0) {
				    chunks = new ArrayList<byte[]>(1);
				}
				chunks.add(chunk);
			    }
			    at += status;
			    end += status;
			    cached += status;
			} else if (status < 0) {
			    if (chunkOffset > 0) {
				chunk = Arrays.copyOf(chunk,chunkOffset);
				chunks.set(chunkIndex,chunk);
			    }
			    stream.close();
			    closed = true;
			}
		    }
		}
	    }

	    long ans = (end > start) ? end-start : -1;

	    if (buf != null) {
		while (start < end) {
		    int cpOffset = (int) (start % BLOCKSIZE);
		    int cpIndex = (int) (start / BLOCKSIZE);
		    int cpLength = (int) Math.min(end-start,BLOCKSIZE-cpOffset);
		    byte[] chunk = chunks.get(cpIndex);
		    System.arraycopy(chunks.get(cpIndex),cpOffset,buf,offset,cpLength);
		    start += cpLength;
		    offset += cpLength;
		}
	    }

	    return ans;
	}

	@Override public long skip(long length) throws IOException {
	    return _read(null,0,length);
	}

	@Override public int read(byte[] buf, int offset, int length) throws IOException {
	    return (int) _read(buf,offset,length);
	}

	@Override public int read() throws IOException {
	    int ans;
	    synchronized(this) {
		synchronized(lock) {
		    if (closed || at < cached) {
			if (at < cached) {
			    ans = 0xFF & (int) chunks.get((int)(at/BLOCKSIZE))[(int)(at%BLOCKSIZE)];
			    ++at;
			} else {
			    ans = -1;
			}
		    } else {
			if (stream == null) {
			    stream = factory.create();
			}
			ans = stream.read();
			int offset = (int) (cached % BLOCKSIZE);
			if (ans >= 0) {
			    if (offset != 0) {
				chunks.get((int)(cached/BLOCKSIZE))[(int)(cached%BLOCKSIZE)]=(byte) ans;
			    } else {
				byte[] chunk = new byte[BLOCKSIZE];
				chunk[0]=(byte) ans;
				if (cached == 0) {
				    chunks = new ArrayList<byte[]>(1);
				}
				chunks.add(chunk);
			    }
			    ++at;
			    ++cached;
			} else {
			    if (offset != 0) {
				int i=chunks.size()-1;
				chunks.set(i,Arrays.copyOf(chunks.get(i),offset));
			    }
			    stream.close();
			    closed=true;
			}
		    }
		    return ans;
		}
	    }
	}

	//	    byte [] buf = new byte[1];
	//	    if (_read(buf,0,1) == 1) { return 0xFF & (int) buf[0]; }
	//	    return -1;


	protected long marked = 0;

	@Override public boolean markSupported() { return true; }
	@Override public synchronized void mark(int readLimit) { marked = at; }
	@Override public synchronized void reset() { at = marked; }

	@Override public int available() throws IOException {
	    long ans;
	    synchronized(this) {
		synchronized(lock) {
		    ans = cached-at;
		    if (stream == null) stream = factory.create();
		    ans = cached-at+stream.available();
		}
	    }
	    return ans < Integer.MAX_VALUE ? (int) ans : Integer.MAX_VALUE;

	}
    }
}
