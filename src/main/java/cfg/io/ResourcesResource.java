package cfg.io;

import java.io.*;
import java.util.*;
import cfg.util.*;

public class ResourcesResource extends ResourcesInputStreamFactory implements Resource {
    String name;
    @Override public String getName() { return name; }
    @Override public ExceptionalIterator<Resource,IOException> iterator() throws IOException {
	return iterable.iterator();
    }

    private ResourcesResource(String _name, ExceptionalIterable<Resource,IOException> _iterable) {
	super(_iterable);
	name=_name;
    }
}
