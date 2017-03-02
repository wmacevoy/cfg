package cfg.io;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import java.nio.charset.Charset;
import cfg.util.*;

public class CachingInputStreamFactoryTest {
    public static final boolean TEST_SKIP = true;
    public static final boolean TEST_READ = true;

    Random rng = new Random();
    
    class RandomFactory implements ExceptionalFactory<InputStream,IOException> {
	byte [] data;
	CachingInputStreamFactory factory;
	RandomFactory(int size) {
	    data = new byte[size];
	    rng.nextBytes(data);
	    factory = new CachingInputStreamFactory(new ByteArrayInputStreamFactory(data));
	}
	@Override public InputStream create() throws IOException {
	    return factory.create();
	}
    }

    
    class RandomReader extends Thread {
	RandomFactory factory;
	InputStream stream;
	int at;
	int nmax;
	RandomReader(RandomFactory _factory, int _nmax) {
	    factory = _factory;
	    stream = null;
	    at = 0;
	    nmax=_nmax;
	}

	@Override public void run() {
	    byte[] buf = new byte[nmax];
	    int loops = Math.max(10,10*factory.data.length/nmax);
	    boolean eof = false;
	    
	    for (int k=0; k<loops; ++k) {
		try { Thread.sleep(rng.nextInt(10)); } catch (InterruptedException ex) {}
		int n = rng.nextInt(nmax+1);
		if (stream == null) {
		    try {
			stream = factory.create();
		    } catch (IOException ex) {
			ex.printStackTrace();
			throw new Error(ex.getMessage());
		    }
		}
		int status;
		boolean skip = (TEST_SKIP && rng.nextInt(5) == 0);
		try {
		    if (skip) {
			status = (int) stream.skip(n);
			//			if (n != 0 && status == 0) {
			//			    System.out.println("n=" + n + " and status=" + status);
			//			}
			assertFalse(n != 0 && status == 0);
		    } else if (TEST_READ && n == 1 && rng.nextInt(2) == 0) {
			status = stream.read();
			if (status >= 0) { buf[0]=(byte) status; status = 1; }
			assertFalse(n != 0 && status == 0);
		    } else {
			status = stream.read(buf,0,n);
			assertFalse(n != 0 && status == 0);
		    }
		} catch (IOException ex) {
		    ex.printStackTrace();
		    throw new Error(ex.getMessage());
		}

		if (status == -1) {
		    assertEquals(at,factory.data.length);
		    eof = true;
		    break;
		} else {
		    assertFalse(n != 0 && status == 0);
		    if (!skip) for (int i=0; i<status; ++i) {
			assertEquals(buf[i],factory.data[at+i]);
		    }
		    at += status;
		}
	    }
	    assertTrue(eof);
	}
    }

    @Test public void testZero() throws Exception {
	RandomFactory factory = new RandomFactory(0);
	assertEquals(factory.create().read(),-1);
	assertEquals(factory.create().read(),-1);	
    }

    @Test public void testOne() throws Exception {
	RandomFactory factory = new RandomFactory(1);
	int code = (0xFF) & (int) factory.data[0];
	{
	    InputStream stream = factory.create();
	    assertEquals(stream.read(),code);
	    assertEquals(stream.read(),-1);
	}
	{
	    InputStream stream = factory.create();
	    assertEquals(stream.read(),code);
	    assertEquals(stream.read(),-1);
	}
    }

    @Test public void testSmall() throws Exception {
	RandomFactory factory = new RandomFactory(10);
	{
	    InputStream stream = factory.create();
	    byte [] data = new byte[factory.data.length];
	    assertEquals(stream.read(data),data.length);
	    for (int i=0; i<data.length; ++i) {
		assertEquals(data[i],factory.data[i]);
	    }
	}
	{
	    InputStream stream = factory.create();
	    byte [] data = new byte[factory.data.length];
	    assertEquals(stream.read(data),data.length);
	    for (int i=0; i<data.length; ++i) {
		assertEquals(data[i],factory.data[i]);
	    }
	}
    }
    
    @Test public void testThreadedZero() {
	RandomFactory factory = new RandomFactory(0);
	RandomReader [] readers  = new RandomReader[10];
	String prefix = "testThreadedZero-";
	for (int i=0; i<readers.length; ++i) {
	    readers[i]=new RandomReader(factory,4);
	    readers[i].setName(prefix + i);
	}
	for (int i=0; i<readers.length; ++i) {
	    readers[i].start();
	}
	for (int i=0; i<readers.length; ++i) {
	    try { readers[i].join(); } catch (Exception ex) {} 
	}
    }

    @Test public void testThreadedOne() {
	RandomFactory factory = new RandomFactory(1);
	RandomReader [] readers  = new RandomReader[10];
	String prefix = "testThreadedOne-";	
	for (int i=0; i<readers.length; ++i) {
	    readers[i]=new RandomReader(factory,4);
	    readers[i].setName(prefix + i);	    
	}
	for (int i=0; i<readers.length; ++i) {
	    readers[i].start();
	}
	for (int i=0; i<readers.length; ++i) {
	    try { readers[i].join(); } catch (Exception ex) {} 
	}
    }
    
    @Test public void testThreadedSmall() {
	for (int j=0; j<10; ++j) {
	    String prefix = "testThreadedSmall-" + j + "-";
	    RandomFactory factory = new RandomFactory(100);
	    RandomReader [] readers  = new RandomReader[10];
	    for (int i=0; i<readers.length; ++i) {
		readers[i]=new RandomReader(factory,4);
		readers[i].setName(prefix + i);
	    }
	    for (int i=0; i<readers.length; ++i) {
		readers[i].start();
	    }
	    for (int i=0; i<readers.length; ++i) {
		try { readers[i].join(); } catch (Exception ex) {} 
	    }
	}
    }

    @Test public void testThreadedLarge() {
	for (int j=0; j<10; ++j) {
	    String prefix = "testThreadedLarge-" + j + "-";	    
	    int blocks = rng.nextInt(5)+5;
	    int pm2 = rng.nextInt(5);
	    int size = CachingInputStreamFactory.BLOCKSIZE*blocks+(pm2-2);

	    RandomFactory factory = new RandomFactory(size);
	    RandomReader [] readers  = new RandomReader[10];
	    for (int i=0; i<readers.length; ++i) {
		if (i % 2 == 0) {
		    readers[i]=new RandomReader(factory,CachingInputStreamFactory.BLOCKSIZE*10);
		}
		if (i % 2 == 1) {
		    readers[i]=new RandomReader(factory,CachingInputStreamFactory.BLOCKSIZE/10);
		}
		readers[i].setName(prefix + i);		
	    }
	    for (int i=0; i<readers.length; ++i) {
		readers[i].start();
	    }
	    for (int i=0; i<readers.length; ++i) {
		try { readers[i].join(); } catch (Exception ex) {}
	    }
	}
    }
}
