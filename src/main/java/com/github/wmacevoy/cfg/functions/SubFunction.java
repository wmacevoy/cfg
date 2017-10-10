package com.github.wmacevoy.cfg.functions;

import java.util.List;
import com.github.wmacevoy.cfg.Cfg;
import com.github.wmacevoy.cfg.jregex.PerlSubstitution;
import java.util.TreeMap;
import org.github.wmacevoy.cfg.jregex.Pattern;

public class SubFunction implements Function {
    Substitutions substitutions = new Substitutions();
    @Override public int args() { return 3; }
    

    @Override
        public String eval(Cfg cfg, String base, String id, List<String> args) {
        String pattern = cfg.cook(base,args.get(0));
        
        String substitution = cfg.cook(base,args.get(1));
        String target = cfg.cook(base,args.get(3));
        return substitutions.get(pattern, substitution).replace(target);
    }
}
