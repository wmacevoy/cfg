package cfg.io;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

public class JarCacheTest {
    @Test public void testListAll() throws Exception {
	JarCache jc = new JarCache();
	String jarPath = jc.getJarPath(JarCacheTest.class);
	assertNotEquals(jarPath,null);
	Iterator<Path> i = jc.list(jarPath).iterator();
	assertEquals(i.next(),new Path("cfg"));
	assertEquals(i.next(),new Path("cfg","self-test"));
	assertEquals(i.next(),new Path("cfg","self-test","messages.cfg"));
	assertEquals(i.next(),new Path("cfg","self-test","misc.cfg"));
	assertEquals(i.hasNext(),false);
    }

    @Test public void testListPart0() throws Exception {
	JarCache jc = new JarCache();
	String jarPath = jc.getJarPath(JarCacheTest.class);
	assertNotEquals(jarPath,null);
	Iterator<Path> i = jc.list(jarPath,new Path()).iterator();
	assertEquals(i.next(),new Path("cfg"));
	assertEquals(i.hasNext(),false);
    }

    @Test public void testListPart1() throws Exception {
	JarCache jc = new JarCache();
	String jarPath = jc.getJarPath(JarCacheTest.class);
	assertNotEquals(jarPath,null);
	Iterator<Path> i = jc.list(jarPath,new Path("cfg")).iterator();
	assertEquals(i.next(),new Path("cfg","self-test"));
	assertEquals(i.hasNext(),false);
    }

    @Test public void testListPart2() throws Exception {
	JarCache jc = new JarCache();
	String jarPath = jc.getJarPath(JarCacheTest.class);
	assertNotEquals(jarPath,null);
	Iterator<Path> i = jc.list(jarPath,new Path("cfg","self-test")).iterator();
	assertEquals(i.next(),new Path("cfg","self-test","messages.cfg"));
	assertEquals(i.next(),new Path("cfg","self-test","misc.cfg"));
	assertEquals(i.hasNext(),false);
    }
}


