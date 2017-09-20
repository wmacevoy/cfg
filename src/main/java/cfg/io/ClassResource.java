package cfg.io;

import java.io.*;
import java.util.*;
import java.net.*;
import cfg.util.*;

public class ClassResource implements Resource {
    private static JarCache cache = new JarCache();

    private String name;
    private Class clazz;
    private Path path;
    private URL url;
    private FileResource file;
    private String jar;

    @Override public String getName() { return name; }

    public ClassResource(String _name, Class _clazz, Path _path) throws IOException {
	name=_name;
	clazz=_clazz;
	path=_path.clone();
	path.normalize();
	url = clazz.getClassLoader().getResource(path.toString());

	if (url != null) {
	    switch(url.getProtocol()) {
	    case "file":
		try {
		    file = new FileResource(new File(url.toURI())); break;
		} catch (URISyntaxException ex) {
		    throw new ResourceIOException(ex);
		}
	    case "jar":  jar  = cache.getJarPath(clazz); break;
	    }
	}
    }

    class ExJarIterator implements ExceptionalIterator<Resource,IOException> {
	Iterator<Path> i;
	ExJarIterator() throws IOException {
	    i = cache.list(jar,path).iterator();
	}

	@Override public boolean hasNext() throws IOException {
	    return i.hasNext();
	}

	@Override public Resource next() throws IOException {
	    Path p = i.next();
	    if (p.endsWith(".cfg",".xml")) {
		String name = p.tail();
		name=name.substring(0,name.lastIndexOf('.'));
		InputStreamFactory factory = new ClassLoaderInputStreamFactory(clazz.getClassLoader(),p);
		return new InputStreamFactoryResource(name,factory);
	    } else {
		return new ClassResource(p.tail(),clazz,p);
	    }
	}
    }
    
    @Override public InputStream create() throws IOException {
	if (jar != null) {
	    return new ResourcesInputStream(new ExJarIterator());
	} else {
	    return file.create();
	}
	
    }

    @Override public void close() throws IOException {
	name=null;
	clazz=null;
	path=null;
	url=null;
	jar=null;
	try {
	    file.close();
	} finally {
	    file = null;
	}
    }
    
    @Override public ExceptionalIterator<Resource, IOException> iterator() throws IOException {
	if (jar != null) {
	    return new ExJarIterator();
	} else {
	    return file.iterator();
	}
    }

}
