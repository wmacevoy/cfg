package com.github.wmacevoy.cfg.functions;

import java.util.List;
import com.github.wmacevoy.cfg.Cfg;

public class RawStringFunction implements Function {
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