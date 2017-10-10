package cfg.io;

import java.io.*;
import java.net.*;

public class ClassLoaderInputStreamFactory implements InputStreamFactory {
    private ClassLoader loader;
    private Path path;

    public Path[] locate() {
	Path path0 = null;
	Path path1 = null; 
	for (int i=path.size(); i>=0; --i) {
	    URL cfg = loader.getResource(path.toString(0,i)+".cfg");
	    URL xml = loader.getResource(path.toString(0,i)+".xml");
	    if (cfg != null || xml != null) {
		String name = path.get(i-1);
		if (cfg != null) name += ".cfg";
		if (xml != null) name += ".xml";
		
		path0=new Path(path.subList(0,i));
		path1=new Path(path.subList(i,path.size()));
		path0.set(i-1,name);
		break;
	    }
	}
	return new Path[] { path0, path1 };
    }

    public ClassLoaderInputStreamFactory(ClassLoader _loader, Path _path) {
	loader=_loader;
	path=_path.clone();
    }

    @Override public void close() {
	loader = null;
	path = null;
    }

    @Override public InputStream create() throws IOException {
	final Path[] paths = locate();
	if (paths[0] == null || paths[1] == null) {
	    return NullInputStream.NULL_INPUT_STREAM;
	} else if (paths[1].size() == 0) {
	    return loader.getResourceAsStream(paths[0].toString());
	} else {
	    InputStreamFactory factory =  new InputStreamFactory() {
		    @Override public InputStream create() throws IOException {
			return loader.getResourceAsStream(paths[0].toString());
		    }
		    @Override public void close() {}
		};
	    Resource resource = new InputStreamFactoryResource("tmp",factory);
	    return Resources.cd(resource,paths[1]).create();
	}
    }
}
