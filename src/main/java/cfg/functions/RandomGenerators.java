package cfg.functions;

import java.util.HashMap;

public class RandomGenerators {
    private HashMap<String,Generator> generators = null;

    public Generator get(String pattern) {
	Generator generator = null;
	synchronized(this) {
	    if (generators == null) {
		generators = new HashMap<String,Generator>();
	    } else {
		generator = generators.get(pattern);
	    }
	}
	if (generator == null) {
	    generator = new RandomGenerator(pattern);
	    synchronized(this) {
		generators.put(pattern,generator);
	    }
	}
	return generator;
    }

}
