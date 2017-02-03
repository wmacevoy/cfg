package cfg;

import java.util.List;

public class GetRawStringFunction implements Function {
    @Override public int args() { return 1; }

    @Override
        public String eval(Cfg cfg, String base, String id, List<String> args) {
        String arg = cfg.cook(base,args.get(0)).trim();
        if (!arg.startsWith("/")) {
            arg = base + arg;
        }
        return cfg.getRawString(arg);
    }
}
