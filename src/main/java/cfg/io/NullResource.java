package cfg.io;

import cfg.util.*;
import java.io.*;

public class NullResource extends NullInputStreamFactory implements Resource {
    String name;
    public NullResource(String _name) {
	name=_name;
    }
    NullResource() {
	name = "null";
    }
    @Override public String getName() { return name; }

    public static final ExceptionalIterator<Resource, IOException> EMPTY = ExceptionalIterators.<Resource,IOException>empty();
    @Override public ExceptionalIterator<Resource, IOException> iterator() {
	return EMPTY;
    }
}
