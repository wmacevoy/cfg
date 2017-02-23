package cfg.functions;

import java.util.List;
import java.util.ArrayList;
import cfg.Cfg;

public class KeyFunction implements Function {
    @Override public int args() { return 1; }

    @Override
        public String eval(Cfg cfg, String base, String id, List<String> args) {
	double bits = 128;
	String bitsArg = cfg.cook(base,args.get(0)).trim();
	if (bitsArg.length() != 0) {
	    bits = Double.parseDouble(bitsArg);
	}

	return Cipher.key(bits);
    }
}

