package cfg;

import java.io.*;
import java.util.*;

class ResourcesInputStreamFactory implements InputStreamFactory {
    ExceptionalIterable<Resource,IOException> iterable;
    public ResourcesInputStreamFactory(ExceptionalIterable<Resource,IOException> _iterable) {
	iterable=_iterable;
    }
    @Override public InputStream create() throws IOException {
	return new ResourcesInputStream(iterable.iterator());
    }
}
