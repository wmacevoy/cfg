package cfg.functions;

import java.util.List;
import java.util.ArrayList;
import cfg.Cfg;

public class KeyFunction implements Function {
    @Override public int args() { return 1; }

    @Override
        public String eval(Cfg cfg, String base, String id, List<String> args) {
	String distinct = "ABDEGHJLNQR34679";
	double bitsPerSymbol = Math.log(distinct.length())/Math.log(2.0);
        double bits = 128;

	String bitsArg = cfg.cook(base,args.get(0)).trim();
	if (bitsArg.length() != 0) {
	    bits = Double.parseDouble(bitsArg);
	}

	int symbols =(int) Math.ceil(bits/bitsPerSymbol-1e-9);
        StringBuilder pattern = new StringBuilder();
	while (symbols > 4) {
	    pattern.append("[" + distinct +"]");
	    pattern.append("{4}-");
	    symbols -= 4;
	}

	if (symbols > 0) {
	    pattern.append("[" + distinct +"]");
	    pattern.append("{" + symbols + "}");
	}

	Function random = cfg.functions.get("random");
	ArrayList<String> randomArgs = new ArrayList<String>();
	randomArgs.add(pattern.toString());
	return random.eval(cfg,base,id,randomArgs);
    }
}

