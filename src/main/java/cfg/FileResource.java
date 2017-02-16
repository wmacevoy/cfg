package cfg;

import java.io.*;
import java.util.*;

class FileResource extends FileInputStreamFactory implements Resource {
    private String name = null;
    public static String getName(File file) {
	String _name = file.getName();
	if (_name.endsWith(".cfg") || _name.endsWith(".xml")) {
	    _name=_name.substring(0,_name.lastIndexOf('.'));
	}
	return _name;
    }

    @Override public String getName() {
	if (name == null) {
	    name = getName(file);
	}
	return name;
    }

    FileResource(File _file) {
	super(_file);
    }

    static class ExIterator implements ExceptionalIterator<Resource,IOException> {
	File[] files;
	int k=0;
	ExIterator(File[] _files) { files=_files; }
	@Override public boolean hasNext() { return k < files.length; }
	@Override public Resource next() {
	    int tmp=k;
	    ++k;
	    return new FileResource(files[tmp]);
	}
    }

    @Override public ExceptionalIterator<Resource,IOException> iterator() throws IOException {
	if (file.isFile()) {
	    return new InputStreamFactoryResource(getName(),this).iterator();
	}
	if (file.isDirectory()) {
	    return new ExIterator(file.listFiles());
	}
	return ResourceIterator.EMPTY;
    }
}
