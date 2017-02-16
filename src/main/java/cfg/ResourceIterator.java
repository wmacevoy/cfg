package cfg;

import java.io.*;

public interface ResourceIterator extends ExceptionalIterator<Resource,IOException> {
    public static final ResourceIterator EMPTY = new ResourceIterator() {
	    @Override public boolean hasNext() { return false; }
	    @Override public Resource next() { return null; }
	};
}
