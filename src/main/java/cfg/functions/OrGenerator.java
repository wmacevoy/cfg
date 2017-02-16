package cfg.functions;

import java.util.ArrayList;

public class OrGenerator implements Generator {
    ArrayList<Generator> generators;
    public OrGenerator(ArrayList<Generator> _generators) {
	generators=_generators;
    }
    public void add(Generator generator) {
	generators.add(generator);
    }
    @Override public String generate() {
	return generators.get(RandomGenerator.random(0,generators.size()-1)).generate();
    }	

    @Override public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("(");
	for (Generator generator : generators) {
	    if (sb.length() > 1) { sb.append("|"); }
	    sb.append(generator.toString());
	}
	sb.append(")");
	return sb.toString();
    }
}
