package cfg.io;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.jar.*;

public class JarCache {
    private TreeMap<String,Lister> cache;

    private class Lister {
	private String jarPath;
	private TreeSet<Path> list=null;
	
	public Lister(String _jarPath) {
	    jarPath=_jarPath;
	    list=null;
	}
	
	public TreeSet<Path> getList() throws IOException {
	    if (list == null) {
		synchronized(this) {
		    if (list == null) {
			catalog();
		    }
		}
	    }
	    return list;
	}

	public class UnsupportedEncodingRuntimeException extends RuntimeException {
	    public final UnsupportedEncodingException exception;
	    UnsupportedEncodingRuntimeException(UnsupportedEncodingException _exception) {
		exception=_exception;
	    }
	}

	private void catalog() throws IOException {
	    list = new TreeSet<Path>();
	    JarFile jar = null;
	    try {
		jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
	    } catch (UnsupportedEncodingException ex) {
		throw new UnsupportedEncodingRuntimeException(ex);
	    }
	    
	    Enumeration<JarEntry> entries = jar.entries();
	    Path path = new Path();
	    while (entries.hasMoreElements()) {
		String name = entries.nextElement().getName();
		if (name.endsWith(".cfg") || name.endsWith(".xml")) {
		    path.resize(0);
		    int begin = 0;
		    int sep = -1;
		    while ((sep = name.indexOf('/',begin)) != -1) {
			String part = name.substring(begin,sep);
			path.add(part);
			list.add(path.clone());
			begin=sep+1;
		    }
		    if (begin < name.length()) {
			String part = name.substring(begin);
			path.add(part);
			list.add(path.clone());
		    }
		}
	    }
	}
	
    }

    public static String getJarPath(Class clazz) {
        String me = clazz.getName().replace(".", "/")+".class";
        URL url = clazz.getClassLoader().getResource(me);
	if (url.getProtocol().equals("jar")) {
	    return url.getPath().substring(5, url.getPath().indexOf("!"));
	} else {
	    return null;
	}
    }

    SortedSet<Path> list(String jarFile, Path base) throws IOException {
	Path begin = base.clone();
	begin.normalize();
	Path end = begin.clone();
	end.add(null);
	return list(jarFile).subSet(begin,false,end,false);
    }

    TreeSet<Path> list(String jarFile) throws IOException {
	Lister lister=null;
	synchronized(this) {
	    if (cache == null) {
		cache = new TreeMap<String,Lister>();
	    }
	    lister = cache.get(jarFile);
	    if (lister == null) {
		lister = new Lister(jarFile);
		cache.put(jarFile,lister);
	    }
	}
	return lister.getList();
    }
}
