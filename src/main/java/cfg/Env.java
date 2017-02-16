package cfg;

import java.util.*;
import java.lang.reflect.*;

public class Env {
    public static void setenv(String name, String value) {
	HashMap<String,String> map = new HashMap<String,String>();
	map.put(name,value);
	setenv(map);
    }

    // http://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
    public static void setenv(Map<String, String> newenv)
    {
	try {
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
	catch (NoSuchFieldException e) {
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
	
    public static String getenv(String name) { 
	return System.getenv(name);
    }

    private HashMap<String,String> local = new HashMap<String,String>();

    public String get(String name) {
	String value = null;

	synchronized(local) {
	    value = local.get(name);
	}
	if (value == null) {
	    value = Env.getenv(name);
	}
	return value;
    }

    public void set(String name, String value) {
	synchronized(local) {
	    if (value == null) {
		local.remove(name);
	    } else {
		local.put(name,value);
	    }
	}
    }
}
