package com.github.wmacevoy.cfg;

import com.github.wmacevoy.cfg.functions.RandomGenerator;
import com.github.wmacevoy.cfg.Cfg;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class CfgTest
{
    static void setenv(String name, String value) { Cfg.ENV(name,value); }
    static String getenv(String name) {
	assertEquals(System.getenv(name),Cfg.ENV(name));
	return Cfg.ENV(name);
    }

    @Test
    public void testRealPath() {
        Cfg cfg = new Cfg("/cfg/self-test/messages");
        assertEquals(cfg.realPath("../misc/version"),"/cfg/self-test/misc/version");
    }

    @Test
    public void testRealFilePath() {
        Cfg cfg = new Cfg("file:/home/user/.project/private");
	assertEquals(cfg.realPath("../public/value"),
		     "file:/home/user/.project/public/value");
    }
                                                      
    @Test
    public void testStringParameters() {
        Cfg cfg = new Cfg("/cfg/self-test");

        System.out.println(cfg.getString("messages/hi"));
        assertEquals(cfg.getString("messages/hi"),"good morning, dave. this is version 3.14");
	if (System.getenv("HOME") == null) {
	    setenv("HOME",
		   System.getProperty("user.home"));
	}
        assertEquals(cfg.getString("misc/home"),System.getenv("HOME"));
    }

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    public File tmp = tmpFolder.getRoot();

    void tmpFile(String [] path, byte[] contents) throws Exception {
	File at = tmp;
	for (int i=0; i<path.length-1; ++i) {
	    if (at.equals(".")) continue;
	    if (at.equals("..")) throw new RuntimeException("no ..");
	    at = new File(at,path[i]);
	    at.mkdir();
	}
	at = new File(at,path[path.length-1]);
	OutputStream out = new FileOutputStream(at);
	out.write(contents);
	out.close();
    }

    void tmpFile(String[] path, String contents) throws Exception {
	tmpFile(path,contents.getBytes("UTF-8"));
    }

    void tmpFile(String path, String contents) throws Exception {
	tmpFile(path.split("|/|"),contents);
    }

    @Test
    public void testCfgFile1() throws Exception {
	tmpFile("sample/name1.cfg","value1");
	Cfg cfg = new Cfg(tmp.getCanonicalPath());
	String path = "sample/name1";
	String expect = "value1";
	String result = cfg.getString(path);
	String[] results = cfg.getStrings(path);
	assertEquals(expect,result);
        assertArrayEquals(results,new String[] { expect} );
    }
    
    @Test
    public void testCfgFile2() throws Exception {
	tmpFile("sample/name1.cfg","value1");
	Cfg cfg = new Cfg(tmp.getCanonicalPath());
	String path = "sample";
	String expect = "<name1>value1</name1>";
	String result = cfg.getString(path);
        String [] results = cfg.getStrings(path);
	assertEquals(expect,result);
        assertArrayEquals(results,new String[] { expect} );
    }

    @Test
    public void testCfgFile3() throws Exception {
	tmpFile("sample/name1.cfg","value1");
	Cfg cfg = new Cfg(tmp.getCanonicalPath());
	String path = "sample";
	String expect = "<name1>value1</name1>";
	String result = cfg.getString(path);
        String [] results = cfg.getStrings(path);
	assertEquals(expect,result);
        assertArrayEquals(results,new String[] { expect} );
    }
    
    @Test
    public void testCfgFile4() throws Exception {
	tmpFile("sample.cfg","<name1>value1</name1>");
	Cfg cfg = new Cfg(tmp.getCanonicalPath());
	String path = "sample";
	String expect = "<name1>value1</name1>";
	String result = cfg.getString(path);
        String [] results = cfg.getStrings(path);
	assertEquals(expect,result);
        assertArrayEquals(results,new String[] { expect} );
    }

    @Test
    public void testCfgFile5() throws Exception {
	tmpFile("sample.cfg","<name1>value1</name1>");
	Cfg cfg = new Cfg(tmp.getCanonicalPath());
	String path = "sample/name1";
	String expect = "value1";
	String result = cfg.getString(path);
        String [] results = cfg.getStrings(path);
	assertEquals(expect,result);
        assertArrayEquals(results,new String[] { expect} );
    }
    
    @Test
    public void testCfgUnion() throws Exception {
        tmpFile("sample.cfg","<a>1</a><a>2</a>");
        Cfg cfg = new Cfg(tmp.getCanonicalPath());
        String path = "sample/a";
        String expect = "1" +  System.lineSeparator() + "2";
        String [] expects = new String[] { "1", "2" };
        String result = cfg.getString(path);
        String [] results = cfg.getStrings(path);
	assertEquals(expect,result);
        assertArrayEquals(expects,results);
    }

    @Test
    public void testDecrypt() {
        Cfg cfg = new Cfg("/cfg/self-test/misc/db");
        String key = "AJA9-D7ED-69DN-Q7HH-B6A9-D37E-QNRR-HGA4";
        String pass = "my-db-password";
        setenv("KEY",key);
        assertEquals(cfg.getString("user"),"dbadmin");
        assertEquals(cfg.getString("pass"),pass);
    }

    @Test
    public void testGet() {
        Cfg cfg = new Cfg("/cfg/self-test/misc/db");
        setenv("KEY","secret");
        assertEquals(cfg.get("${user}:${pass}"),"dbadmin:867-5209");
    }

    @Test public void testEncryptDecrypt() {
        Cfg cfg = new Cfg("/cfg/self-test/misc");
        setenv("KEY","secret");
        assert cfg.getString("message").equals("Hello, World!");
    }

    @Test public void testRandom() {
        Cfg cfg = new Cfg("/cfg/self-test/misc");
	for (int i=0; i<10; ++i) {
	    String word = cfg.get("$random{[A-Za-z][A-Za-z0-9]{0,12}}");
	    System.out.println("pattern: " + word);
	}
    }

    @Test public void testEnv() {
        Cfg cfg = new Cfg("/cfg/self-test/misc");
	String value = cfg.get("$random{[A-Za-z][A-Za-z0-9]{0,12}}");
	Cfg.ENV("TEST_KEY",value);
	assertEquals(Cfg.ENV("TEST_KEY"),value);
	assertEquals(System.getenv("TEST_KEY"),value);
    }

    @Test public void testIsURL() {
	assertEquals(Cfg.isURL("file:/this/path.txt"),true);
	assertEquals(Cfg.isURL("file:/home/user/.project/private/../public/value"),true);
	assertEquals(Cfg.isURL("http://www.place.com/resource"),true);
	assertEquals(Cfg.isURL("https://www.place.com/resource"),true);
    }

    @Test public void testLoadFile() throws Exception {
	String prefix = "testLoadFile";
	String suffix = ".cfg";
	String value  = new RandomGenerator("[A-Za-z0-9]{32}").generate();
	File file = File.createTempFile(prefix, suffix);
	file.deleteOnExit();

	try (OutputStream out = new FileOutputStream(file);
	     PrintStream ps = new PrintStream(out)) {
		
		ps.println("<value>" + value + "</value>");
	    }
	
	String url = "file:" + file.getCanonicalPath();
	Cfg.load(url).available();
    }

    @Test public void testMount() {
	Cfg.ENV("MASTER_KEY","secret");
	/*
	Cfg cfg = new Cfg() {{
	    env("KEY",getenv("MASTER_KEY"));
	    mount("/cfg","class:/cfg");
	    mount("/home","file:$env{HOME}/.cfg");
	    mount("/master","string:$env{MASTER_KEY}");
	    mount("/private","$decrypt{$env{KEY},${/home/private}}");
	}};
	*/
	
    }

    /*
    @Test public void testLocalFile() throws Exception {
	String prefix = "testLocalFile";
	String suffix = ".cfg";
	String value  = new RandomGenerator("[A-Za-z0-9]{32}").generate();    
	File file = File.createTempFile(prefix, suffix);
	file.deleteOnExit();
	try (OutputStream out = new FileOutputStream(file);
	     PrintStream ps = new PrintStream(out)) {

		ps.println("<value>" + value + "</value>");
	    }
	String url = "file:" + file.getCanonicalPath();
	System.out.println("url: " + url);
	Cfg cfg = new Cfg(url);
	System.out.println("root: " + cfg.root);
	assertEquals(cfg.get("${value}"),value);
    }
    */
}
