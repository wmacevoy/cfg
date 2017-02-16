package cfg.functions;

import java.util.ArrayList;

public class AndGenerator implements Generator {
    ArrayList<Generator> generators = new ArrayList<Generator>();
    public AndGenerator(ArrayList<Generator> _generators) {
	generators=_generators;
    }
    public void add(Generator generator) {
	generators.add(generator);
    }

    @Override public String generate() {
	StringBuilder sb = new StringBuilder();
	for (Generator generator : generators) {
	    sb.append(generator.generate());
	}
	return sb.toString();
    }	

    @Override public String toString() {
	StringBuilder sb = new StringBuilder();
	for (Generator generator : generators) {
	    if (sb.length() > 0) { sb.append("&"); }
	    sb.append(generator.toString());
	}
	return sb.toString();
    }
}
