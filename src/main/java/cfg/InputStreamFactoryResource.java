package cfg;

import java.io.*;
import java.util.*;

public class InputStreamFactoryResource implements Resource {
    String name;
    InputStreamFactory factory;

    public InputStreamFactoryResource(String _name,
			       InputStreamFactory _factory) {
	name = _name;
	factory=_factory;
    }

    @Override public String getName() { return name; }

    @Override public InputStream create() throws IOException {
        return factory.create();
    }

    @Override public ExceptionalIterator<Resource,IOException> iterator() throws IOException {
	CodeStream codes = new InputStreamCodeStream(create());
	CodeStreamResourceCompiler compiler = new CodeStreamResourceCompiler(codes);
	compiler.compile();
	return ExceptionalIterators.<Resource,IOException>iterator(compiler.resources.iterator());
    }
}

