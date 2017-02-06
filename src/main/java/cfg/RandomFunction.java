package cfg;

import java.util.List;
import java.util.HashMap;

public class PatternFunction implements Function {
    HashMap<String,Generator> patterns = new HashMap<String,Generator>();
    @Override public int args() { return 1; }

    @Override
        public String eval(Cfg cfg, String base, String id, List<String> args) {
        String pattern = cfg.cook(base,args.get(0));
	Generator generator = patterns.get(pattern);
	if (generator == null) {
	    generator = new PatternGenerator(pattern);
	    patterns.put(pattern,generator);
	}
	return generator.generate();
    }
}
