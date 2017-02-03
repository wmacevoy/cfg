package cfg;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.lang.reflect.*;

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

    @Test
    public void testEncryptDecrypt() {
        Cfg cfg = new Cfg("/cfg/self-test/misc");
        setenv("KEY","secret");
        assert cfg.getString("message").equals("Hello, World!");
    }

    @Test
	public void testPattern() {
        Cfg cfg = new Cfg("/cfg/self-test/misc");
	for (int i=0; i<10; ++i) {
	    String word = cfg.get("$random{[A-Za-z][A-Za-z0-9]{0,12}}");
	    System.out.println("pattern: " + word);
	}
	
    }
}