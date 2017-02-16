package cfg;

import java.io.*;

public interface ResourceIterable extends ExceptionalIterable<Resource,IOException> {
    public static final ResourceIterable EMPTY = new ResourceIterable() {
	    @Override public ResourceIterator iterator() { return ResourceIterator.EMPTY; }
	};
}
