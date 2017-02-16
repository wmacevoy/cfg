package cfg;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import cfg.functions.*;
import cfg.io.*;

public class CfgTest
{
    static void setenv(String name, String value) {
       HashMap<String,String> map = new HashMap<String,String>();
        map.put(name,value);
        setenv(map);
    }

    // http://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java

    static void setenv(Map<String, String> newenv)
    {
  try
      {
          Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
          Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
          theEnvironmentField.setAccessible(true);
          Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
          env.putAll(newenv);
          Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
          theCaseInsensitiveEnvironmentField.setAccessible(true);
          Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
          cienv.putAll(newenv);
      }
  catch (NoSuchFieldException e)
      {
          try {
              Class[] classes = Collections.class.getDeclaredClasses();
              Map<String, String> env = System.getenv();
              for(Class cl : classes) {
                  if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                      Field field = cl.getDeclaredField("m");
                      field.setAccessible(true);
                      Object obj = field.get(env);
                      Map<String, String> map = (Map<String, String>) obj;
                      map.clear();
                      map.putAll(newenv);
                  }
              }
          } catch (Exception e2) {
              e2.printStackTrace();
          }
      } catch (Exception e1) {
      e1.printStackTrace();
  } 
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

    @Test
    public void testDecrypt() {
        Cfg cfg = new Cfg("/cfg/self-test/misc/db");
        String key = "secret";
        String pass = "867-5209";
        setenv("KEY",key);
        System.out.println("encpass: " + Cipher.encrypt(key,pass));
        assertEquals(cfg.getString("user"),"dbadmin");
        assertEquals(cfg.getString("pass"),"867-5209");
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
