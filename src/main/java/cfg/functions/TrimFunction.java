package cfg.functions;

import java.util.List;
import cfg.Cfg;

public class TrimFunction implements Function {
    @Override public int args() { return 1; }

    @Override
        public String eval(Cfg cfg, String base, String id, List<String> args) {
        String arg = cfg.cook(base,args.get(0)).trim();
        return arg.trim();
    }
}
