package cfg;

import java.io.*;

public class ResourcesInputStream extends CatInputStream {
    public static class ExIterator implements ExceptionalIterator<InputStream,IOException> {
	ExceptionalIterator<Resource,IOException> iterator;
	int k;
	Resource resource;

	ExIterator(ExceptionalIterator<Resource,IOException> _iterator) {
	    iterator=_iterator;
	    k=2;
	    resource=null;
	}

	@Override public boolean hasNext() throws IOException {
	    return (k < 2) || iterator.hasNext();
	}

	@Override public InputStream next() throws IOException {
	    if (++k >= 3) {
		k=0;
		resource = iterator.next();
	    }
	    switch(k) {
	    case 0:  return InputStreams.create("<" + resource.getName() + ">");
	    case 1:  return resource.create();
	    case 2:  return InputStreams.create("</" + resource.getName() + ">");
	    default: throw new IndexOutOfBoundsException();
	    }
	}
    }
    public ResourcesInputStream(ExceptionalIterator<Resource,IOException> iterator) {
	super(new ExIterator(iterator));
    }
}
