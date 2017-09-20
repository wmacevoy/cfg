package cfg.io;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import java.nio.charset.Charset;
import cfg.util.*;

public class TimeoutCachingInputStreamFactoryTest {
    class TFactory implements InputStreamFactory {
	@Override public InputStream create() throws IOException {
	    String s = Double.toString((System.currentTimeMillis())/1000.0);
	    return InputStreams.create(s);
	}
	@Override public void close() {}
    }

    void sleep(double seconds) {
	try { Thread.sleep((int) Math.ceil(seconds*1000)); } catch (InterruptedException ex) {}
    }

    @Test public void testNoCache() throws Exception {
	TFactory tFactory = new TFactory();
	double t0=Double.parseDouble(InputStreams.string(tFactory.create()));
	sleep(0.250);
	double t1=Double.parseDouble(InputStreams.string(tFactory.create()));
	sleep(0.250);
	double t2=Double.parseDouble(InputStreams.string(tFactory.create()));	
	assertTrue(Math.abs(t1-t0-0.250) < 0.05);
	assertTrue(Math.abs(t2-t1-0.250) < 0.05);
    }

    @Test public void testInfCache() throws Exception {
	TFactory tFactory = new TFactory();
	CachingInputStreamFactory cFactory  = new CachingInputStreamFactory(tFactory);

	double t0=Double.parseDouble(InputStreams.string(cFactory.create()));
	sleep(0.250);
	double t1=Double.parseDouble(InputStreams.string(cFactory.create()));
	sleep(0.250);
	double t2=Double.parseDouble(InputStreams.string(cFactory.create()));
	assertEquals(t0,t1,0);
	assertEquals(t1,t2,0);
    }

    @Test public void test500msCache() throws Exception {
	int ms = 500;
	TFactory tFactory = new TFactory();
	CachingInputStreamFactory cfactory  = new CachingInputStreamFactory(tFactory);
	TimeoutCachingInputStreamFactory toFactory =
	    new TimeoutCachingInputStreamFactory(tFactory,ms);

	double [] t = new double[5];
	double delay = 0.4;
	for (int i=0; i<t.length; ++i) {
	    t[i]=Double.parseDouble(InputStreams.string(toFactory.create()));
	    if (i > 0) {
		System.out.println("dt=" + (t[i]-t[0]));
	    }
	    sleep(delay);
	}
	assertEquals(t[0],t[1],0);
	assertTrue(Math.abs(t[2]-t[0]-delay*2) < 0.05);
	assertEquals(t[2],t[3],0);
	assertTrue(Math.abs(t[4]-t[0]-delay*4) < 0.05);
    }
}
