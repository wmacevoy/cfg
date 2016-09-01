package cfg;

import static kiss.API.*;

import java.util.*;
import java.lang.reflect.*;
class CfgTest
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


    void testStringParameters() {
        Cfg cfg = new Cfg("/cfg/self-test");

        assert cfg.getString("messages/hi").equals("hello, world!");
        assert cfg.getString("messages/version").equals("3.14");
        assert cfg.getString("misc/home").equals(System.getenv("HOME"));
    }

    void testDecrypt() {
        Cfg cfg = new Cfg("/cfg/self-test/misc/db");
        String key = "secret";
        String pass = "867-5209";
        setenv("KEY",key);
        println("encpass: " + encrypt(key,pass));
        assert cfg.getString("user").equals("dbadmin");
        assert cfg.getString("pass").equals("867-5209");
    }

    void testGet() {
        Cfg cfg = new Cfg("/cfg/self-test/misc/db");
        setenv("KEY","secret");
        assert cfg.get("${user}:${pass}").equals("dbadmin:867-5209");
    }

    void testEncryptDecrypt() {
        Cfg cfg = new Cfg("/cfg/self-test/misc");
        setenv("KEY","secret");
        assert cfg.getString("message").equals("Hello, World!");
    }
}