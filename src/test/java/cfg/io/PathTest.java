package cfg.io;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import java.nio.charset.Charset;

public class PathTest {
    @Test public void testCanonicalPath() {
	Path p = new Path("this","is","a","test");
	Path q = new Path("this","is","not","..","a","test");
	Path r = new Path(q,".","..","..","another");

	assertEquals(p.toString(),"this/is/a/test");
	assertEquals(q.toString(),"this/is/not/../a/test");
	assertEquals(r.toString(),"this/is/not/../a/test/./../../another");

	Path np = new Path(p); np.normalize();
	Path nq = new Path(q); nq.normalize();
	Path nr = new Path(r); nr.normalize();
	
	assertEquals(np.toString(),"this/is/a/test");
	assertEquals(nq.toString(),"this/is/a/test");
	assertEquals(nr.toString(),"this/is/another");
    }

    @Test public void testPathOrder() {
	Path p1 = new Path("this");
	Path p2 = new Path("this","specific","path");
	Path p3 = new Path("zend");
	assertEquals(p1.compareTo(p1) == 0,true);
	assertEquals(p1.compareTo(p2) < 0,true);
	assertEquals(p1.compareTo(p3) < 0,true);
	assertEquals(p2.compareTo(p1) > 0,true);
	assertEquals(p2.compareTo(p2) == 0, true);
	assertEquals(p2.compareTo(p3) < 0, true);
	assertEquals(p3.compareTo(p1) > 0,true);
	assertEquals(p3.compareTo(p2) > 0, true);
	assertEquals(p3.compareTo(p3) == 0, true);
    }

    @Test public void testPaths() {
	TreeSet<Path> paths = new TreeSet<Path>();
	paths.add(new Path((String)null));
	paths.add(new Path("cfg"));
	paths.add(new Path("cfg",null));	
	paths.add(new Path("cfg","self-test"));
	paths.add(new Path("cfg","self-test","messages"));
	paths.add(new Path("cfg","self-test",null));	
	paths.add(new Path("cfg"));
	paths.add(new Path("cfg","self-test"));
	paths.add(new Path("cfg","self-test","misc"));
	Iterator<Path> i = paths.iterator();
	assertEquals(i.next(),new Path("cfg"));
	assertEquals(i.next(),new Path((String)null));	
	assertEquals(i.next(),new Path("cfg","self-test"));
	assertEquals(i.next(),new Path("cfg",null));	
	assertEquals(i.next(),new Path("cfg","self-test","messages"));
	assertEquals(i.next(),new Path("cfg","self-test","misc"));
	assertEquals(i.next(),new Path("cfg","self-test",null));	
	assertEquals(i.hasNext(),false);
    }

    @Test public void testPathRange1() {
	Path base = new Path("cfg","self-test");
	Path begin = base.clone();
	begin.normalize();
	Path end = begin.clone();
	end.add(null);
	assertEquals(begin.compareTo(end),-1);
    }

    @Test public void testPathEndsWith() {
	assertFalse(new Path().endsWith("a","b"));
	assertTrue(new Path("x","y","cab").endsWith("a","b"));
	assertFalse(new Path("cfg","self-test").endsWith(".cfg",".xml"));
	assertTrue(new Path("cfg","self-test","misc.xml").endsWith(".cfg",".xml"));
	assertTrue(new Path("cfg","self-test","messages.cfg").endsWith(".cfg",".xml"));
	assertFalse(new Path("cfg","self-test","messages.conf").endsWith(".cfg",".xml"));
    }
}


