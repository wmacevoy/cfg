package cfg;

public class InputStreamFactoryResource implements Resource {
    String name;
    private InputStreamFactory inputStreamFactory;
    public InputStreamResource(String _name,
			       InputStreamFactory _inputStreamFactory) {
	name = _name;
	inputStreamFactory=_inputStreamFactory;
    }
    public InputStream stream() { return inputStreamFactory.create(); }

    List<Resource> resources = null;
    public List<Resource> contents() {
	if (resources != null) {
	    CodeStream codeStream = 
		new InputStreamCodeStream(stream());

	    CodeStreamResourceCompiler compiler = new 
		CodeStreamResourceCompiler(codeStream);
	    compiler.compile();
	    resources=compiler.resources();
	}
	return resources;
    }
}