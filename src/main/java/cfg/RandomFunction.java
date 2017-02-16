package cfg;

import java.util.List;
import java.util.HashMap;

public class RandomFunction implements Function {
    RandomGenerators generators = new RandomGenerators();

    @Override public int args() { return 1; }

    @Override
        public String eval(Cfg cfg, 
			   String base, String id, List<String> args) {
        String pattern = cfg.cook(base,args.get(0)).trim();
	return generators.get(pattern).generate();
    }
}
